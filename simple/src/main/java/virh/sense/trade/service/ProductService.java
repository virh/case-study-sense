package virh.sense.trade.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Product;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductService {

	@Autowired
	ProductRepository productRepository;
	
	static Map<Long, Long> stockMap = new ConcurrentHashMap<>();
	
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
	
	public boolean checkStockAndPrepareDecrement(Long productId, Long number) {
		synchronized (stockMap) {
			boolean flag = checkStockEnough(productId, number);
			if (flag) {
				prepareStockDecrement(productId, number);
			}
			return flag;
		}
	}
	
	public void loadStock() {
		productRepository.findAll().forEach(product -> {
			stockMap.put(product.getId(), product.getNumber());
		});
	}
	
	public void clearStock() {
		stockMap.clear();
	}
}
