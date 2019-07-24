package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Account;

@Service
public class AccountService {

	@Autowired
	AccountRepository accountRepository;
	
	public boolean checkBalanceEnough(Long accountId, BigDecimal price) {
		Optional<Account> account = accountRepository.findById(accountId);
		return account.isPresent() && account.get().getBalance().compareTo(price)>=0;
	}
	
}
