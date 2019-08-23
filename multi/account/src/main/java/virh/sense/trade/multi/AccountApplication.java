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
import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountServiceImpl;

@SpringBootApplication
@EntityScan(basePackageClasses=Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@ComponentScan(basePackageClasses= {LogExecutionTime.class, LogExecuteTimeAspect.class})
@DubboComponentScan(basePackageClasses = AccountServiceImpl.class)
@Import({DubboConfig.class, SetaAutoConfig.class})
@EnableDiscoveryClient
public class AccountApplication {

	public static void main(String[] args) {
		//new SpringApplicationBuilder(AccountApplication.class).web(WebApplicationType.NONE).run(args);
		SpringApplication.run(AccountApplication.class, args);
	}

}
