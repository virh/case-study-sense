package virh.sense.trade.multi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.aspect.LogExecuteTimeAspect;
import virh.sense.trade.domain.Order;
import virh.sense.trade.service.OrderRepository;
import virh.sense.trade.service.OrderService;

@SpringBootApplication
@EntityScan(basePackageClasses=Order.class)
@EnableJpaRepositories(basePackageClasses = OrderRepository.class)
@ComponentScan(basePackageClasses= {LogExecutionTime.class, OrderService.class, LogExecuteTimeAspect.class})
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
