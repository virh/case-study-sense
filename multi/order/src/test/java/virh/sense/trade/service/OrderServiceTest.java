package virh.sense.trade.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import virh.sense.trade.multi.OrderApplication;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = OrderApplication.class)
public class OrderServiceTest {

	private static Logger log = LoggerFactory.getLogger(OrderServiceTest.class);

	private ApplicationConfig accountApplication = new ApplicationConfig("account-test");
	private ApplicationConfig productApplication = new ApplicationConfig("product-test");
	private RegistryConfig registryNA = new RegistryConfig("N/A");
	private ProtocolConfig protocolAccountDubo12345 = new ProtocolConfig("dubbo", 12345);
	private ProtocolConfig protocolProductDubo12346 = new ProtocolConfig("dubbo", 12346);

	private ServiceConfig<AccountService> accountServiceConfig;
	private ServiceConfig<ProductService> productServiceConfig;
	
	@MockBean
	AccountService accountService;
	
	@MockBean
	ProductService productService;
	
	@Autowired
	OrderService orderService;

	@Before
	public void prepareData() {
		accountServiceConfig = exportAccountService();
		productServiceConfig = exportProductService();
		when(accountService.checkBalanceEnough(anyLong(), any())).thenReturn(true, false);
		when(productService.checkStockEnough(anyLong(), anyLong())).thenReturn(true, false);
	}

	@After
	public void cleanServiceConfig() {
		accountServiceConfig.unexport();
		productServiceConfig.unexport();
	}
	
	private ServiceConfig<AccountService> exportAccountService() {
		ServiceConfig<AccountService> service = new ServiceConfig<AccountService>();
        service.setApplication(accountApplication);
        service.setRegistry(registryNA);
        service.setProtocol(protocolAccountDubo12345);
        service.setInterface(AccountService.class.getName());
        service.setRef(accountService);
        service.setValidation(String.valueOf(true));
        service.export();
        return service;
	}
	
	private ServiceConfig<ProductService> exportProductService() {
		ServiceConfig<ProductService> service = new ServiceConfig<ProductService>();
        service.setApplication(productApplication);
        service.setRegistry(registryNA);
        service.setProtocol(protocolProductDubo12346);
        service.setInterface(AccountService.class.getName());
        service.setRef(productService);
        service.setValidation(String.valueOf(true));
        service.export();
        return service;
	}
	
	@Test
	public void test_stock_not_enough() {
		when(productService.checkStockEnough(anyLong(), anyLong())).thenReturn(false);
		assertFalse(orderService.buy(1L, 1L, 20L, BigDecimal.valueOf(100)));
	}

	@Test
	public void test_balance_not_enough() {
		when(accountService.checkBalanceEnough(anyLong(), any())).thenReturn(false);
		assertFalse(orderService.buy(1L, 1L, 10L, BigDecimal.valueOf(200)));
	}

	@Test
	public void test_normal() {
		assertTrue(orderService.buy(1L, 1L, 100L, BigDecimal.valueOf(20)));
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
						results.add(orderService.buy(1L, 1L, 10L, BigDecimal.valueOf(100)));
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
				success++;
			} else {
				fail++;
			}
		}
		assertEquals(1, success);
		assertEquals(99, fail);
		assertEquals(0, exceptions.size());
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
						results.add(orderService.buy(1L, 1L, 10L, BigDecimal.valueOf(100)));
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
			log.debug("[test_multiple_10_000_thread_normal] the 10_000 threads consume " + (endTime - beginTime));
		} catch (InterruptedException e) {
			fail("should not fail with allDown await");
		} finally {
			executorService.shutdown();
		}
		int success = 0;
		int fail = 0;
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i)) {
				success++;
			} else {
				fail++;
			}
		}
		assertEquals(1, success);
		assertEquals(9_999, fail);
		assertEquals(0, exceptions.size());
	}
}
