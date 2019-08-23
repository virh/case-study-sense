package virh.sense.trade.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import io.seata.core.context.RootContext;
import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountService;

@Service(version = "1.0.0",protocol = "${dubbo.protocol.id}",
	application = "${dubbo.application.id}",
	registry = "${dubbo.registry.id}",
	timeout = 3000)
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
		System.out.println("global transaction id ：" + RootContext.getXID());
		Account account = accountRepository.findById(accountId).get();
		account.setBalance(account.getBalance().subtract(price));
		accountRepository.save(account);
	}
	
}
