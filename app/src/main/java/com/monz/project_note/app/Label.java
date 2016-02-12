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
    public static void addLabel(String str){
        labels.add(str);
    }
    public static String getLabel(int position){
        return (String) labels.toArray()[position];
    }
}
