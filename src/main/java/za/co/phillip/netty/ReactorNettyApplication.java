package za.co.phillip.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import reactor.ipc.netty.NettyContext;
import reactor.util.Logger;
import reactor.util.Loggers;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAsync
@EnableScheduling
@EnableAutoConfiguration
public class ReactorNettyApplication implements CommandLineRunner{
	
	final Logger log = Loggers.getLogger(ReactorNettyApplication.class);
	
	@Autowired
	public NettyContext server;

	public static void main(String[] args) {
		SpringApplication.run(ReactorNettyApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		log.info("Starting up");
		
	}
}
