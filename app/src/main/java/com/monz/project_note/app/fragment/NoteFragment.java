package com.monz.project_note.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.monz.project_note.app.R;

/**
 * Created by Андрей on 15.02.2016.
 */
public class NoteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TAG", "ONCREATEVIEW");
        return inflater.inflate(R.layout.note_fragment_layout, null);
    }
}
