package com.javasharks.springai_capsule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class StoryStatus {

    boolean storyEnded;
    private String story;
    private int decisions;

    /*
    public StoryStatus() {
        this.story = "";
        this.decisions = 0;
        this.storyEnded = false;
    }
    */

    public void storyUpdate(String newPart) {
        this.story += newPart + "\n";
    }

    public void UpdateDecisions() {
        decisions--;
    }

    public void eraseSession(StoryStatus storyStatus) {
        storyStatus.setStory("");
        storyStatus.setStoryEnded(true);
        storyStatus.setDecisions(0);
    }

    public String getStory() {
        return this.story;
    }

    public int getDecisions() {
        return this.decisions;
    }

    public boolean getStoryEnded() {
        return this.storyEnded;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setDecisions(int decisions) {
        this.decisions = decisions;
    }

    public void setStoryEnded(boolean storyEnded) {
        this.storyEnded = storyEnded;
    }
}
