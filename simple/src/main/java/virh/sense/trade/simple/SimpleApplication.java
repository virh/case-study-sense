package virh.sense.trade.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.ProductService;

@SpringBootApplication
@EntityScan(basePackageClasses=Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@ComponentScan(basePackageClasses=ProductService.class)
public class SimpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleApplication.class, args);
	}

}
