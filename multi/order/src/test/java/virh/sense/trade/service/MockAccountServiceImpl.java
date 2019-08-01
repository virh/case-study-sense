package virh.sense.trade.service;

import java.math.BigDecimal;

public class MockAccountServiceImpl implements AccountService {

	@Override
	public boolean checkBalanceEnough(Long accountId, BigDecimal price) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareBalanceDecrement(Long accountId, BigDecimal price) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareBalanceRevert(Long accountId, BigDecimal price) {
		// TODO Auto-generated method stub

	}

	@Override
	public BigDecimal queryBalance(Long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeBalanceDecrement(Long accountId, BigDecimal price) {
		// TODO Auto-generated method stub

	}

}
