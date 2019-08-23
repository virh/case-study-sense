package virh.sense.trade.multi;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.aspect.LogExecuteTimeAspect;
import virh.sense.trade.domain.Order;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.OrderRepository;
import virh.sense.trade.service.OrderService;

@SpringBootApplication
@EntityScan(basePackageClasses = Order.class)
@EnableJpaRepositories(basePackageClasses = OrderRepository.class)
@ComponentScan(basePackageClasses = { LogExecutionTime.class, OrderService.class, LogExecuteTimeAspect.class })
@DubboComponentScan(basePackageClasses = AccountService.class)
@Import({DubboConfig.class, SetaAutoConfig.class})
@EnableDiscoveryClient
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
