package virh.sense.trade.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import virh.sense.trade.domain.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

}
