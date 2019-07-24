package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.Product;
import virh.sense.trade.domain.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

}
