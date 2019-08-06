package virh.sense.trade.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import virh.sense.trade.domain.Product;
import virh.sense.trade.multi.ProductApplication;
import virh.sense.trade.service.ProductRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ProductApplication.class)
public class ProductControllerTest {
	
	private static Logger log = LoggerFactory.getLogger(ProductControllerTest.class);
	
	@Autowired
	private WebApplicationContext context;
	
	MockMvc mvc;
	
	@Autowired
	ProductRepository productRepository;
	
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
	
	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void test_stock_enough() throws Exception {
		mvc.perform(get("/product/check?productId={productId}&number={number}", product.getId(), 10)).andExpect(status().isOk()).andExpect(content().string("true"));
	}
	
	@Test
	public void test_stock_not_enough() throws Exception {
		mvc.perform(get("/product/check?productId={productId}&number={number}", product.getId(), 20)).andExpect(status().isOk()).andExpect(content().string("false"));
	}
}
