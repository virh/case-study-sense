package virh.sense.trade.multi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.aspect.LogExecuteTimeAspect;
import virh.sense.trade.domain.Product;
import virh.sense.trade.service.ProductRepository;
import virh.sense.trade.service.ProductServiceImpl;

@SpringBootApplication
@EntityScan(basePackageClasses=Product.class)
@EnableJpaRepositories(basePackageClasses = ProductRepository.class)
@ComponentScan(basePackageClasses= {LogExecutionTime.class, ProductServiceImpl.class, LogExecuteTimeAspect.class})
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

}
