package com.monz.project_note.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by Андрей on 28.01.2016.
 */
public class CreateNoteActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout layout;
    private boolean swapIcon = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note_activity);
        layout = (RelativeLayout) findViewById(R.id.note_create__layout);
        initToolbar();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        toolbar.setTitle(R.string.titleText);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_note:
                        finish();
                        return true;
                    case R.id.note_color:
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreateNoteActivity.this);
                        builderSingle.setTitle("Choice color:");
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                CreateNoteActivity.this,
                                android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.add("RED");
                        arrayAdapter.add("BLUE");
                        arrayAdapter.add("GREEN");
                        arrayAdapter.add("YELLOW");
                        arrayAdapter.add("ORANGE");
                        arrayAdapter.add("BROWN");

                        builderSingle.setNegativeButton(
                                "cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builderSingle.setAdapter(
                                arrayAdapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strName = arrayAdapter.getItem(which);
                                        switch (strName) {
                                            case "RED":
                                                layout.setBackgroundColor(Color.parseColor("#ff8a80"));
                                                toolbar.setBackgroundColor(Color.parseColor("#ff8a80"));
                                                break;
                                            case "BLUE":
                                                layout.setBackgroundColor(Color.parseColor("#42a5f5"));
                                                toolbar.setBackgroundColor(Color.parseColor("#42a5f5"));
                                                break;
                                            case "GREEN":
                                                layout.setBackgroundColor(Color.parseColor("#66bb6a"));
                                                toolbar.setBackgroundColor(Color.parseColor("#66bb6a"));
                                                break;
                                            case "YELLOW":
                                                layout.setBackgroundColor(Color.parseColor("#fff176"));
                                                toolbar.setBackgroundColor(Color.parseColor("#fff176"));
                                                break;
                                            case "ORANGE":
                                                layout.setBackgroundColor(Color.parseColor("#ffb74d"));
                                                toolbar.setBackgroundColor(Color.parseColor("#ffb74d"));
                                                break;
                                            case "BROWN":
                                                layout.setBackgroundColor(Color.parseColor("#bcaaa4"));
                                                toolbar.setBackgroundColor(Color.parseColor("#bcaaa4"));
                                                break;
                                        }
                                    }
                                });
                        builderSingle.show();
                        return true;
                    case R.id.note_label:
                        return true;
                    case R.id.note_access:
                        android.support.v7.internal.view.menu.ActionMenuItemView mi = (android.support.v7.internal.view.menu.ActionMenuItemView) findViewById(R.id.note_access);
                        if (!swapIcon) {
                            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_open_outline));
                            swapIcon = true;
                        } else {
                            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_outline));
                            swapIcon = false;
                        }

                        return true;
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.create_note_menu);
    }

}
