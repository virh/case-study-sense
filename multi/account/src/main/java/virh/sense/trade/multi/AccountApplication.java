package virh.sense.trade.multi;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.aspect.LogExecuteTimeAspect;
import virh.sense.trade.controller.AccountController;
import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountServiceImpl;

@SpringBootApplication
@EntityScan(basePackageClasses=Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@EnableEurekaServer
@ComponentScan(basePackageClasses= {LogExecutionTime.class, AccountServiceImpl.class, LogExecuteTimeAspect.class, AccountController.class})
public class AccountApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AccountApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

}
