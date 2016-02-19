package com.monz.project_note.app;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Андрей on 09.02.2016.
 */
public class Label {
    private static LinkedHashSet<String> labels = new LinkedHashSet<>();

    public static LinkedHashSet<String> getLabels() {
        return labels;
    }

    public static void setLabels(ArrayList<String> arr) {
        labels.clear();
            for (String str : arr) {
                if(!str.equals(""))
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
