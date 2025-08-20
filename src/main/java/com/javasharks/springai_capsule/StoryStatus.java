package com.javasharks.springai_capsule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryStatus {

    public int health;
    public int stamina;
    public int mental;
    private int decisions;

    public void UpdateDecisions() {
        decisions--;
    }


}
