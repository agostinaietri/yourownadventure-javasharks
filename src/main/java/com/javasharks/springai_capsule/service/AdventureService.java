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


    //setting up memory advisor so that the story and choices are remembered
    @Autowired
    public AdventureService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public ChatResponse storyInitializer(String genre, int numCharacters, String nameDescription, int choices, String complexity, String location) {
        String template =
                "You're an interactive storyteller."
                + "Start a story with the following parameters (output it with Story:)"
                + "Genre: {genre}"
                + "Number of characters: {numCharacters}"
                + "Description and name of main character: {nameDescription}"
                + "Total choices in the story: {choices}, these can range from 5, 10 and up to 20."
                + "Number of choices per turn: {complexity}, that is: (High: 5 choices per turn), (Med: 3 choices), (Low: 2 choices)"
                + "Location where the story takes place: {location}"
                + "Provide the user with exactly {choices} choices, one below the other with the text Choices as title, separate from the story.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("genre", genre, "numCharacters", numCharacters, "nameDescription", nameDescription,
                        "choices", choices, "complexity", complexity, "location", location));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        String content = response.getResult().getOutput().getText();
        String[] parts = content.split("Choices:", 2);

        // updates game session
        String initialStory = parts[0].trim();
        storyStatus.setStory(initialStory);
        storyStatus.setDecisions(choices);

        return response;
    }
    
    public ChatResponse storyProgress(String story, String selectedChoice) {

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "-Decisions remaining: {decisions}."
                + "Task:\n"
                + "-Continue the story from the context.\n"
                + "-Describe what follows.\n"
                + "Generate exactly {choices} choices for the player. Each choice must be distinct and relevant.\n"
                + "For every choice the user selects, let the characters be affected by them, be it in a positive or " +
                "negative way, so the story progresses. Physical and mental state should be affected by choices.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        if(storyStatus.getDecisions() <= 0) {
            this.storyStatus.setStoryEnded(true);
            ChatResponse endingResponse = endStory(story);
            return endingResponse;

        } else {
            //subtracts 1 from decisions every turn
            storyStatus.UpdateDecisions();
            int decisionsLeft = storyStatus.getDecisions();

            Prompt prompt = promptTemplate
                    .create(Map.of("story", story, "decisionsLeft", decisionsLeft));

            ChatResponse progressResponse = chatClient.prompt(prompt).call().chatResponse();

            // updates game session
            this.storyStatus.setStoryEnded(false);
            this.storyStatus.storyUpdate(story);

            return progressResponse;
        }
    }

    public ChatResponse endStory(String story) {

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "Task:\n"
                + "-Generate an ending according to the story so far and the main character's mental and physical state." +
                "The ending can be good, neutral or bad depending on the main character's mental and physical state." +
                "Let the user know the story has finished.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("story", story));

        ChatResponse endingResponse = chatClient.prompt(prompt).call().chatResponse();
        storyStatus.eraseSession(storyStatus);
        this.storyStatus.setStoryEnded(true);

        return endingResponse;
    }
}
