package com.javasharks.springai_capsule.service;

import com.javasharks.springai_capsule.StoryStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdventureService {
    //private final DataRagService dataRagService;
    private final ChatClient chatClient;
    private StoryStatus storyStatus;
    //private final PromptTemplate ragTemplate;

    /*
    @Autowired
    private EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    */


    //setting up memory advisor so that the story and choices are remembered
    @Autowired
    public AdventureService(ChatClient.Builder builder, ChatMemory chatMemory) {
        //this.dataRagService = dataRagService;
        /*
        this.ragTemplate = new PromptTemplate(
                "Always use at least two features of this car: {carInfo} in every"
                        + "part of the story. And always mention the car model by name and how cool it is."
        );
        */
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    // inject audio model

    //@Autowired
    //private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    //@Autowired
    //private OpenAiAudioSpeechModel openAiAudioSpeechModel;

    // method that expects file to transcribe to English
    /*
    public String speechToText(String path) {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions
                .builder()
                .withLanguage("en")
                .withResponseFormat(TranscriptResponseFormat.TEXT)
                .build();
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscription(
                new FileSystemResource(path), options);
        return openAiAudioTranscriptionModel.call(transcriptionPrompt).getResult().getOutput();
    }
    */

    //method that returns audio from text input
    /*
    public byte[] textToSpeech(String text) {
        return openAiAudioSpeechModel.call(text);
    }
    */

    //model for audio transcription
    //@Autowired
    //private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    /*
    //turns speech audio to text
    public String speechToText(String path) {
        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(
                new FileSystemResource(path));
        return openAiAudioTranscriptionModel.call(audioTranscriptionPrompt).getResult().getOutput();
    }

    */



    public ChatResponse storyInitializer(String genre, int numCharacters, String nameDescription, int choices, String complexity, String location) {
        this.storyStatus = new StoryStatus();

        String template =
                "You're an interactive storyteller."
                + "Start a story with the following parameters (output it with Story:)"
                + "Genre: {genre}"
                + "Number of characters: {numCharacters}"
                + "Description and name of main character: {nameDescription}"
                + "Total choices in the story: {choices}, these can range from 5, 10 and up to 20."
                + "Number of choices per turn: {complexity}, that is: (High: 5 choices per turn), (Med: 3 choices), (Low: 2 choices)"
                + "Location where the story takes place: {location}"
                + "Provide the user with exactly {choices} choices, one below the other with the text Choices as title, "
                        + "separate from the story. Don't provide anything below the choices.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("genre", genre, "numCharacters", numCharacters, "nameDescription", nameDescription,
                        "choices", choices, "complexity", complexity, "location", location));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        String content = response.getResult().getOutput().getText();
        String[] parts = content.split("Choices:", 2);

        // updates game session
        String initialStory = parts[0].trim();

        this.storyStatus.setStory(initialStory);
        this.storyStatus.setStoryEnded(false);
        this.storyStatus.setChoicesLeft(choices);
        this.storyStatus.setChoicesNumber(choices);

        return response;
    }

    public ChatResponse storyProgress(String lastChoice) {
        //String carInfo = dataRagService.getAllCarInfo();

        this.storyStatus.setLastChoice(lastChoice);

        if(this.storyStatus.getChoicesLeft() <= 0) {
            this.storyStatus.setStoryEnded(true);
            this.storyStatus.eraseSession(this.storyStatus);
            ChatResponse endingResponse = endStory();
            return endingResponse;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("story", storyStatus.getStory());
        variables.put("lastChoice", lastChoice);
        variables.put("choicesLeft", storyStatus.getChoicesLeft());
        variables.put("choicesNumber", storyStatus.getChoicesNumber());
        //variables.put("carInfo", carInfo);

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "-Choices remaining: {choicesLeft}."
                + "-Last choice: {lastChoice}"
                + "Task:\n"
                + "-Continue the story from the context.\n"
                + "-Describe what follows.\n"
                + "Generate exactly {choicesNumber} story appropriate choices for the player to choose from, and" +
                "nothing else below. Each choice must be distinct and relevant.\n For every choice the user selects," +
                "let the characters be affected by them, be it in a positive or negative way, so the story progresses." +
                "Physical and mental state should be affected by choices. Write the next part of the story according to "+
                "the {story} story so far";
                //" and the last choice by the user: {lastChoice} and always make sure to include" +
                //"one or two features of this car: {carInfo} and mention it by the model.";

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(variables);

        ChatResponse progressResponse = chatClient.prompt(prompt).call().chatResponse();
        String content = progressResponse.getResult().getOutput().getText();
        String[] parts = content.split("Choices:", 2);
        String newStoryPart = parts[0].trim();

        //update game session
        this.storyStatus.setStory(newStoryPart);
        this.storyStatus.setChoicesLeft(storyStatus.getChoicesLeft()-1);

        return progressResponse;

        /*
        if(storyStatus.getDecisions() <= 0) {
            this.storyStatus.setStoryEnded(true);
            ChatResponse endingResponse = endStory(currentStory);
            return endingResponse;

        } else {
            //subtracts 1 from decisions every turn
            storyStatus.UpdateDecisions();
            int decisionsLeft = storyStatus.getDecisions();

            Prompt prompt = promptTemplate
                    .create(Map.of( "decisionsLeft", decisionsLeft, "lastChoice", lastChoice));

            ChatResponse progressResponse = chatClient.prompt(prompt).call().chatResponse();

            String content = progressResponse.getResult().getOutput().getText();
            String[] parts = content.split("Choices:", 2);
            String choices = parts[1].trim();

            // updates game session
            String initialStory = parts[0].trim();
            this.storyStatus.setStoryEnded(false);
            this.storyStatus.storyUpdate(initialStory);
            this.storyStatus.setLastChoice(lastChoice);

            return progressResponse;
        }
        */
    }

    public ChatResponse endStory() {

        String story = storyStatus.getStory();
        //String lastChoice = storyStatus.getLastChoice();

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "Task:\n"
                + "-Generate an ending according to the {story} so far and the main " +
                "character's mental and physical state. The ending can be good, neutral or bad depending on the main " +
                "character's mental and physical state. Let the user know the story has finished.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        //Prompt prompt = promptTemplate
        //        .create(Map.of("story", story, "lastChoice", lastChoice));

        Prompt prompt = promptTemplate
                .create(Map.of("story", story));

        ChatResponse endingResponse = chatClient.prompt(prompt).call().chatResponse();
        this.storyStatus.eraseSession(this.storyStatus);
        this.storyStatus.setStoryEnded(true);

        return endingResponse;
    }
}
