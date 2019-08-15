package virh.sense.trade.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountServiceGrpc.AccountServiceImplBase;

@Service(timeout=5000, version="0.0.1")
public class AccountServiceImpl extends AccountServiceImplBase implements AccountService {

	@Autowired
	AccountRepository accountRepository;
	
	Map<Long, BigDecimal> priceMap = new HashMap<>();
	
	public boolean checkBalanceEnough(Long accountId, BigDecimal price) {
		if (!priceMap.containsKey(accountId)) {
			Optional<Account> account = accountRepository.findById(accountId);
			if (!account.isPresent()) {
				return false;
			}
			priceMap.put(accountId, account.get().getBalance());
		}
		return priceMap.get(accountId).compareTo(price)>=0;
	}
	
	public void prepareBalanceDecrement(Long accountId, BigDecimal price) {
		priceMap.put(accountId, priceMap.get(accountId).subtract(price));
	}
	
	public void prepareBalanceRevert(Long accountId, BigDecimal price) {
		priceMap.put(accountId, priceMap.get(accountId).add(price));
	}
	
	public BigDecimal queryBalance(Long accountId) {
		return priceMap.get(accountId);
	}

	@Override
	public void executeBalanceDecrement(Long accountId, BigDecimal price) {
		Account account = accountRepository.findById(accountId).get();
		account.setBalance(account.getBalance().subtract(price));
		accountRepository.save(account);
	}

	@Override
	public void checkBalanceEnough(AccountAndPriceRequest request, StreamObserver<CheckResponse> responseObserver) {
		BigDecimal price = retrievePrice(request);
		boolean flag = checkBalanceEnough(request.getAccountId(), price);
		CheckResponse response = CheckResponse.newBuilder()
				.setFlag(flag)
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	private BigDecimal retrievePrice(AccountAndPriceRequest request) {
		BigDecimal price = new BigDecimal(read(request.getPrice().getIntVal()), request.getPrice().getScale());
		return price;
	}

	@Override
	public void prepareBalanceDecrement(AccountAndPriceRequest request,
			StreamObserver<EmptyResponse> responseObserver) {
		BigDecimal price = retrievePrice(request);
		prepareBalanceDecrement(request.getAccountId(), price);
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void prepareBalanceRevert(AccountAndPriceRequest request, StreamObserver<EmptyResponse> responseObserver) {
		BigDecimal price = retrievePrice(request);
		prepareBalanceRevert(request.getAccountId(), price);
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void queryBalance(AccountRequest request, StreamObserver<AccountQueryResponse> responseObserver) {
		BigDecimal price = queryBalance(request.getAccountId());
		AccountQueryResponse response = AccountQueryResponse.newBuilder()
				.setPrice(
						BDecimal.newBuilder()
							.setScale(price.scale())
							.setIntVal(write(price.toBigInteger())))
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void executeBalanceDecrement(AccountAndPriceRequest request,
			StreamObserver<EmptyResponse> responseObserver) {
		BigDecimal price = retrievePrice(request);
		executeBalanceDecrement(request.getAccountId(), price);
		EmptyResponse response = EmptyResponse.newBuilder()
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	BInteger write(BigInteger val) {
		BInteger.Builder builder = BInteger.newBuilder();
		ByteString bytes = ByteString.copyFrom(val.toByteArray());
		builder.setValue(bytes);
		return builder.build();
	}

	BigInteger read(BInteger message) {
		ByteString bytes = message.getValue();
		return new BigInteger(bytes.toByteArray());
	}
	
}
