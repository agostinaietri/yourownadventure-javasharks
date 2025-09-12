package com.javasharks.springai_capsule;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentWriter;

import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void init() {
        TextReader sharkCarsReader = new TextReader(new ClassPathResource("sharkcars.txt"));
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(100,100,5,100,true);
        List<Document> documents = tokenTextSplitter.split(sharkCarsReader.get());
        vectorStore.add(documents);
    }

}
