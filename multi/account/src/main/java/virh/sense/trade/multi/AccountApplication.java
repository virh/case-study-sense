package virh.sense.trade.multi;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import virh.sense.trade.annotation.LogExecutionTime;
import virh.sense.trade.aspect.LogExecuteTimeAspect;
import virh.sense.trade.domain.Account;
import virh.sense.trade.service.AccountRepository;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.AccountServiceImpl;

@SpringBootApplication
@EntityScan(basePackageClasses=Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@ComponentScan(basePackageClasses= {LogExecutionTime.class, AccountServiceImpl.class, LogExecuteTimeAspect.class})
@DubboComponentScan(basePackageClasses = AccountService.class)
@Import(DubboConfig.class)
public class AccountApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AccountApplication.class).web(WebApplicationType.NONE)
			/*.listeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
	            Environment environment = event.getEnvironment();
	            int port = environment.getProperty("embedded.zookeeper.port", int.class);
	            new EmbeddedZooKeeper(port, false).start();
	        })*/.run(args);
	}

}
