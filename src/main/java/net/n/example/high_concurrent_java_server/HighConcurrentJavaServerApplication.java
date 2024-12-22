package net.n.example.high_concurrent_java_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HighConcurrentJavaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighConcurrentJavaServerApplication.class, args);
	}

}
