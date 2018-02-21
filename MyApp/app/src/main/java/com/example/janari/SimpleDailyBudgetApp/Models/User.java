package com.example.janari.SimpleDailyBudgetApp.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String password;

    public User(String username, String password) {
        this.name = username;
        this.password = password;
    }
}
