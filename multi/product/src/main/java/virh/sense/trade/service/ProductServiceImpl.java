package virh.sense.trade.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import virh.sense.trade.domain.Product;

@Service(timeout=5000, version="0.0.1")
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductRepository productRepository;
	
	Map<Long, Long> stockMap = new HashMap<>();
	
	public boolean checkStockEnough(Long productId, Long number) {
		if (!stockMap.containsKey(productId)) {
			Optional<Product> product = productRepository.findById(productId);
			if (!product.isPresent()) {
				return false;
			}
			stockMap.put(productId, product.get().getNumber());
		}
		return stockMap.get(productId) >= number;
	}
	
	
	public void prepareStockDecrement(Long productId, Long number) {
		stockMap.put(productId, stockMap.get(productId)-number);
	}
	
	public void prepareStockRevert(Long productId, Long number) {
		stockMap.put(productId, stockMap.get(productId)+number);
	}
	
	public Long queryStock(Long productId) {
		return stockMap.get(productId);
	}


	@Override
	public void executeStockDecrement(Long productId, Long number) {
		Product product = productRepository.findById(productId).get();
		product.setNumber(product.getNumber()-number);
		productRepository.save(product);
	}
}
