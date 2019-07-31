package virh.sense.trade.service;

import java.math.BigDecimal;

public interface AccountService {

	public boolean checkBalanceEnough(Long accountId, BigDecimal price);
	
	public void prepareBalanceDecrement(Long accountId, BigDecimal price);
	
	public void prepareBalanceRevert(Long accountId, BigDecimal price);
	
	public BigDecimal queryBalance(Long accountId);
	
	public void executeBalanceDecrement(Long accountId, BigDecimal price);
	
}
