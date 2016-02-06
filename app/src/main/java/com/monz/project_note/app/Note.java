package com.monz.project_note.app;

import android.graphics.Color;

/**
 * Created by Андрей on 29.01.2016.
 */
public class Note {

    private String title;
    private String text;
    private boolean common_access;
    private String color;
    private String author;
    private String date;
    private int id;
    private static int uniq = 0;

    public int getId() {
        return id;
    }
    public String getText() {
        return text;
    }


    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public boolean isCommon_access() {

        return common_access;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public Note(String title, String text, boolean common_access, String color, String author, String date) {
        this.title = title;
        this.text = text;
        this.common_access = common_access;
        this.color = color;
        this.author = author;
        this.date = date;
        uniq++;
        id = uniq;
    }
}
