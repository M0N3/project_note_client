package com.monz.project_note.app;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
    private ArrayList<String> labels;

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCommon_access(boolean common_access) {
        this.common_access = common_access;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

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

    public Note(String title, String text, boolean common_access, String color, String author, String date, ArrayList<String> labels) {
        this.title = title;
        this.text = text;
        this.common_access = common_access;
        this.color = color;
        this.author = author;
        this.date = date;
        this.labels = labels;
        uniq++;
        id = uniq;
    }

    public Note(Integer id, String title, String text, boolean common_access, String color, String author, String date, ArrayList<String> labels) {
        this.title = title;
        this.text = text;
        this.common_access = common_access;
        this.color = color;
        this.author = author;
        this.date = date;
        this.labels = labels;
        this.id = id;
        if (id >= uniq)
            uniq = id + 1;
    }
}
