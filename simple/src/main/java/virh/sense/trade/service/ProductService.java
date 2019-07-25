package virh.sense.trade.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import virh.sense.trade.domain.Product;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;
	
	public boolean checkStockEnough(Long productId, Long number) {
		Optional<Product> product = productRepository.findById(productId);
		return product.isPresent() && product.get().getNumber() >= number;
	}
	
}
