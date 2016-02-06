package com.monz.project_note.app;

import java.util.List;

/**
 * Created by Андрей on 29.01.2016.
 */
public class User {
    private String username;
    private String password;
    private List<Note> notes;

    public String getUsername() {
        return username;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note n) {
        this.notes.add(n);
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
