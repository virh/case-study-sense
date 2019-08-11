package virh.sense.trade.multi;import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfig {

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("product-provider");
		return applicationConfig;
	}
	
	@Bean
	public RegistryConfig registryConfig(@Value("${dubbo.registry.address}") String address) {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(address);
		return registryConfig;
	}
	
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setId("dubbo");
		protocolConfig.setName("dubbo");
		protocolConfig.setPort(12346);
		return protocolConfig;
	}
}
