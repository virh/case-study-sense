package virh.sense.trade.multi;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.grpc.Server;
import io.grpc.ServerBuilder;
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

	@Autowired
	AccountServiceImpl accountService;
	
	@Value("${grpc.port}")
	int port;
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(AccountApplication.class).web(WebApplicationType.NONE).run(args);
	}

	
	@PostConstruct
	void postExecute() throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(port).addService(accountService).build();
		server.start();
	}
}
