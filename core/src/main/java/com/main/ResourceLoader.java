package com.main;

import java.util.ArrayList;

public class ResourceLoader {
    public static ArrayList<Task> loadTask(){
        try{
            System.out.println("Deserialisation success"); //Debug
            return DeserializeJsonData.initializeTaskClass();
        }catch (Exception e1) {
            System.out.println("Deserialisation failed"); //Debug
            e1.printStackTrace();
            return null;
        }
    }

}
