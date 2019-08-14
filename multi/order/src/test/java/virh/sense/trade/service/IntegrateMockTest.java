package virh.sense.trade.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.eureka.server.InstanceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.LeaseInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;

import virh.sense.trade.multi.OrderApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, 
	properties = {"eureka.instance.hostname=localhost", 
			"eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/",
			"spring.freemarker.template-loader-path=classpath:/templates/",
			"spring.freemarker.prefer-file-system-access=false",
			"eureka.client.registerWithEureka=false",
			"eureka.client.fetchRegistry=false",
			"logging.level.com.netflix.eureka=DEBUG", 
			"logging.level.com.netflix.discovery=DEBUG",
			"eureka.datacenter=myname"})
@ContextConfiguration(classes = OrderApplication.class)
@AutoConfigureWireMock(port = 8081)
public class IntegrateMockTest {

	private static Logger log = LoggerFactory.getLogger(IntegrateMockTest.class);

	private static final String PRODUCT_NAME = "product";
	
	private static final String ACCOUNT_NAME = "account";

	private static final String HOST_NAME = "localhost";

	private static final String INSTANCE_ID = "localhost:8081";
	
	private static final int PORT = 8081;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	PeerAwareInstanceRegistry instanceRegistry;
	
	@Before
	public void prepareData() throws URISyntaxException {
		stubFor(get(urlPathMatching("/account/check")).atPriority(1)
				.inScenario("AccountScenario")
				.whenScenarioStateIs(STARTED)
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("true"))
				.willSetStateTo("FAIL"));
		stubFor(get(urlPathMatching("/product/check")).atPriority(1)
				.inScenario("ProductScenario")
				.whenScenarioStateIs(STARTED)
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("true"))
				.willSetStateTo("FAIL"));
		stubFor(get(urlPathMatching("/account/check")).atPriority(1)
				.inScenario("AccountScenario")
				.whenScenarioStateIs("FAIL")
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("false")));
		stubFor(get(urlPathMatching("/product/check")).atPriority(1)
				.inScenario("ProductScenario")
				.whenScenarioStateIs("FAIL")
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("false")));
		stubFor(get(anyUrl()).atPriority(2)
				.willReturn(ok()));
		// creating instance info
		final LeaseInfo leaseInfo = getLeaseInfo();
		final InstanceInfo productInstance = getInstanceInfo(PRODUCT_NAME, HOST_NAME,
				INSTANCE_ID, PORT, leaseInfo);
		final InstanceInfo accountInstance = getInstanceInfo(ACCOUNT_NAME, HOST_NAME,
				INSTANCE_ID, PORT, leaseInfo);
		instanceRegistry.register(productInstance, false);
		instanceRegistry.register(accountInstance, false);
//		CountDownLatch countDownLatch = new CountDownLatch(1);
//		try {
//			countDownLatch.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@After
	public void cleanServiceConfig() {
		reset();
		instanceRegistry.clearRegistry();
	}

	@Test
	public void test_stock_not_enough() {
		stubFor(get(urlPathMatching("/product/check")).atPriority(1)
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("false")));
		assertFalse(orderService.buy(1L, 1L, 20L, BigDecimal.valueOf(100)));
		verify(1, getRequestedFor(urlPathMatching("/product/check")));
	}

	@Test
	public void test_balance_not_enough() {
		stubFor(get(urlPathMatching("/account/check")).atPriority(1)
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("false")));
		assertFalse(orderService.buy(1L, 1L, 10L, BigDecimal.valueOf(200)));
		verify(1, getRequestedFor(urlPathMatching("/account/check")));
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
	
	private LeaseInfo getLeaseInfo() {
		LeaseInfo.Builder leaseBuilder = LeaseInfo.Builder.newBuilder();
		leaseBuilder.setRenewalIntervalInSecs(10);
		leaseBuilder.setDurationInSecs(15);
		return leaseBuilder.build();
	}

	private InstanceInfo getInstanceInfo(String appName, String hostName,
			String instanceId, int port, LeaseInfo leaseInfo) {
		InstanceInfo.Builder builder = InstanceInfo.Builder.newBuilder();
		builder.setAppName(appName);
		builder.setHostName(hostName);
		builder.setInstanceId(instanceId);
		builder.setPort(port);
		builder.setLeaseInfo(leaseInfo);
		try {
			builder.setIPAddr(InetAddress.getByName(hostName).getHostAddress());
		} catch (UnknownHostException e) {
			//
		}
		builder.setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn));
		return builder.build();
	}
	
	@Configuration
	@EnableAutoConfiguration
	@EnableEurekaServer
	protected static class Application {

	}
}
