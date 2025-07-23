package fr.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserBatchAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserBatchAppApplication.class, args);
	}

}
