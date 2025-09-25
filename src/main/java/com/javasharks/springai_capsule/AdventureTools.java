package com.javasharks.springai_capsule;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class AdventureTools {
    int result;
    @Tool(description = "Count how many times the input words has been mentioned in the story")
    public int getWordCount(String story, String word1, String word2) {
        if(word1.isEmpty() || word2.isEmpty() || story.isEmpty()) {
            return 0;
        }
        if(story.contains(word1)) {
            result++;
        }
        if(story.contains(word2)) {
            result++;
        }
        return result;
    }

    public int getCount(String word1, String word2) {
        return result;
    }
}
