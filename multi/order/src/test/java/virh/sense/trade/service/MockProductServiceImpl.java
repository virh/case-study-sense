package virh.sense.trade.service;

public class MockProductServiceImpl implements ProductService {

	@Override
	public boolean checkStockEnough(Long productId, Long number) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prepareStockDecrement(Long productId, Long number) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareStockRevert(Long productId, Long number) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long queryStock(Long productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeStockDecrement(Long productId, Long number) {
		// TODO Auto-generated method stub

	}

}
