package com.example.notify_crawler;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NotifyCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyCrawlerApplication.class, args);
	}
	@Bean
	public NewTopic topic() {
		return TopicBuilder.name("topic1")
				.partitions(10)
				.replicas(1)
				.build();
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<String, String> template) {
		return args -> {
			template.send("topic1", "test");
		};
	}

}
