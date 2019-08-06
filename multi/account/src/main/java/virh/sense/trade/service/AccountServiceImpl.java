package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Account;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;
	
	Map<Long, BigDecimal> priceMap = new HashMap<>();
	
	public boolean checkBalanceEnough(Long accountId, BigDecimal price) {
		if (!priceMap.containsKey(accountId)) {
			Optional<Account> account = accountRepository.findById(accountId);
			if (!account.isPresent()) {
				return false;
			}
			priceMap.put(accountId, account.get().getBalance());
		}
		return priceMap.get(accountId).compareTo(price)>=0;
	}
	
	public void prepareBalanceDecrement(Long accountId, BigDecimal price) {
		priceMap.put(accountId, priceMap.get(accountId).subtract(price));
	}
	
	public void prepareBalanceRevert(Long accountId, BigDecimal price) {
		priceMap.put(accountId, priceMap.get(accountId).add(price));
	}
	
	public BigDecimal queryBalance(Long accountId) {
		return priceMap.get(accountId);
	}

	@Override
	public void executeBalanceDecrement(Long accountId, BigDecimal price) {
		Account account = accountRepository.findById(accountId).get();
		account.setBalance(account.getBalance().subtract(price));
		accountRepository.save(account);
	}
	
}
