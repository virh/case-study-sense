package virh.sense.trade.simple;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.Client;
import virh.sense.trade.domain.Product;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.ClientRepository;
import virh.sense.trade.service.OrderRepository;
import virh.sense.trade.service.ProductRepository;
import virh.sense.trade.service.ProductService;
import virh.sense.trade.service.TransactionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegratedTest {

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	AccountService accountService;
	
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
		accountRepository.deleteAll();
		clientRepository.deleteAll();
		productRepository.deleteAll();
	}
	
	@Test
	public void test_stock_not_enough() {
		assertFalse(productService.checkStockEnough(product.getId(), 20l));
	}
	
	@Test
	public void test_balance_not_enough() {
		assertFalse(accountService.checkBalanceEnough(account.getId(), BigDecimal.valueOf(200)));
	}
	
	@Test
	public void test_normal() {
	}

}
