package com.javasharks.springai_capsule;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RagService {
    private final String context;
    public RagService(@Value("classpath:sharkcars.json") Resource resource) throws IOException {
        this.context = new String(resource.getInputStream().readAllBytes());
    }

    public List<String> getContext() {
        return List.of(context);
    }
}
