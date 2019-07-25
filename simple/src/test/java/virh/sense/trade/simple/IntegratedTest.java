package virh.sense.trade.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.Client;
import virh.sense.trade.domain.Product;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.ClientRepository;
import virh.sense.trade.service.OrderItemRepository;
import virh.sense.trade.service.OrderRepository;
import virh.sense.trade.service.OrderService;
import virh.sense.trade.service.ProductRepository;
import virh.sense.trade.service.ProductService;
import virh.sense.trade.service.TransactionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegratedTest {
	
	private static Logger log = LoggerFactory.getLogger(IntegratedTest.class);

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	OrderService orderService;
	
	private Client client;
	private Account account;
	private Product product;
	
	@Before
	public void prepareData() {
		client = new Client(null, "Alice");
		clientRepository.save(client);
		account = new Account(null, "account", BigDecimal.valueOf(100), client);
		accountRepository.save(account);
		product = new Product(null, "orange", BigDecimal.valueOf(20), 10);
		productRepository.save(product);
	}
	
	@After
	public void cleanData() {
		orderItemRepository.deleteAll();
		orderRepository.deleteAll();
		accountRepository.deleteAll();
		clientRepository.deleteAll();
		productRepository.deleteAll();
	}
	
	@Test
	public void test_stock_not_enough() {
		assertFalse(orderService.buy(product.getId(), account.getId(), 20L, BigDecimal.valueOf(100)));
	}
	
	@Test
	public void test_balance_not_enough() {
		assertFalse(orderService.buy(product.getId(), account.getId(), 10L, BigDecimal.valueOf(200)));
	}
	
	@Test
	public void test_normal() {
		assertTrue(orderService.buy(product.getId(), account.getId(), 10L, BigDecimal.valueOf(100)));
		assertFalse(orderService.buy(product.getId(), account.getId(), 10L, BigDecimal.valueOf(100)));
		assertEquals(0, productRepository.findById(product.getId()).get().getNumber());
		assertEquals(0, accountRepository.findById(account.getId()).get().getBalance().doubleValue(), 0.001);
	}
	
	@Test
	public void test_multiple_100_thread_normal() {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
		List<Boolean> results = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch beforeBlocker = new CountDownLatch(1);
		CountDownLatch allDone = new CountDownLatch(100);
		for (int i = 0; i < 100; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						beforeBlocker.await();
						results.add(orderService.buy(product.getId(), account.getId(), 10L, BigDecimal.valueOf(100)));
						allDone.countDown();
					} catch (Throwable e) {
						exceptions.add(e);
					}
				}
			});
		}
		try {
			beforeBlocker.countDown();
			allDone.await();
		} catch (InterruptedException e) {
			fail("should not fail with allDown await");
		} finally {
			executorService.shutdown();
		}
		int success = 0;
		int fail = 0;
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i)) {
				success ++;
			} else {
				fail++;
			}
		}
		assertEquals(1, success);
		assertEquals(99, fail);
		assertEquals(0, exceptions.size());
		assertEquals(0, productRepository.findById(product.getId()).get().getNumber());
		assertEquals(0, accountRepository.findById(account.getId()).get().getBalance().doubleValue(), 0.001);
	}
	
	@Test
	public void test_multiple_10_000_thread_normal() {
		ExecutorService executorService = Executors.newFixedThreadPool(10_000);
		List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
		List<Boolean> results = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch beforeBlocker = new CountDownLatch(1);
		CountDownLatch allDone = new CountDownLatch(10_000);
		for (int i = 0; i < 10_000; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						beforeBlocker.await();
						results.add(orderService.buy(product.getId(), account.getId(), 10L, BigDecimal.valueOf(100)));
						allDone.countDown();
					} catch (Throwable e) {
						exceptions.add(e);
					}
				}
			});
		}
		try {
			long beginTime = System.currentTimeMillis();
			beforeBlocker.countDown();
			allDone.await();
			long endTime = System.currentTimeMillis();
			log.debug("[test_multiple_10_000_thread_normal] the 10_000 threads consume " + (endTime-beginTime));
		} catch (InterruptedException e) {
			fail("should not fail with allDown await");
		} finally {
			executorService.shutdown();
		}
		int success = 0;
		int fail = 0;
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i)) {
				success ++;
			} else {
				fail++;
			}
		}
		assertEquals(1, success);
		assertEquals(9_999, fail);
		assertEquals(0L, productService.queryStock(product.getId()).longValue());
		assertEquals(0, accountService.queryBalance(account.getId()).doubleValue(), 0.001);
		assertEquals(0, exceptions.size());
		assertEquals(0, productRepository.findById(product.getId()).get().getNumber());
		assertEquals(0, accountRepository.findById(account.getId()).get().getBalance().doubleValue(), 0.001);
	}

}
