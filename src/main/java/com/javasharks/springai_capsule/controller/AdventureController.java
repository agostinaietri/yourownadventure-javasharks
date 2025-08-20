package com.javasharks.springai_capsule.controller;

import com.javasharks.springai_capsule.service.AdventureService;
import com.javasharks.springai_capsule.service.OllamaService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/chat")
public class AdventureController {

    @Autowired
    private OllamaService aiService;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    @Autowired
    private AdventureService adventureService;

    /*
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }*/

    @GetMapping("/ask")
    public String generate(@RequestParam("promptMessage") String promptMessage) {
        //return aiService.generateResult(promptMessage);
        return chatClient.prompt()
                .user(promptMessage)
                .call()
                .content();
    }

    @GetMapping("/form")
    public String showForm() {
        return "AdventureHelper.html";
    }

    @PostMapping("/start")
    public String startStory(
            @RequestParam String genre,
            @RequestParam int numCharacters,
            @RequestParam String nameDescription,
            @RequestParam String choices,
            @RequestParam String complexity,
            @RequestParam String location,
            Model model)
    {
        ChatResponse response = adventureService.storyInitializer(genre, numCharacters, nameDescription, choices, complexity, location);

        model.addAttribute("response", response.getResult().getOutput());

        return "AdventureHelper.html";
    }

    @Configuration
    public static class ChatMemoryConfig {
        @Bean
        public ChatMemory chatMemory(ChatMemoryRepository repository) {
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(repository)
                    .maxMessages(20)
                    .build();
        }
    }
}
