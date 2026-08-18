package com.kabuto.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.kabuto.cloud.dao")
public class KabutoSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KabutoSpringBootApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Profile("!prod")
	public void onApplicationReady() {
		System.out.println("\n");
		System.out.println("\033[32m" +
				"  ╔══════════════════════════════════════════════════╗\n" +
				"  ║                                                  ║\n" +
				"  ║   🚀 Application started successfully!           ║\n" +
				"  ║                                                  ║\n" +
				"  ║   📖 Swagger UI:                                 ║\n" +
				"  ║      http://localhost:8080/api/swagger-ui.html   ║\n" +
				"  ║                                                  ║\n" +
				"  ║   📄 API Docs:                                   ║\n" +
				"  ║      http://localhost:8080/api/v3/api-docs       ║\n" +
				"  ║                                                  ║\n" +
				"  ╚══════════════════════════════════════════════════╝" +
				"\033[0m");
	}

}
