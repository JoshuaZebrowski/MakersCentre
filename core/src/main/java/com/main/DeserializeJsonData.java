package com.main;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;

public class DeserializeJsonData {
    public static ArrayList<Task> initializeTaskClass() throws Exception {
        File file = new File(Gdx.files.internal("assets/data/tasks.json").file().getAbsolutePath());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }


}
