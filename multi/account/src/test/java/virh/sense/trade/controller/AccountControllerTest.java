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

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.Client;
import virh.sense.trade.multi.AccountApplication;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.ClientRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AccountApplication.class)
public class AccountControllerTest {
	
	private static Logger log = LoggerFactory.getLogger(AccountControllerTest.class);

	@Autowired
	private WebApplicationContext context;
	
	MockMvc mvc;
	
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
	
	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void test_balance_enough() throws Exception {
		mvc.perform(get("/account/check?accountId={accountId}&price={price}", account.getId(), 100)).andExpect(status().isOk()).andExpect(content().string("true"));
	}
	
	@Test
	public void test_balance_not_enough() throws Exception {
		mvc.perform(get("/account/check?accountId={accountId}&price={price}",  account.getId(), 200)).andExpect(status().isOk()).andExpect(content().string("false"));
	}
}
