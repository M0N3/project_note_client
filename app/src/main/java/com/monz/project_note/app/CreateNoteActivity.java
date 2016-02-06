package com.monz.project_note.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Андрей on 28.01.2016.
 */
public class CreateNoteActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout layout;
    private boolean swapIcon = false;
    private EditText title;
    private EditText text;
    private String color;
    private String date;
    private int id;
    private android.support.v7.view.menu.ActionMenuItemView mi;

    @Override
    public void onBackPressed() {
        returnData();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note_activity);
        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        layout = (RelativeLayout) findViewById(R.id.note_create__layout);
        title = (EditText) findViewById(R.id.editTitle);
        color = getIntent().getStringExtra("color");
        text = (EditText) findViewById(R.id.editTextContent);
        initToolbar();
        if (!getIntent().getBooleanExtra("new", false)) {
            initNote();
        }
    }

    private void initNote() {
        layout.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("color")));
        toolbar.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("color")));
        title.setText(getIntent().getStringExtra("title"));
        text.setText(getIntent().getStringExtra("text"));
        color = getIntent().getStringExtra("color");
        mi = (android.support.v7.view.menu.ActionMenuItemView) findViewById(R.id.note_access);
        if (getIntent().getBooleanExtra("access", false)) {
            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_open_outline));
            swapIcon = true;
        } else {
            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_outline));
            swapIcon = false;
        }

    }

    private void returnData() {
        id = getIntent().getIntExtra("id", -1);
        Log.i("TAG", "Note added");
        Intent intent = new Intent();
        intent.putExtra("title", title.getText().toString());
        intent.putExtra("date", date);
        intent.putExtra("text", text.getText().toString());
        intent.putExtra("access", swapIcon);
        intent.putExtra("color", color);
        intent.putExtra("id", id);
        intent.putExtra("change", true);
        setResult(RESULT_OK, intent);
    }


    private void initToolbar() {
        color = "#66bb6a";
        date = new SimpleDateFormat("yyyy.MM.dd").format(Calendar.getInstance().getTime());
        toolbar.setTitle(R.string.empty);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_note:
                        returnData();
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
                                                color = "#ff8a80";
                                                break;
                                            case "BLUE":
                                                layout.setBackgroundColor(Color.parseColor("#42a5f5"));
                                                toolbar.setBackgroundColor(Color.parseColor("#42a5f5"));
                                                color = "#42a5f5";
                                                break;
                                            case "GREEN":
                                                layout.setBackgroundColor(Color.parseColor("#66bb6a"));
                                                toolbar.setBackgroundColor(Color.parseColor("#66bb6a"));
                                                color = "#66bb6a";
                                                break;
                                            case "YELLOW":
                                                layout.setBackgroundColor(Color.parseColor("#fff176"));
                                                toolbar.setBackgroundColor(Color.parseColor("#fff176"));
                                                color = "#fff176";
                                                break;
                                            case "ORANGE":
                                                layout.setBackgroundColor(Color.parseColor("#ffb74d"));
                                                toolbar.setBackgroundColor(Color.parseColor("#ffb74d"));
                                                color = "#ffb74d";
                                                break;
                                            case "BROWN":
                                                layout.setBackgroundColor(Color.parseColor("#bcaaa4"));
                                                toolbar.setBackgroundColor(Color.parseColor("#bcaaa4"));
                                                color = "#bcaaa4";
                                                break;
                                        }
                                    }
                                });
                        builderSingle.show();
                        return true;
                    case R.id.note_label:
                        return true;
                    case R.id.note_access:
                        mi = (android.support.v7.view.menu.ActionMenuItemView) findViewById(R.id.note_access);
                        if (!swapIcon) {
                            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_open_outline));
                            swapIcon = true;
                        } else {
                            mi.setIcon(getResources().getDrawable(R.mipmap.ic_lock_outline));
                            swapIcon = false;
                        }
                        return true;
                    case R.id.note_delete:
                        AlertDialog.Builder ad = new AlertDialog.Builder(CreateNoteActivity.this);
                        ad.setTitle("Delete note");  // заголовок
                        ad.setMessage("Are you sure"); // сообщение
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                            }
                        });
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                Intent intent = new Intent();
                                intent.putExtra("delete", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                        ad.setCancelable(false);
                        ad.show();
                        return true;
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.create_note_menu);
    }

}
