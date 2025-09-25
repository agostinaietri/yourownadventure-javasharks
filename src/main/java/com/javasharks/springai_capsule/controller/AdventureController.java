package com.javasharks.springai_capsule.controller;

import com.javasharks.springai_capsule.StoryStatus;
import com.javasharks.springai_capsule.service.AdventureService;
import com.javasharks.springai_capsule.service.OllamaService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
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

    // folder where audio inputs will be saved
    //private static final String UPLOAD_DIR = "/Users/agostina.lucia.ietri/Desktop/springai-audios";

    /*
    @PostMapping("/form/speechToText")
    public String speechToText(@RequestParam("file") MultipartFile file, Model model,
                               RedirectAttributes redirectAttributes) {
        if(file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "speechToText";
        }

        try {
            // make sure directory exists
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if(Files.notExists(uploadDir)) {
                //create it if it doesn't
                Files.createDirectories(uploadDir);
            }

            //save input file to folder
            Path path = uploadDir.resolve(file.getOriginalFilename());
            Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
            // generate text, add to model
            String speechToText = service.speechToText(path.toString());
            model.addAttribute("transcription", transcription);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to upload file");
        }

        return "AdventureHelper.html";
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
    public String showForm(Model model) {
        model.addAttribute("showForm", true);
        return "AdventureHelper.html";
    }

    @PostMapping("/start")
    public String startStory(
            @RequestParam String genre,
            @RequestParam int numCharacters,
            @RequestParam String nameDescription,
            @RequestParam int choices,
            @RequestParam int complexity,
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
        boolean storyEnded = false;
        // updates game session
        //this.storyStatus.setStoryEnded(false);
        //this.storyStatus.setDecisions(choices);
        //this.storyStatus.storyUpdate(story);

        //Image image = getImage("Generate an according image for the introduction of the story in {story}");

        model.addAttribute("storyStarted", true);
        model.addAttribute("storyEnded", storyEnded);
        model.addAttribute("response", response);
        model.addAttribute("story", story);
        model.addAttribute("choicesResponse", Arrays.asList(choicesResponse));
        //model.addAttribute("image", image);
        model.addAttribute("showForm", false);

        return "AdventureHelper.html";
    }

    @PostMapping("/progress")
    public String progressStory(@RequestParam("choice") String lastChoice, Model model) {

        ChatResponse content = adventureService.storyProgress(lastChoice);
        String progressResponse = content.getResult().getOutput().getText();

        String[] parts = progressResponse.split("Choices:", 2);
        String[] choicesResponse = new String[0];
        if(parts.length > 1) {
            choicesResponse = parts[1].trim().split("\n");
            model.addAttribute("storyEnded", false);
        } else {
            model.addAttribute("storyEnded", true);
        }

        String storyUpdate = parts[0].trim();

        model.addAttribute("storyStarted", true);
        model.addAttribute("progressResponse", progressResponse);
        model.addAttribute("story", storyUpdate);
        model.addAttribute("lastChoice", lastChoice);
        model.addAttribute("choicesResponse", Arrays.asList(choicesResponse));
        model.addAttribute("showForm", false);

        boolean isProgress = true;

        model.addAttribute("isProgress", isProgress);
        return "AdventureHelper.html";
    }

    @PostMapping("/ending")
    public String endStory(@RequestParam String story, Model model) {
        ChatResponse content = adventureService.endStory();
        String endingResponse = content.getResult().getOutput().getText();

        //Image image = getImage("Generate an according image for the ending of the story in {story}");

        boolean storyEnded = true;
        model.addAttribute("storyEnded", storyEnded);
        model.addAttribute("endingResponse", endingResponse);
        //model.addAttribute("image", image);

        //calling method for returning audio for ending
        //byte[] audioBytes = adventureService.textToSpeech(story);
        //String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

        //add audio to model
        //model.addAttribute("audio-ending", base64Audio);

        return "AdventureHelper.html";
    }
}
