package com.javasharks.springai_capsule.service;

import com.javasharks.springai_capsule.StoryStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdventureService {
    private final ChatClient chatClient;
    private StoryStatus storyStatus = new StoryStatus();

    @Autowired
    public AdventureService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public ChatResponse storyInitializer(String genre, int numCharacters, String nameDescription, String choices, String complexity, String location) {
        String template =
                "You're an interactive storyteller."
                + "Start a story with the following parameters: "
                + "Genre: {genre}"
                + "Number of characters: {numCharacters}"
                + "Description and name of main character: {nameDescription}"
                + "Total choices in the story: {choices}"
                + "Number of choices per turn: {complexity}"
                + "Location where the story takes place: {location}";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("genre", genre, "numCharacters", numCharacters, "nameDescription", nameDescription,
                        "choices", choices, "complexity", complexity, "location", location));

        return chatClient.prompt(prompt).call().chatResponse();
    }
    
    public ChatResponse storyProgress(String story, int health, int stamina, String mental, int decisionsLeft) {

        String template = "You're narrating a choose your own adventure story."
                + "Context:"
                + "-Story: {story}\n"
                + "-Character status: Health: {health}, stamina: {stamina}\n"
                + "-Decisions remaining: {decisions}.\n"
                + "Task:\n"
                + "-Continue the story from the context.\n"
                + "-Describe what follows.\n"
                + "Generate exactly {choices} choices for the player. Each choice must be distinct and relevant";

        storyStatus.UpdateDecisions();

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("story", story, "health", health, "stamina", stamina, "mental", mental,
                        "decisionsLeft", decisionsLeft));

        return chatClient.prompt(prompt).call().chatResponse();
    }
}
