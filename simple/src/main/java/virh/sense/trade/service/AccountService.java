package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Account;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountService {

	@Autowired
	AccountRepository accountRepository;
	
	static Map<Long, BigDecimal> priceMap = new ConcurrentHashMap<>();
	
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
	
	public boolean checkBalanceAndDecrement(Long accountId, BigDecimal price) {
		synchronized (priceMap) {
			boolean flag = checkBalanceEnough(accountId, price);
			if (flag) {
				prepareBalanceDecrement(accountId, price);
			}
			return flag;
		}
	}
	
	public void loadBalance() {
		accountRepository.findAll().forEach(account -> {
			priceMap.put(account.getId(), account.getBalance());
		});
	}
	
	public void clearBalance() {
		priceMap.clear();
	}
}
