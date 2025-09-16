package com.javasharks.springai_capsule;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarDataLoader {

    /*
    @Value("src/main/resources/sharkcars.txt")
    private Resource resource;

    private static final String[] KEYS = {"brand", "model", "year",
            "max_speed", "electric_autononmy", "sound", "camera", "price"};

    @Autowired
    @Qualifier("myVectorStore")
    private VectorStore vectorStore;

    public void uploadData() {
        JsonReader jsonReader = new JsonReader(resource, KEYS);
        List<Document> docs = jsonReader.get();
        vectorStore.add(docs);
    }
    */

}