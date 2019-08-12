package virh.sense.trade.multi;import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DubboConfig {

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("order-consume");
		return applicationConfig;
	}
	
	@Bean
	public ConsumerConfig consumerConfig() {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setTimeout(3000);
		return consumerConfig;
	}
	
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setId("dubbo");
		protocolConfig.setName("dubbo");
		protocolConfig.setPort(12345);
		return protocolConfig;
	}
	
	@Primary
	@Bean
	public RegistryConfig registryConfig(@Value("${dubbo.registry.address}") String address/*, @Value("${dubbo.registry.file}") String file*/) {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(address);
		//registryConfig.setFile(file);
		return registryConfig;
	}
}
