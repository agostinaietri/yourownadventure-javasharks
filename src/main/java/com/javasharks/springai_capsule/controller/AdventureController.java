package com.javasharks.springai_capsule.controller;

import com.javasharks.springai_capsule.StoryStatus;
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
import org.springframework.ai.image.Image;
//import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
//import org.springframework.ai.openai.OpenAiImageOptions;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class AdventureController {

    @Autowired
    private OllamaService aiService;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    /*
    @Autowired
    private ImageClient openAiImageClient;
    */

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

    /*
    // method that will be called upon generating the initial part of the story
    // the result will be associated to the modal for startStory
    public Image getImage(@PathVariable String imagePrompt){
        ImageResponse response = openAiImageClient.call(
                new ImagePrompt(imagePrompt,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(4)
                                .withHeight(1024)
                                .withWidth(1024).build())

        );
        return response.getResult().getOutput();
    }
    */

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
            @RequestParam int choices,
            @RequestParam String complexity,
            @RequestParam String location,
            Model model)
    {

        ChatResponse content = adventureService.storyInitializer(genre, numCharacters, nameDescription, choices, complexity, location);
        String response = content.getResult().getOutput().getText();

        String[] parts = response.split("Choices:", 2);

        String story = parts[0].trim();
        String[] choicesResponse = parts[1].trim().split("\n");

        // flag for front-end (hides form)
        boolean storyStarted = true;
        // updates game session
        //this.storyStatus.setStoryEnded(false);
        //this.storyStatus.setDecisions(choices);
        //this.storyStatus.storyUpdate(story);

        //Image image = getImage("Generate an according image for the introduction of the story in {story}");

        model.addAttribute("storyStarted", true);
        model.addAttribute("response", response);
        model.addAttribute("story", story);
        model.addAttribute("choicesResponse", Arrays.asList(choicesResponse));
        //model.addAttribute("image", image);

        return "AdventureHelper.html";
    }

    @PostMapping("/progress")
    public String progressStory(
            @RequestParam String story,
            @RequestParam String selectedChoice,
            Model model) {

        ChatResponse content = adventureService.storyProgress(story, selectedChoice);
        String progressResponse = content.getResult().getOutput().getText();

        String[] parts = progressResponse.split("Choices:", 2);

        String storyUpdate = parts[0].trim();
        String[] choicesResponse = parts[1].trim().split("\n");

        model.addAttribute("storyStarted", true);
        model.addAttribute("progressResponse", progressResponse);
        model.addAttribute("story", storyUpdate);
        model.addAttribute("choicesResponse", Arrays.asList(choicesResponse));

        return "AdventureHelper.html";
    }

    @PostMapping("/ending")
    public String endStory(@RequestParam String story, Model model) {
        ChatResponse content = adventureService.endStory(story);
        String endingResponse = content.getResult().getOutput().getText();

        //Image image = getImage("Generate an according image for the ending of the story in {story}");

        boolean storyEnded = true;
        model.addAttribute("storyEnded", storyEnded);
        model.addAttribute("endingResponse", endingResponse);
        //model.addAttribute("image", image);

        return "AdventureHelper.html";
    }

    /*
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
    */
}
