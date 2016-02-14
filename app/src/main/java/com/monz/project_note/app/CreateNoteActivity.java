package com.monz.project_note.app;

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
import android.widget.*;
import com.monz.project_note.app.adapter.CustomAlertAdapter;
import com.monz.project_note.app.adapter.LabelListAdapter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Андрей on 28.01.2016.
 */
public class CreateNoteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
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
    private ArrayList<String> list;

    //  private Button btn_listviewdialog=null;
    // private EditText txt_item=null;
    // private ArrayList<String> TitleName = new ArrayList<String>();
    private ArrayList<String> array_sort;
    int textlength = 0;
    private android.app.AlertDialog myalertDialog = null;
    private android.app.AlertDialog.Builder myDialog;
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
        //txt_item=(EditText)findViewById(R.id.editText_item);
        // btn_listviewdialog=(Button)findViewById(R.id.button_listviewdialog);
        //  btn_listviewdialog.setOnClickListener(this);
//        Label.addLabel("C#");
//        Label.addLabel("C++");
//        Label.addLabel("Java");
//        Label.addLabel("Python");
        list = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        layout = (RelativeLayout) findViewById(R.id.note_create__layout);
        title = (EditText) findViewById(R.id.editTitle);
        color = getIntent().getStringExtra("color");
        text = (EditText) findViewById(R.id.editTextContent);
        rv = (RecyclerView) findViewById(R.id.createNoteRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

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
        list = getIntent().getStringArrayListExtra("labels");
        lla = new LabelListAdapter(list);
        rv.setAdapter(lla);
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
        intent.putStringArrayListExtra("labels", list);
        setResult(RESULT_OK, intent);
    }

    private CustomAlertAdapter arrayAdap;

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
                        return true;
                    case R.id.note_label:
                        myDialog = new android.app.AlertDialog.Builder(CreateNoteActivity.this);
                        final EditText editText = new EditText(CreateNoteActivity.this);
                        final ListView listview = new ListView(CreateNoteActivity.this);
                        editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_magnify, 0, 0, 0);
                        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
                        array_sort = new ArrayList<String>(Arrays.asList(arr));
                        LinearLayout layout = new LinearLayout(CreateNoteActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.addView(editText);
                        layout.addView(listview);
                        myDialog.setView(layout);
                        arrayAdap = new CustomAlertAdapter(CreateNoteActivity.this, array_sort, list);
                        listview.setAdapter(arrayAdap);
                        listview.setOnItemClickListener(CreateNoteActivity.this);
                        editText.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {

                            }

                            public void beforeTextChanged(CharSequence s,
                                                          int start, int count, int after) {

                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                textlength = editText.getText().length();
                                array_sort.clear();
                                for (int i = 0; i < Label.getLabels().size(); i++) {
                                    if (textlength <= Label.getLabel(i).length()) {
                                        if (Label.getLabel(i).toLowerCase().contains(editText.getText().toString().toLowerCase().trim())) {
                                            array_sort.add(Label.getLabel(i));
                                        }
                                    }
                                }
                                listview.setAdapter(new CustomAlertAdapter(CreateNoteActivity.this, array_sort, list));
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
                        myalertDialog = myDialog.create();
                        myalertDialog.dismiss();
                        myalertDialog.show();
                        myalertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!editText.getText().toString().trim().equals(""))
                                Label.addLabel(editText.getText().toString());
                                editText.setText("");
                                editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_magnify, 0, 0, 0);
                            }
                        });


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

    @Override
    public void onItemClick(AdapterView adap, View view, int position, long id) {
        String strName = Label.getLabel(position);
        ColorDrawable cd = (ColorDrawable) view.getBackground();
        if (cd != null && cd.getColor() == Color.parseColor("#66bb6a")) {
            view.setBackgroundColor(Color.parseColor("#f2f2f2"));
            list.remove(strName);
        } else {
            view.setBackgroundColor(Color.parseColor("#66bb6a"));
            list.add(strName);
        }
        lla = new LabelListAdapter(list);
        rv.setAdapter(lla);
    }
}
