package virh.sense.trade.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import virh.sense.trade.domain.Product;
import virh.sense.trade.multi.ProductApplication;
import virh.sense.trade.service.ProductRepository;
import virh.sense.trade.service.ProductServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ProductApplication.class)
public class ProductServiceTest {
	
	private static Logger log = LoggerFactory.getLogger(ProductServiceTest.class);
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ProductService productService;
	
	private Product product;
	
	@Before
	public void prepareData() {
		product = new Product(null, "orange", BigDecimal.valueOf(20), 10);
		productRepository.save(product);
	}
	
	@After
	public void cleanData() {
		productRepository.deleteAll();
	}
	
	@Test
	public void test_stock_enough() {
		assertTrue(productService.checkStockEnough(product.getId(), 10L));
	}
	
	@Test
	public void test_stock_not_enough() {
		assertFalse(productService.checkStockEnough(product.getId(), 20L));
	}
}
