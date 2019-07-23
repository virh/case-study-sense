package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

}
