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

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.Client;
import virh.sense.trade.multi.AccountApplication;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AccountApplication.class)
public class AccountServiceTest {
	
	private static Logger log = LoggerFactory.getLogger(AccountServiceTest.class);

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	AccountService accountService;
	
	private Client client;
	private Account account;
	
	@Before
	public void prepareData() {
		client = new Client(null, "Alice");
		clientRepository.save(client);
		account = new Account(null, "account", BigDecimal.valueOf(100), client);
		accountRepository.save(account);
	}
	
	@After
	public void cleanData() {
		accountRepository.deleteAll();
		clientRepository.deleteAll();
	}
	
	@Test
	public void test_balance_enough() {
		assertTrue(accountService.checkBalanceEnough(account.getId(), BigDecimal.valueOf(100)));
	}
	
	@Test
	public void test_balance_not_enough() {
		assertFalse(accountService.checkBalanceEnough(account.getId(), BigDecimal.valueOf(200)));
	}
}
