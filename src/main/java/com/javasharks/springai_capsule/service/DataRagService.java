package com.javasharks.springai_capsule.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataRagService {

    /*
    @Value("classpath:/sharkcars.txt")
    private Resource fileResource;

    @Autowired
    VectorStore vectorStore;

    public VectorStore addDataInVectorStore() {
        TextReader textReader = new TextReader(fileResource);
        textReader.getCustomMetadata().put("filename", "sharkcars.txt");
        List<Document> documents_aux = textReader.get();
        List<Document> documents = new TokenTextSplitter().apply(documents_aux);
        vectorStore.add(documents);
        return vectorStore;
    }

    public String getAllCarInfo() {
        List<Document> allDocs = (List<Document>) addDataInVectorStore();
        return allDocs.stream()
                .map(d -> d.getText().toString())
                .collect(Collectors.joining());
    }
    */
}
