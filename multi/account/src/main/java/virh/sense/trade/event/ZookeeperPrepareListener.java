package virh.sense.trade.event;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import virh.sense.trade.multi.EmbeddedZooKeeper;

public class ZookeeperPrepareListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		Environment environment = event.getEnvironment();
        int port = environment.getProperty("embedded.zookeeper.port", int.class);
        new EmbeddedZooKeeper(port, false).start();
	}

}
