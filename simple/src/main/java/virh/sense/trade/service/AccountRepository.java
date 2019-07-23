package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.Account;
import virh.sense.trade.domain.Client;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

}
