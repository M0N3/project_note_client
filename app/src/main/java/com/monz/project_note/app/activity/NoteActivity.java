package com.monz.project_note.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.monz.project_note.app.Label;
import com.monz.project_note.app.R;
import com.monz.project_note.app.adapter.ChangeLabelAlertAdapter;
import com.monz.project_note.app.adapter.LabelListAdapter;

import java.text.SimpleDateFormat;
import java.util.*;

public class NoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;

    private RelativeLayout layout;

    private boolean swapIcon = false;

    private EditText title;

    private EditText text;

    private String color;

    private String date;

    private int id;

    private LabelListAdapter lla;

    private RecyclerView rv;

    private ArrayList<String> noteList;

    private ArrayList<String> array_sort;

    private int textLength = 0;

    private android.app.AlertDialog myAlertDialog = null;

    private android.app.AlertDialog.Builder myDialog;

    private android.support.v7.view.menu.ActionMenuItemView menuItemView;

    @Override
    public void onBackPressed() {
        returnData();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);

        noteList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        layout = (RelativeLayout) findViewById(R.id.note_create__layout);
        title = (EditText) findViewById(R.id.editTitle);
        color = getIntent().getStringExtra("color");
        text = (EditText) findViewById(R.id.editTextContent);
        rv = (RecyclerView) findViewById(R.id.createNoteRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

        initToolbar();
        // Если не новая заметка - инициализируем уже существующую
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
        noteList = getIntent().getStringArrayListExtra("labels");
        lla = new LabelListAdapter(noteList);
        rv.setAdapter(lla);
        menuItemView = (android.support.v7.view.menu.ActionMenuItemView) findViewById(R.id.note_access);
        if (getIntent().getBooleanExtra("access", false)) {
            menuItemView.setIcon(getResources().getDrawable(R.mipmap.ic_lock_open_outline));
            swapIcon = true;
        } else {
            menuItemView.setIcon(getResources().getDrawable(R.mipmap.ic_lock_outline));
            swapIcon = false;
        }

    }

    private void returnData() {
        // Возвращаем данные заметки в основное активити
        id = getIntent().getIntExtra("id", -1);
        Log.i("TAG", "Note added");
        Intent intent = new Intent();
        intent.putExtra("title", title.getText().toString());
        intent.putExtra("date", date);
        intent.putExtra("text", text.getText().toString());
        intent.putExtra("access", swapIcon);
        intent.putExtra("color", color);
        intent.putExtra("id", id);
        intent.putStringArrayListExtra("labels", noteList);
        setResult(RESULT_OK, intent);
    }

    private ChangeLabelAlertAdapter arrayAdap;

    private void initToolbar() {
        // Задаем начальное состояние заметки
        color = "#66bb6a";
        date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        toolbar.setTitle(R.string.empty);
        // Обрабатываем клики по тулбар меню
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_note:
                        returnData();
                        finish();
                        return true;
                    case R.id.note_color:
                        colorHandler();
                        return true;
                    case R.id.note_label:
                        labelHandler();
                        return true;
                    case R.id.note_access:
                     accessHandler();
                        return true;
                    case R.id.note_delete:
                      deleteHandler();
                        return true;
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.create_note_menu);
    }

    private void deleteHandler() {
        AlertDialog.Builder ad = new AlertDialog.Builder(NoteActivity.this);
        ad.setTitle("Delete note");  // заголовок
        ad.setMessage("Are you sure?"); // сообщение
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
    }

    private void accessHandler() {
        menuItemView = (android.support.v7.view.menu.ActionMenuItemView) findViewById(R.id.note_access);
        if (!swapIcon) {
            menuItemView.setIcon(getResources().getDrawable(R.mipmap.ic_lock_open_outline));
            swapIcon = true;
        } else {
            menuItemView.setIcon(getResources().getDrawable(R.mipmap.ic_lock_outline));
            swapIcon = false;
        }
    }

    private void labelHandler() {
        myDialog = new android.app.AlertDialog.Builder(NoteActivity.this);
        final EditText editText = new EditText(NoteActivity.this);
        final ListView listview = new ListView(NoteActivity.this);
        editText.setSingleLine(true);
        editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_magnify, 0, 0, 0);
        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
        array_sort = new ArrayList<>(Arrays.asList(arr));
        LinearLayout layout = new LinearLayout(NoteActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        arrayAdap = new ChangeLabelAlertAdapter(NoteActivity.this, array_sort, noteList);
        listview.setAdapter(arrayAdap);
        listview.setOnItemClickListener(NoteActivity.this);
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                textLength = editText.getText().length();
                array_sort.clear();
                for (int i = 0; i < Label.getLabels().size(); i++) {
                    if (textLength <= Label.getLabel(i).length()) {
                        if (Label.getLabel(i).toLowerCase().contains(editText.getText().toString().toLowerCase().trim())) {
                            array_sort.add(Label.getLabel(i));
                        }
                    }
                }
                listview.setAdapter(new ChangeLabelAlertAdapter(NoteActivity.this, array_sort, noteList));
            }
        });
        myDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        myDialog.setNeutralButton("add new label", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        myAlertDialog = myDialog.create();
        myAlertDialog.dismiss();
        myAlertDialog.show();
        myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().trim().equals(""))
                    Label.addLabel(editText.getText().toString());
                editText.setText("");
                editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_magnify, 0, 0, 0);
            }
        });

    }

    private void colorHandler() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(NoteActivity.this);
        builderSingle.setTitle("Choice color:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                NoteActivity.this,
                android.R.layout.select_dialog_item);

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
    }

    @Override
    public void onItemClick(AdapterView adap, View view, int position, long id) {
        // Обрабатываем клик по ярлыку в списке выбора
        // и добавляем или убираем ярлык из заметки
        String strName = Label.getLabel(position);
        ColorDrawable cd = (ColorDrawable) view.getBackground();
        if (cd != null && cd.getColor() == getResources().getColor(R.color.labelBack)) {
            view.setBackgroundColor(Color.parseColor("#f2f2f2"));
            noteList.remove(strName);
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.labelBack));
            noteList.add(strName);
        }
        lla = new LabelListAdapter(noteList);
        rv.setAdapter(lla);
    }
}
