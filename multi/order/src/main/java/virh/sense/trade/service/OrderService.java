package virh.sense.trade.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.domain.OrderItem;
import virh.sense.trade.domain.SaleOrder;
import virh.sense.trade.service.AccountServiceGrpc.AccountServiceBlockingStub;
import virh.sense.trade.service.ProductServiceGrpc.ProductServiceBlockingStub;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Reference(version="0.0.1", application="product-consume", url="dubbo://localhost:12346", lazy=true)
	private ProductService productService;

	@Reference(version="0.0.1", application="account-consume", url="dubbo://localhost:12345", lazy=true)
	private AccountService accountService;

	private ReentrantLock lock = new ReentrantLock();

	@Value("${grpc.account.port}")
	int accountPort;
	
	@Value("${grpc.product.port}")
	int productPort;
	
	@Value("${grpc.account.host}")
	String accountHost;
	
	@Value("${grpc.product.host}")
	String productHost;
	
	@LogExecutionTime
	public boolean buy(Long productId, Long accountId, Long number, BigDecimal price) {
		lock.lock();
		try {
			if (!productService.checkStockEnough(productId, number)) {
				return false;
			}
			if (!accountService.checkBalanceEnough(accountId, price)) {
				return false;
			}
			productService.prepareStockDecrement(productId, number);
			accountService.prepareBalanceDecrement(accountId, price);
		} finally {
			lock.unlock();
		}
		try {
			SaleOrder order = new SaleOrder();
			order.setSeq(System.currentTimeMillis() + "");
			order.setTotalPrice(price);
			OrderItem item = new OrderItem();
			item.setProductId(productId);
			orderRepository.save(order);
			orderItemRepository.save(item);
			accountService.executeBalanceDecrement(accountId, price);
			productService.executeStockDecrement(productId, number);
		} catch (Exception e) {
			lock.lock();
			try {
				productService.prepareStockRevert(productId, number);
				accountService.prepareBalanceRevert(accountId, price);
			} finally {
				lock.unlock();
			}
			return false;
		}
		return true;
	}
	
	@LogExecutionTime
	public boolean buyGrpc(Long productId, Long accountId, Long number, BigDecimal price) {
		ManagedChannel productManagedChannel = ManagedChannelBuilder
		        .forAddress(productHost, productPort).usePlaintext().build();
		ProductServiceBlockingStub productServiceBlockingStub = ProductServiceGrpc.newBlockingStub(productManagedChannel);
		ManagedChannel accountManagedChannel = ManagedChannelBuilder
		        .forAddress(accountHost, accountPort).usePlaintext().build();
		AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(accountManagedChannel);
		ProductAndNumberRequest productAndNumberRequest = ProductAndNumberRequest.newBuilder()
				.setProductId(productId)
				.setNumber(number)
				.build();
		BDecimal bDecimal = BDecimal.newBuilder()
				.setIntVal(write(price.toBigInteger()))
				.setScale(price.scale())
				.build();
		AccountAndPriceRequest accountAndPriceRequest = AccountAndPriceRequest.newBuilder().setAccountId(accountId)
				.setPrice(bDecimal)
				.build();
		lock.lock();
		try {
			
			if (!productServiceBlockingStub.checkStockEnough(productAndNumberRequest).getFlag()) {
				return false;
			}
			if (!accountServiceBlockingStub.checkBalanceEnough(accountAndPriceRequest).getFlag()) {
				return false;
			}
			productServiceBlockingStub.prepareStockDecrement(productAndNumberRequest);
			accountServiceBlockingStub.prepareBalanceDecrement(accountAndPriceRequest);
		} finally {
			lock.unlock();
		}
		try {
			SaleOrder order = new SaleOrder();
			order.setSeq(System.currentTimeMillis() + "");
			order.setTotalPrice(price);
			OrderItem item = new OrderItem();
			item.setProductId(productId);
			orderRepository.save(order);
			orderItemRepository.save(item);
			accountServiceBlockingStub.executeBalanceDecrement(accountAndPriceRequest);
			productServiceBlockingStub.executeStockDecrement(productAndNumberRequest);
		} catch (Exception e) {
			lock.lock();
			try {
				productServiceBlockingStub.prepareStockRevert(productAndNumberRequest);
				accountServiceBlockingStub.prepareBalanceRevert(accountAndPriceRequest);
			} finally {
				lock.unlock();
			}
			return false;
		}
		return true;
	}
	
	public static BInteger write(BigInteger val) {
		BInteger.Builder builder = BInteger.newBuilder();
		ByteString bytes = ByteString.copyFrom(val.toByteArray());
		builder.setValue(bytes);
		return builder.build();
	}

	public static BigInteger read(BInteger message) {
		ByteString bytes = message.getValue();
		return new BigInteger(bytes.toByteArray());
	}

}
