package com.javasharks.springai_capsule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoryResponse {

    private String storyUpdate;
    private List<String> choices;
}
