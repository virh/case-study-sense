package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.domain.OrderItem;
import virh.sense.trade.domain.SaleOrder;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AccountService accountService;
	
	private ReentrantLock lock = new ReentrantLock();
	
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
	
}
