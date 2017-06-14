package za.co.phillip.netty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.util.Logger;
import reactor.util.Loggers;

@SpringBootApplication
public class ReactorNettyApplication implements CommandLineRunner{
	
	final Logger log = Loggers.getLogger(ReactorNettyApplication.class);
	
	
	public static void main(String[] args) {
		SpringApplication.run(ReactorNettyApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		log.info("Starting up");
		
	}
}
