package com.monz.project_note.app;

import java.util.ArrayList;
import java.util.Arrays;

public class Note {

    private String title;

    private String text;

    private boolean common_access;

    private String color;

    private String author;

    private String date;

    private int id;

    // uniq - статическая переменная, которая инкрементирутся каждый раз при создании заметки (Note)
    // что позволяет каждой змаетке иметь уникальный id
    private static int uniq = 0;

    private ArrayList<String> labels;

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

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabels() {
        return labels;
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

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public boolean isCommon_access() {
        return common_access;
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

    // этот конструктор используем, когда восстанавливаем данные из БД
    public Note(Integer id, String title, String text, boolean common_access, String color, String author, String date, ArrayList<String> labels) {
        this.title = title;
        this.text = text;
        this.common_access = common_access;
        this.color = color;
        this.author = author;
        this.date = date;
        this.labels = labels;
        this.id = id;
        // т.к. id уже известны, а uniq = 0, находим максимальный id
        // и присваиваем его uniq, таким образом он остается уникальным
        if (id >= uniq)
            uniq = id + 1;
    }

    public String toJSON() {
        // сервер не обрабатывает мультилайн строки, поэтому парсим в одну
        // а знак переноса строки заменяем на "xkl", что б потом рапарсить обратно
        String content = text.replaceAll("[\r\n]+", "xkl");
        return "{" +
                "\"author\": \"" + author + "\"" +
                ", \"title\": \"" + title + "\"" +
                ", \"text\": \"" + content + "\"" +
                ", \"date\": \"" + date + "\"" +
                ", \"access\": " + common_access +
                ", \"number\": " + id +
                ", \"color\": \"" + color + "\"" +
                ", \"labels\": " + Arrays.toString(quoteArray(toStringArray(labels.toArray()))) +
                "}";
    }

    private static String[] toStringArray(Object[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    private static String[] quoteArray(String[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = "\"" + array[i] + "\"";
        }
        return result;
    }
}
