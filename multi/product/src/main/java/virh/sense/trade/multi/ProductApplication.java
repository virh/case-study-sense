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
import virh.sense.trade.domain.Product;
import virh.sense.trade.service.AccountService;
import virh.sense.trade.service.ProductRepository;
import virh.sense.trade.service.ProductServiceImpl;

@SpringBootApplication
@EntityScan(basePackageClasses=Product.class)
@EnableJpaRepositories(basePackageClasses = ProductRepository.class)
@ComponentScan(basePackageClasses= {LogExecutionTime.class, ProductServiceImpl.class, LogExecuteTimeAspect.class})
@DubboComponentScan(basePackageClasses = AccountService.class)
@Import(DubboConfig.class)
public class ProductApplication {

	@Autowired
	ProductServiceImpl productService;
	
	@Value("${grpc.port}")
	int port;
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).web(WebApplicationType.NONE).run(args);
	}

	@PostConstruct
	void postExecute() throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(port).addService(productService).build();
		server.start();
	}
}
