package com.monz.project_note.app.adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {
    private List<Note> data;
    private static MyClickListener myClickListener;
    private LabelListAdapter lla;
 //   private LinearLayoutManager layoutManagerItem;
    private Context context;

    public NoteListAdapter(List<Note> data, Context con) {
        this.data = data;
        this.context = con;
        // this.layoutManagerItem = mn;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note item = data.get(position);

        lla = new LabelListAdapter(item.getLabels());
        holder.rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv.setAdapter(lla);

        holder.title.setText(item.getTitle());
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        holder.setId(item.getId());
//        lla = new LabelListAdapter(item.getLabels());
//        holder.rv.setLayoutManager(layoutManagerItem);
//        holder.rv.setAdapter(lla);
//        for(String str : item.getLabels()){
//            TextView tx = new TextView(context);
//            tx.setPadding(10,0,10,0);
//          tx.setBackgroundColor(Color.parseColor("#f2f2f2"));
//            tx.setText(str);
//            holder.layout.addView(tx);
//            TextView stx = new TextView(context);
//            stx.setText("  ");
//            holder.layout.addView(stx);
//
//        }
        if (item.isCommon_access()) {
            holder.access.setText("Public");
        } else {
            holder.access.setText("Private");
        }
        holder.getCardView().setCardBackgroundColor(Color.parseColor(item.getColor()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v, NoteViewHolder nvl);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private int id;
        private TextView title;
        private TextView author;
        private TextView date;
        private TextView access;
        private LinearLayout layout;
        private RecyclerView rv;

        public void setId(int id) {
            this.id = id;
        }

        public int getUniq() {
            return id;
        }

        public TextView getTitle() {
            return title;
        }

        public CardView getCardView() {
            return cardView;
        }

        public NoteViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.noteCardView);
            rv = (RecyclerView) itemView.findViewById(R.id.noteRecyclerView);
            title = (TextView) itemView.findViewById(R.id.list_item_title);
            author = (TextView) itemView.findViewById(R.id.list_item_author);
            date = (TextView) itemView.findViewById(R.id.list_item_date);
            access = (TextView) itemView.findViewById(R.id.list_item_access);
            //layout = (LinearLayout) itemView.findViewById(R.id.label_layout);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v, this);
        }
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }
}
