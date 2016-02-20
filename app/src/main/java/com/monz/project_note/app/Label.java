package com.monz.project_note.app;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Label {

    // Используем LinkedHashSet, потому что он не допускает повторов и
    // поддерживает связный список элементов набора в том порядке, в котором они вставлялись
    private static LinkedHashSet<String> labels = new LinkedHashSet<>();

    public static LinkedHashSet<String> getLabels() {
        return labels;
    }

    public static void setLabels(ArrayList<String> arr) {

        // Очищаем перед заполнением
        labels.clear();
        for (String str : arr) {
            // Не добавляем пустые строки, если таковые присутствуют
            if (!str.equals(""))
                labels.add(str);
        }

    }
    public static void addLabel(String str) {
        labels.add(str);
    }

    public static void remove(int position) {
        labels.remove(labels.toArray()[position]);
    }

    public static String getLabel(int position) {
        return (String) labels.toArray()[position];
    }
}
