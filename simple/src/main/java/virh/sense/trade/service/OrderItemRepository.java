package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.OrderItem;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}
