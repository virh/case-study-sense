package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

}
