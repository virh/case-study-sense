package virh.sense.trade.service;

public interface ProductService {

	public boolean checkStockEnough(Long productId, Long number);
	
	public void prepareStockDecrement(Long productId, Long number);
	
	public void prepareStockRevert(Long productId, Long number);
	
	public Long queryStock(Long productId);
	
	public void executeStockDecrement(Long productId, Long number);
	
}
