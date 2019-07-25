package virh.sense.trade.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Product;

@Service
public class ProductService {

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
}
