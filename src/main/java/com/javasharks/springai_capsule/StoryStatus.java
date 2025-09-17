package com.javasharks.springai_capsule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class StoryStatus {

    private String story;
    private String lastChoice;
    private int complexityLeft;
    private int choicesNumber;
    private int complexity;

    public void eraseSession(StoryStatus storyStatus) {
        this.story = "";
        lastChoice = "";
        complexityLeft = 0;
        choicesNumber = 0;
        complexity = 0;
    }

    public void setStoryEnded(boolean b) {
    }

    public String getLastChoice() {
        return this.lastChoice;
    }

    public String getStory() {
        return this.story;
    }

    public void setStory(String newStoryPart) {
        this.story = " " + newStoryPart;
    }

    public int getComplexityLeft() {
        return this.complexityLeft;
    }

    public void setComplexityLeft(int complexityLeft) {
        this.complexityLeft = complexityLeft;
    }

    public void setLastChoice(String lastChoice) {
        this.lastChoice = lastChoice;
    }

    public int getChoicesNumber() {
        return this.choicesNumber;
    }

    public void setChoicesNumber(int choices) {
        this.choicesNumber = choices;
    }
    public int getComplexity() {
        return this.complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
}
