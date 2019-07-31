package virh.sense.trade.multi;import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
