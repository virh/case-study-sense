package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.OrderItem;
import virh.sense.trade.domain.Product;
import virh.sense.trade.domain.SaleOrder;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderService {
	
	private static Logger log = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AccountService accountService;
	
	private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public boolean buy(Long productId, Long accountId, Long number, BigDecimal price) {
		if (!productService.checkStockAndPrepareDecrement(productId, number)) {
			return false;
		}
		if (!accountService.checkBalanceAndDecrement(accountId, price)) {
			productService.prepareStockRevert(productId, number);
			return false;
		}
		long beginTime = System.currentTimeMillis();
		executorService.submit(new Runnable() {
			public void run() {
				try {
					SaleOrder order = new SaleOrder();
					order.setSeq(System.currentTimeMillis() + "");
					order.setTotalPrice(price);
					OrderItem item = new OrderItem();
					Product product = productRepository.findById(productId).get();
					Account account = accountRepository.findById(accountId).get();
					item.setProduct(product);
					orderRepository.save(order);
					orderItemRepository.save(item);
					product.setNumber(product.getNumber()-number);
					account.setBalance(account.getBalance().subtract(price));
					productRepository.save(product);
					accountRepository.save(account);
				} catch (Exception e) {
					productService.prepareStockRevert(productId, number);
					accountService.prepareBalanceRevert(accountId, price);
				}				
			}
		});
		long endTime = System.currentTimeMillis();
		log.debug("[OrderService.buy] new thread consume " + (endTime-beginTime));
		return true;
	}
	
}
