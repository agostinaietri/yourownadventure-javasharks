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
    private int choicesLeft;
    private int choicesNumber;

    public void eraseSession(StoryStatus storyStatus) {
        this.story = "";
        lastChoice = "";
        choicesLeft = 0;
        choicesNumber = 0;
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

    public int getChoicesLeft() {
        return this.choicesLeft;
    }

    public void setChoicesLeft(int choicesLeft) {
        this.choicesLeft = choicesLeft;
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
}
