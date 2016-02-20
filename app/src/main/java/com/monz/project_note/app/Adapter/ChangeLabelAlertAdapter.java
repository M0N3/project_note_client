package com.monz.project_note.app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.monz.project_note.app.R;
import java.util.ArrayList;
import java.util.List;

public class ChangeLabelAlertAdapter extends BaseAdapter {

    private ArrayList<String> data = null;

    private List<String> labels = null;

    private LayoutInflater mInflater = null;

    public ChangeLabelAlertAdapter(Activity activty, ArrayList<String> data, List<String> labels) {
        this.labels = labels;
        this.mInflater = activty.getLayoutInflater();
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.alert_list_row, null);
            holder.title = (TextView) convertView.findViewById(R.id.label_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Сначала все элементы должны быть 1 цвета
        ColorDrawable cd = (ColorDrawable) convertView.getBackground();
        if (cd != null && cd.getColor() == Color.parseColor("#80deea")) {
            convertView.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }

        // проверям - если наш лейбл уже добавлен к заметке
        // помечаем его
        String dataValue = data.get(position);
        if (labels.contains(dataValue)) {
            convertView.setBackgroundColor(Color.parseColor("#80deea"));
        }
        holder.title.setText(dataValue);

        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
    }
}