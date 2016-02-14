package com.monz.project_note.app.adapter;

import android.app.Activity;
import android.content.Context;
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

/**
 * Created by Андрей on 08.02.2016.
 */
public class CustomAlertAdapter extends BaseAdapter {

    Context ctx = null;
    ArrayList<String> listarray = null;
    List<String> labels = null;
    private LayoutInflater mInflater = null;

    public CustomAlertAdapter(Activity activty, ArrayList<String> list, List<String> labels) {
        this.ctx = activty;
        this.labels = labels;
        mInflater = activty.getLayoutInflater();
        this.listarray = list;
    }

    @Override
    public int getCount() {

        return listarray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listarray.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.alertlistrow, null);
            holder.titlename = (TextView) convertView.findViewById(R.id.textView_titllename);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ColorDrawable cd = (ColorDrawable) convertView.getBackground();
        if (cd != null && cd.getColor() == Color.parseColor("#66bb6a")) {
            convertView.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }
        String datavalue = listarray.get(position);
        if (labels.contains(datavalue)) {
            convertView.setBackgroundColor(Color.parseColor("#66bb6a"));
        }
        holder.titlename.setText(datavalue);

        return convertView;
    }

    private static class ViewHolder {
        TextView titlename;
    }
}