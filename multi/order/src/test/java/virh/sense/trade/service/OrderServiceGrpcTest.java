package virh.sense.trade.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.dubbo.config.ApplicationConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import virh.sense.trade.multi.OrderApplication;
import virh.sense.trade.service.AccountServiceGrpc.AccountServiceImplBase;
import virh.sense.trade.service.ProductServiceGrpc.ProductServiceImplBase;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"grpc.port=8001",
		"grpc.account.port=${grpc.port}",
		"grpc.product.port=${grpc.port}"
})
@ContextConfiguration(classes = OrderApplication.class)
public class OrderServiceGrpcTest extends AbstractTestExecutionListener {

	private static Logger log = LoggerFactory.getLogger(OrderServiceGrpcTest.class);

	@MockBean
	AccountServiceImplBase accountService;
	
	@MockBean
	ProductServiceImplBase productService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	ApplicationConfig applicationConfig;
	
	@Value("${grpc.port}")
	int port;
	
	private static boolean initialized = false;

	public void beforeTestClass(TestContext testContext) throws Exception {
		MockitoAnnotations.initMocks(this);
		testContext.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);

	}
	
	@Before
	public void prepareServer() throws IOException {
		if (!initialized) {
			Server server = ServerBuilder.forPort(port)
					.addService(accountService)
					.addService(productService)
					.build();
			server.start();
			initialized = true;
		}
	}

	@Before
	public void prepareData() {
		doAnswer(new Answer() {
			private int count = 0;

			public Object answer(InvocationOnMock invocation) {
				count++;
				boolean flag = true;
				if (count>=2) {
					flag = false;
				}
				StreamObserver<CheckResponse> responseObserver = invocation.getArgument(1);
				CheckResponse response = CheckResponse.newBuilder().setFlag(flag).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
				return null;
			}
		}).when(accountService).checkBalanceEnough(any(), any());
		doAnswer(new Answer() {
			private int count = 0;
			public Object answer(InvocationOnMock invocation) {
				count++;
				boolean flag = true;
				if (count>=2) {
					flag = false;
				}
				StreamObserver<CheckResponse> responseObserver = invocation.getArgument(1);
				CheckResponse response = CheckResponse.newBuilder().setFlag(flag).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
				return null;
			}
		}).when(productService).checkStockEnough(any(), any());
		Answer emptyAnswer = new Answer() {
			public Object answer(InvocationOnMock invocation) {
				StreamObserver<EmptyResponse> responseObserver = invocation.getArgument(1);
				EmptyResponse response = EmptyResponse.newBuilder().build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
				return null;
			}
		};
		doAnswer(emptyAnswer).when(accountService).prepareBalanceDecrement(any(), any());
		doAnswer(emptyAnswer).when(accountService).executeBalanceDecrement(any(), any());
		doAnswer(emptyAnswer).when(accountService).prepareBalanceRevert(any(), any());
		doAnswer(emptyAnswer).when(productService).prepareStockDecrement(any(), any());
		doAnswer(emptyAnswer).when(productService).executeStockDecrement(any(), any());
		doAnswer(emptyAnswer).when(productService).prepareStockRevert(any(), any());
	}

	@After
	public void cleanServiceConfig() {
	}
	
	@Test
	public void test_stock_not_enough() {
		doAnswer(new Answer() {
			   public Object answer(InvocationOnMock invocation) {
				   StreamObserver<CheckResponse> responseObserver = invocation.getArgument(1);
				   CheckResponse response = CheckResponse.newBuilder()
							.setFlag(false)
							.build();
					responseObserver.onNext(response);
					responseObserver.onCompleted();
					return null;
			   }})
			.when(productService).checkStockEnough(any(), any());
		assertFalse(orderService.buyGrpc(1L, 1L, 20L, BigDecimal.valueOf(100)));
	}

	@Test
	public void test_balance_not_enough() {
		doAnswer(new Answer() {
			   public Object answer(InvocationOnMock invocation) {
				   StreamObserver<CheckResponse> responseObserver = invocation.getArgument(1);
				   CheckResponse response = CheckResponse.newBuilder()
							.setFlag(false)
							.build();
					responseObserver.onNext(response);
					responseObserver.onCompleted();
					return null;
			   }})
			 .when(accountService).checkBalanceEnough(any(), any());
		assertFalse(orderService.buyGrpc(1L, 1L, 10L, BigDecimal.valueOf(200)));
	}

	@Test
	public void test_normal() {
		assertTrue(orderService.buyGrpc(1L, 1L, 100L, BigDecimal.valueOf(20)));
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
						results.add(orderService.buyGrpc(1L, 1L, 10L, BigDecimal.valueOf(100)));
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
						results.add(orderService.buyGrpc(1L, 1L, 10L, BigDecimal.valueOf(100)));
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
