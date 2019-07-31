package virh.sense.trade.service;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import virh.sense.trade.multi.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ProductApplication.class)
public class ProductRepositoryTest {

	@Autowired
	ProductRepository productRepository;
	
	@Test
	public void testDataSource() {
		assertFalse(productRepository.existsById(1l));
	}
}
