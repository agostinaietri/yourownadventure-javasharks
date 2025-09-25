package com.javasharks.springai_capsule.service;

import com.javasharks.springai_capsule.AdventureTools;
import com.javasharks.springai_capsule.RagService;
import com.javasharks.springai_capsule.StoryStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdventureService {
    private final ChatClient chatClient;
    private StoryStatus storyStatus;
    private final RagService ragService;
    private int wordCount;
    private String word1 = "Megalodon";
    private String word2 = "Thank you for paving the way for us";


    //setting up memory advisor so that the story and choices are remembered
    @Autowired
    public AdventureService(ChatClient.Builder builder, ChatMemory chatMemory, RagService ragService, AdventureTools adventureTools) {
        this.ragService = ragService;
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(adventureTools)
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

    public ChatResponse storyInitializer(String genre, int numCharacters, String nameDescription, int choices, int complexity, String location) {
        this.storyStatus = new StoryStatus();

        String template =
                "You're an interactive storyteller."
                + "Start a story with the following parameters (output it with Story:)"
                + "Genre: {genre}"
                + "Number of characters: {numCharacters}"
                + "Description and name of main character: {nameDescription}"
                + "Total choices in the story: {choices}, these can range from 5, 10 and up to 20."
                + "Number of parts in the story: {complexity}, that is: (High: 5 choices per turn), (Med: 3 choices), (Low: 2 choices)"
                + "Location where the story takes place: {location}"
                + "Provide the user with exactly {choices} choices, one below the other with the text Choices as title, separate from the story. Don't provide anything below that.";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("genre", genre, "numCharacters", numCharacters, "nameDescription", nameDescription,
                        "choices", choices, "complexity", complexity, "location", location));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        String content = response.getResult().getOutput().getText();
        content.replaceAll("\\*\\*", "");
        String[] parts = content.split("Choices:", 2);

        // updates game session
        String initialStory = parts[0].trim();

        this.storyStatus.setStory(initialStory);
        this.storyStatus.setStoryEnded(false);
        this.storyStatus.setComplexityLeft(complexity);
        this.storyStatus.setChoicesNumber(choices);
        //complexity as in "parts of the story"
        this.storyStatus.setComplexity(complexity);

        return response;
    }

    public ChatResponse storyProgress(String lastChoice) {

        this.storyStatus.setLastChoice(lastChoice);
        String context = String.join("\n", ragService.getContext());

        if(this.storyStatus.getComplexityLeft() <= 0) {
            this.storyStatus.setStoryEnded(true);
            this.storyStatus.eraseSession(this.storyStatus);
            ChatResponse endingResponse = endStory();
            return endingResponse;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("story", storyStatus.getStory());
        variables.put("lastChoice", lastChoice);
        variables.put("complexityLeft", storyStatus.getComplexityLeft());
        variables.put("choicesNumber", storyStatus.getChoicesNumber());
        variables.put("context", context);

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "-Complexity remaining: {complexityLeft}."
                + "-Last choice: {lastChoice}"
                + "Task:\n"
                + "-Continue the story from the context.\n"
                + "-Describe what follows.\n"
                + "Generate exactly {choicesNumber} story appropriate choices for the player to choose from. Each choice " +
                "must be distinct and relevant.\n For every choice the user selects, let the characters be affected by " +
                "them, be it in a positive or negative way, so the story progresses. Physical and mental state should be " +
                "affected by choices. Write the next part of the story according to the {story} story so far and the " +
                "last choice by the user: {lastChoice}. Also, include a car in the story, mention it by name "
                + "(Megalodon by SharkCars) and mention one or two features of it and how cool and modern it is."
                + " features and information of the car can be found in the context: {context}. Also, mention the phrase"
                + " 'Thanks for paving the way for us' at the end of the prompt.";
                //+ " At the end of the response, call the tool 'getWordCount' to check how many times the words 'Megalodon'"
                //+ " and 'Thanks for paving the way for us' were mentioned. Return it like: 'Count: X'";

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(variables);

        ChatResponse progressResponse = chatClient.prompt(prompt).call().chatResponse();

        String content = progressResponse.getResult().getOutput().getText();
        content.replaceAll("\\*\\*", "");
        String[] parts = content.split("Choices:", 2);
        String newStoryPart = parts[0].trim();

        var toolResponse = progressResponse.getResult().getOutput().getToolCalls();
        if(!toolResponse.isEmpty()) {
            wordCount += Integer.parseInt(toolResponse.get(0).getClass().getName());
        }

        //update game session
        this.storyStatus.setStory(newStoryPart);
        this.storyStatus.setComplexityLeft(storyStatus.getComplexityLeft()-1);

        return progressResponse;
    }

    public ChatResponse endStory() {

        String story = storyStatus.getStory();

        String template = "You're narrating a choose your own adventure story."
                + "Context:\n"
                + "-Story: {story}\n"
                + "Task:\n"
                + "-Generate an ending according to the {story} so far and the main " +
                "character's mental and physical state. The ending can be good, neutral or bad depending on the main " +
                "character's mental and physical state. Let the user know the story has finished.";
                //+"Then below that, display 'Megalodon word count: ' and then {wordCount}";

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Prompt prompt = promptTemplate
                .create(Map.of("story", story));//"wordCount", wordCount));

        ChatResponse endingResponse = chatClient.prompt(prompt).call().chatResponse();
        this.storyStatus.eraseSession(this.storyStatus);
        this.storyStatus.setStoryEnded(true);

        return endingResponse;
    }
}
