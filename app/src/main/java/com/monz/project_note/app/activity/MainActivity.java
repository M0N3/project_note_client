package com.monz.project_note.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monz.project_note.app.Label;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;
import com.monz.project_note.app.adapter.ChangeLabelAlertAdapter;
import com.monz.project_note.app.adapter.NoteListAdapter;
import com.monz.project_note.app.database.NoteDBHelper;
import com.monz.project_note.app.fragment.AnotherNoteFragment;
import com.monz.project_note.app.fragment.HelpFragment;
import com.monz.project_note.app.fragment.LabelListFragment;
import com.monz.project_note.app.fragment.NoteListFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RecyclerView rv;
    private List<Note> noteList;
    private List<Note> anotherNotes;
    private NoteListAdapter nla;
    private NavigationView nv;
    private NoteDBHelper noteDBHelper;
    private NoteListFragment noteFragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private LabelListFragment labelFragment;
    private AnotherNoteFragment anotherNoteFragment;
    private HelpFragment helpFragment;
    private ListView listView;
    private String username = null;
    private String url;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(R.layout.activity_main);
        url = getString(R.string.server_url);

        // Если нету активного юзера
        if (username == null) {
            username = getIntent().getStringExtra("name");
        }

        noteFragment = new NoteListFragment();
        labelFragment = new LabelListFragment();
        helpFragment = new HelpFragment();
        anotherNoteFragment = new AnotherNoteFragment();
        manager = getSupportFragmentManager();
        requestQueue = Volley.newRequestQueue(this);

        // Устанавливаем фрагмент с заметками в начале
        transaction = manager.beginTransaction();
        transaction.add(R.id.main_frameLayout, noteFragment);
        transaction.commit();
        manager.executePendingTransactions();

        initToolbar();

        // Создаем объект ДБ
        noteDBHelper = new NoteDBHelper(getApplicationContext());
        // Восстанавливаем заметки с БД
        noteList = noteDBHelper.getNotes(username);
        // Восстанавливаем ярлыки с БД
        Label.setLabels(noteDBHelper.getLabels(username));
        if (noteDBHelper.isLabelsCreated(username)) {
            noteDBHelper.addLabels(username);
        }

        initNavigationView();
        initDrawerLayout();
        initNoteListAdapter();
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Обрабатываем открытие бокового меню
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                TextView userTitle = (TextView) findViewById(R.id.usernameTitle);
                if (userTitle != null)
                    userTitle.setText("Hello " + getIntent().getStringExtra("name"));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.note_title));
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        // Обрабатываем клики по тулбар-меню
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.note:
                        noteFragmentSetup();
                        return true;
                    case R.id.another_note:
                        anotherNoteFragmentSetup();
                        return true;
                    case R.id.label:
                        labelFragmentSetup();
                        return true;
                }
                return false;
            }
        });
    }

    private void initNavigationView() {
        nv = (NavigationView) findViewById(R.id.navigation);
        // Обрабатываем клики бокового меню
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_exit_item:
                        moveTaskToBack(true);
                        System.exit(0);
                        return true;
                    case R.id.menu_sign_out_item:
                        noteDBHelper.updateUser(username, false);
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_label_item:
                        labelFragmentSetup();
                        return true;
                    case R.id.menu_note_item:
                        noteFragmentSetup();
                        return true;
                    case R.id.menu_help_item:
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.main_frameLayout, helpFragment);
                        transaction.commit();
                        manager.executePendingTransactions();
                        return true;
                }
                return false;
            }
        });
    }

    private void initNoteListAdapter() {
        nla = new NoteListAdapter(noteList, this);
        // Обрабатываем клик по заметке, и переходим к ее редактированию
        initNLAClickListener(nla, noteList, true);
    }

    private void initNLAClickListener(NoteListAdapter nla, final List<Note> list, final Boolean editable) {
        nla.setOnItemClickListener(new NoteListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v, NoteListAdapter.NoteViewHolder nvl) {
                for (Note n : list) {
                    if (n.getId() == nvl.getId()) {
                        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                        intent.putExtra("new", false);
                        intent.putExtra("access", n.isCommon_access());
                        intent.putExtra("title", n.getTitle());
                        intent.putExtra("text", n.getText());
                        intent.putExtra("color", n.getColor());
                        intent.putExtra("id", n.getId());
                        intent.putExtra("editable", editable);
                        NEW = false;
                        changed_note = n;
                        list.remove(n);
                        list.add(0, changed_note);
                        intent.putStringArrayListExtra("labels", n.getLabels());
                        startActivityForResult(intent, 1);
                        break;
                    }
                }
            }
        });
    }

    private boolean isNoteFragment = true;
    private boolean isLabelFragment = false;

    private void noteFragmentSetup() {
        isNoteFragment = true;
        isLabelFragment = false;
        hideKeyboard();

        transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, noteFragment);
        transaction.commit();
        manager.executePendingTransactions();
        initNoteListAdapter();
        nla = new NoteListAdapter(noteList, this);
        rv = (RecyclerView) noteFragment.getView().findViewById(R.id.mainRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(nla);
    }

    private ChangeLabelAlertAdapter adapter;


    private void labelFragmentSetup() {
        isNoteFragment = false;
        isLabelFragment = true;
        transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, labelFragment);
        transaction.commit();
        manager.executePendingTransactions();
        // Устанавливаем список ярлыков
        listView = (ListView) labelFragment.getView().findViewById(R.id.labelListView);
        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
        final ArrayList<String> array_sort = new ArrayList<>(Arrays.asList(arr));
        adapter = new ChangeLabelAlertAdapter(MainActivity.this, array_sort, array_sort);
        listView.setAdapter(adapter);
        // Обрабатываем клик по ярлыку
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Delete label");  // заголовок
                ad.setMessage("Are you sure?"); // сообщение
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                // Удаляем ярлык, и все его вхождения в заметки, обновляем списки
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        for (Note n : noteList) {
                            if (n.getLabels().contains(Label.getLabels().toArray()[position])) {
                                n.getLabels().remove(Label.getLabels().toArray()[position]);
                                noteDBHelper.updateNote(n);
                            }
                        }
                        nla = new NoteListAdapter(noteList, MainActivity.this);
                        rv.setAdapter(nla);
                        String label = (String) Label.getLabels().toArray()[position];
                        Label.remove(position);
                        adapter.notifyDataSetChanged();
                        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
                        final ArrayList<String> array_sort = new ArrayList<>(Arrays.asList(arr));
                        adapter = new ChangeLabelAlertAdapter(MainActivity.this, array_sort, array_sort);
                        listView.setAdapter(adapter);
                        noteDBHelper.updateLabels(username);
                    }
                });
                ad.setCancelable(false);
                ad.show();
            }
        });
    }

    private void anotherNoteFragmentSetup() {
        isNoteFragment = false;
        isLabelFragment = false;
        transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, anotherNoteFragment);
        transaction.commit();
        manager.executePendingTransactions();
        hideKeyboard();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Заменяем стандартную иконку поиска на свою
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        v.setImageResource(R.mipmap.ic_magnify);
        mSearchView.setOnQueryTextListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Устанавливаем поиск по клику на иконку поиска
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
        // Ставим клавиатуру на пол экрана
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isAttached = false;

    @Override
    protected void onStart() {
        super.onStart();
        // Устанавливаем список заметок в начале 1 раз
        if (!isAttached) {
            rv = (RecyclerView) noteFragment.getView().findViewById(R.id.mainRecyclerView);
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rv.setAdapter(nla);
            isAttached = true;
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        // Скрываем кейбоард
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        // Выход из приложения
        moveTaskToBack(true);
        System.exit(0);
        super.onBackPressed();
    }

    private boolean NEW;
    private Note changed_note;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideKeyboard();
        super.onActivityResult(requestCode, resultCode, data);
        // Обрабатываем данные, которые вернулись после
        // создания или редактитрования заметки
        if (data == null || !isLabelFragment && !isNoteFragment) {
            return;
        }

        boolean delete = data.getBooleanExtra("delete", false);
        if (delete) {
            if (!NEW) {
                noteList.remove(changed_note);
                nla = new NoteListAdapter(noteList, this);
                rv.setAdapter(nla);
                noteDBHelper.deleteNote(changed_note);
            }
            return;
        }
        String title = data.getStringExtra("title");
        String text = data.getStringExtra("text");
        String date = data.getStringExtra("date");
        ArrayList<String> labels = data.getStringArrayListExtra("labels");
        boolean access = data.getBooleanExtra("access", true);
        String color = data.getStringExtra("color");
        if (NEW) {
            Note note = new Note(title, text, access, color, getIntent().getStringExtra("name"), date, labels);
            noteList.add(0, note);
            Log.v("TAG", "InAdded");
            noteDBHelper.addNote(note);
            sendAddNoteRequest(note);
        } else {
            changed_note.setColor(color);
            changed_note.setText(text);
            changed_note.setTitle(title);
            changed_note.setCommon_access(access);
            changed_note.setLabels(labels);
            noteDBHelper.updateNote(changed_note);
        }
        noteDBHelper.updateLabels(username);
        nla = new NoteListAdapter(noteList, this);
        rv.setAdapter(nla);
    }

    public void onFABClick(View v) {
        // Клик по кнопке создания заметки
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        NEW = true;
        intent.putExtra("new", true);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Добавляем ярлык по клику в список ярлыков
        if (isLabelFragment) {
            if (!query.trim().equals("") && !Label.getLabels().contains(query)) {
                Label.addLabel(query);
                Toast.makeText(MainActivity.this, "Label added!", Toast.LENGTH_SHORT).show();
                noteDBHelper.updateLabels(username);
            }
        }
        hideKeyboard();
        return false;
    }


    public void onAnotherNoteFragmentClick(View v) {
        EditText ed = (EditText) findViewById(R.id.Edit_another_note);
        final String query = ed.getText().toString();
        ed.setText("");
        anotherNotes = new ArrayList<>();
        // Отправляем запрос на сервер
        JsonRequest jsonRequest = new JsonObjectRequest(
                StringRequest.Method.POST, url + "/getnotes",
                "{ \"name\": \"" + query + "\"}",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray JSONNotes = (JSONArray) response.get("getnotes");
                            JSONArray JSONLabels = (JSONArray) response.get("getlabels");
                            for (int i = 0; i < JSONNotes.length(); i++) {
                                ArrayList<String> noteLabels = getLabels(JSONNotes.getJSONObject(i).getInt("Number"), JSONLabels);
                                anotherNotes.add(new Note(
                                        JSONNotes.getJSONObject(i).getString("Title"),
                                        JSONNotes.getJSONObject(i).getString("Content").replaceAll("xkl", "\n"),
                                        !JSONNotes.getJSONObject(i).getBoolean("Private"),
                                        JSONNotes.getJSONObject(i).getString("Color"),
                                        query,
                                        JSONNotes.getJSONObject(i).getString("Date"),
                                        noteLabels
                                ));
                            }
                        } catch (JSONException e) {
                            Log.i("TAG", e.toString());
                        }
                        initAnotherNotes(anotherNotes);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TAG", "ONRESPONSE ERROR " + error.getMessage());
            }
        });
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }

    private ArrayList<String> getLabels(int number, JSONArray JSONLabels) {
        ArrayList<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < JSONLabels.length(); i++) {
                if (JSONLabels.getJSONObject(i).getInt("Note") == number) {
                    list.add(JSONLabels.getJSONObject(i).getString("TagName"));
                }
            }
        } catch (JSONException e) {
            Log.i("TAG", e.toString());
        }
        return list;
    }

    private void initAnotherNotes(List<Note> anotherNotes) {
        NoteListAdapter adapter = new NoteListAdapter(anotherNotes, MainActivity.this);
        initNLAClickListener(nla, anotherNotes, false);
        RecyclerView recycle = (RecyclerView) anotherNoteFragment.getView().findViewById(R.id.anotherRecyclerView);
        recycle.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recycle.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Обрабатываем поиск по заметкам или ярлыкам
        int textLength = 0;
        if (isNoteFragment) {
            List<Note> sort = noteSort(newText, noteList);
            rv.setAdapter(new NoteListAdapter(sort, MainActivity.this));
        } else if (isLabelFragment) {
            textLength = newText.length();
            ArrayList<String> sort = new ArrayList<>();
            for (int i = 0; i < Label.getLabels().size(); i++) {
                if (textLength <= Label.getLabel(i).length()) {
                    if (Label.getLabel(i).toLowerCase().contains(newText.toLowerCase().trim())) {
                        sort.add(Label.getLabel(i));
                    }
                }
            }
            listView.setAdapter(new ChangeLabelAlertAdapter(MainActivity.this, sort, sort));
        } else {
            List<Note> sort = noteSort(newText, anotherNotes);
            initAnotherNotes(sort);
        }
        return false;
    }

    private List<Note> noteSort(String newText, List<Note> list) {
        int textLength = 0;
        textLength = newText.length();
        List<Note> sort = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (textLength <= list.get(i).getTitle().length()) {
                if (list.get(i).getTitle().toLowerCase().contains(newText.toLowerCase().trim())) {
                    sort.add(list.get(i));
                }
            }
            // Ищем и по названиям ярлыка в заметке тоже
            for (int j = 0; j < list.get(i).getLabels().size(); j++) {
                if (textLength <= list.get(i).getLabels().get(j).length()) {
                    if (list.get(i).getLabels().get(j).toLowerCase().contains(newText.toLowerCase().trim()) &&
                            !sort.contains(list.get(i))) {
                        sort.add(list.get(i));
                    }
                }
            }
        }
        return sort;
    }

    private void sendAddNoteRequest(Note note) {
        Log.v("TAG", "InREQUEST");
        JsonRequest jsonRequest = new JsonObjectRequest(
                StringRequest.Method.PUT, url + "/addnote",
                note.toJSON(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TAG", "Note added to server successfully");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TAG", "Note adding failed");
            }
        });
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }
}
