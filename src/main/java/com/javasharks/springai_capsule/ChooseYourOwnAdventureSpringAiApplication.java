package com.javasharks.springai_capsule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChooseYourOwnAdventureSpringAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChooseYourOwnAdventureSpringAiApplication.class, args);
	}

	// image generation config
	/*
	@Bean
	ImageClient imageClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
		return new OpenAiImageClient(new OpenAiImageApi(apiKey));
	}
	*/
}
