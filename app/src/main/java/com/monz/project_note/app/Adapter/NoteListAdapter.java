package com.monz.project_note.app.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.monz.project_note.app.CreateNoteActivity;
import com.monz.project_note.app.MainActivity;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;

import java.util.List;

/**
 * Created by Андрей on 30.01.2016.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {
    private List<Note> data;
    private CardView cardView;
    private NoteViewHolder holder;
    private static MyClickListener myClickListener;

    public NoteListAdapter(List<Note> data) {
        this.data = data;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        this.holder = holder;
        Note item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        holder.setId(item.getId());
        cardView = holder.getCardView();
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
            this.id = id;
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.list_item_title);
            author = (TextView) itemView.findViewById(R.id.list_item_author);
            date = (TextView) itemView.findViewById(R.id.list_item_date);
            access = (TextView) itemView.findViewById(R.id.list_item_access);
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
