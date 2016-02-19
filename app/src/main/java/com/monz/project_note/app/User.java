package com.monz.project_note.app;

import java.util.List;

/**
 * Created by Андрей on 29.01.2016.
 */
public class User {
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
