package com.example.janari.SimpleDailyBudgetApp.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    public String userBudget;
    public String time;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Message(String userBudget,String time) {
        this.userBudget = userBudget;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", userBudget);
        result.put("time", time);

        return result;
    }
}
