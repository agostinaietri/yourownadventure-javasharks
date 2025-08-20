package com.javasharks.springai_capsule.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.Map;

@Service
public class OllamaService {

    @Autowired
    private OllamaChatModel chatModel;
    private ChatClient chatClient;

    public String generateResult(String prompt) {

        ChatResponse response = chatModel.call(
                new Prompt(
                        prompt,
                         //"Generate the names of 5 famous pirates.",
                        OllamaOptions.builder()
                                .model(OllamaModel.MISTRAL)
                                .temperature(0.4)
                                .build()
                ));
        return response.getResult().getOutput().getText();
    }


    public String getAdventureStory(String genre, int numCharacters, String nameDescription, String length, String complexity, String location) {
        PromptTemplate promptTemplate = new PromptTemplate("You are an expert writer.\n"
                + "You should be able to generate a story where the user will provide you with options in regards to " +
                "the prompt you return.\n"
                + "The story should end with a good ending or one of the alternative endings available for the main character.\n"
                + "Generate the story following these main points which will be provided to you via user input:\n"
                + "The story should belong to the {genre} genre, the options are: Fantasy, Children's Literature, " +
                    "Action, Drama, Thriller, Terror, Mystery, Sci-Fi, War, Post-apocalyptic, Comedy.\n" + "If user " +
                    "provides a genre that's not listed, let them know and provide the list again.\n"
                + "The story should have {numCharacters} characters. 5 characters or less." + "If the number provided " +
                    "is bigger, let the user know the limit.\n"
                + "The story should have a main character that follows this information: {nameDescription}.\n"
                + "The story should be {length}. If {length} is short it should have: 5 decisions, if {length} is " +
                    "medium: 10 decisions, if {length} is long: 20." + "If user provides a length that's not permitted," +
                    "let them know the permitted values.\n"
                + "The story should follow the complexity level provided. " + "If {complexity} is high, there should be" +
                    "5 decisions per turn, if {complexity} is medium, there should be 3 decisions per turn, " +
                    "if {complexity} is low, there should be 2 decisions per turn.\n"
                + "The story should be set in {location} provided by user.\n");

        Prompt prompt = promptTemplate
                .create(Map.of("genre", genre, "numCharacters", numCharacters, "nameDescription", nameDescription,
                        "length", length, "complexity", complexity, "location", location));

        return chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
    }
}
