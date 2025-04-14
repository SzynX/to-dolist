package com.example.todolist;

import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonLoader {
    public List<Task> loadTasksFromJson() throws Exception {
        ClassPathResource resource = new ClassPathResource("sample-todo.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.readAllBytes(Paths.get(resource.getURI())), new TypeReference<List<Task>>() {});
    }
}