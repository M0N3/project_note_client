package com.monz.project_note.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.monz.project_note.app.R;
import java.util.List;

public class LabelListAdapter extends RecyclerView.Adapter<LabelListAdapter.LabelViewHolder> {

    private List<String> data;

    private LabelViewHolder holder;

    public LabelListAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_item, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        this.holder = holder;
        String str = data.get(position);
        this.holder.label.setText(str);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder  {

        private TextView label;

        public LabelViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.label_card_item);
        }
    }
}
