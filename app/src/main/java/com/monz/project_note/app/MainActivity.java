package com.monz.project_note.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
//import android.widget.Toast;
import com.monz.project_note.app.adapter.CustomAlertAdapter;
import com.monz.project_note.app.adapter.NoteListAdapter;
import com.monz.project_note.app.database.NoteDBHelper;
import com.monz.project_note.app.fragment.HelpFragment;
import com.monz.project_note.app.fragment.LabelFragment;
import com.monz.project_note.app.fragment.NoteFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RecyclerView rv;

    private List<Note> list;
    private NoteListAdapter nla;
    private NavigationView nv;

    private NoteDBHelper noteDBHelper;

    private NoteFragment noteFragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private LabelFragment labelFragment;
    private HelpFragment helpFragment;
    private ListView listView;
    private boolean isAttached = false;
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (username == null) {
            username = getIntent().getStringExtra("name");
        }
        noteFragment = new NoteFragment();
        labelFragment = new LabelFragment();
        helpFragment = new HelpFragment();


        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.main_frameLayout, noteFragment);
        transaction.commit();
        manager.executePendingTransactions();
        initToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        noteDBHelper = new NoteDBHelper(getApplicationContext());
        list = noteDBHelper.getNotes(username);
        Label.setLabels(noteDBHelper.getLabels(username));
        if(noteDBHelper.isLabelsCreated(username)){
            noteDBHelper.addLabels(username);
        }
        nla = new NoteListAdapter(list, this);
        nla.setOnItemClickListener(new NoteListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v, NoteListAdapter.NoteViewHolder nvl) {
                for (Note n : list) {
                    if (n.getId() == nvl.getUniq()) {
                        Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                        intent.putExtra("new", false);
                        intent.putExtra("access", n.isCommon_access());
                        intent.putExtra("title", n.getTitle());
                        intent.putExtra("text", n.getText());
                        intent.putExtra("color", n.getColor());
                        intent.putExtra("id", n.getId());
                        NEW = false;
                        changed_note = n;
                        list.remove(n);
                        list.add(0, changed_note);
                        Log.i("TAG", "ONCLICK");
                        intent.putStringArrayListExtra("labels", n.getLabels());
                        startActivityForResult(intent, 1);
                        break;
                    }
                }
            }
        });

        nv = (NavigationView) findViewById(R.id.navigation);
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

    private void noteFragmentSetup() {
        isNoteFragment = true;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, noteFragment);
        transaction.commit();
        manager.executePendingTransactions();
        nla = new NoteListAdapter(list, this);
        rv = (RecyclerView) noteFragment.getView().findViewById(R.id.mainRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(nla);
    }

    private CustomAlertAdapter adapter;
    private int textlength = 0;
    private EditText edit;

    private void labelFragmentSetup() {
        isNoteFragment = false;
        transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, labelFragment);
        transaction.commit();
        manager.executePendingTransactions();
        listView = (ListView) labelFragment.getView().findViewById(R.id.labelListView);
        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
        final ArrayList<String> array_sort = new ArrayList<String>(Arrays.asList(arr));
        adapter = new CustomAlertAdapter(MainActivity.this, array_sort, array_sort);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Delete label");  // заголовок
                ad.setMessage("Are you sure"); // сообщение
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        for (Note n : list) {
                            if (n.getLabels().contains(Label.getLabels().toArray()[position])) {
                                n.getLabels().remove(Label.getLabels().toArray()[position]);
                                Log.i("TAG", "DELETELABEL");
                                noteDBHelper.updateNote(n);
                            }
                        }
                        nla = new NoteListAdapter(list, MainActivity.this);
                        rv.setAdapter(nla);
                        Label.remove(position);
                        adapter.notifyDataSetChanged();
                        String[] arr = Label.getLabels().toArray(new String[Label.getLabels().size()]);
                        final ArrayList<String> array_sort = new ArrayList<String>(Arrays.asList(arr));
                        adapter = new CustomAlertAdapter(MainActivity.this, array_sort, array_sort);
                        listView.setAdapter(adapter);
                        noteDBHelper.updateLabels(username);
                    }
                });
                ad.setCancelable(false);
                ad.show();
            }
        });
//        edit = (EditText) labelFragment.getView().findViewById(R.id.labelEditText);
//        edit.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s,
//                                          int start, int count, int after) {
//
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        if (mSearchView == null)
            Log.i("TAG", "SEARCHNULL");
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        Log.i("TAG", Integer.toString(searchImgId));
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        if (v == null)
            Log.i("TAG", "VNULL");
        v.setImageResource(R.mipmap.ic_magnify);
        mSearchView.setOnQueryTextListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("TAG", "ONMENU");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        return super.onCreateOptionsMenu(menu);


//        MenuInflater inflater = getMenuInflater();
//        // Inflate menu to add items to action bar if it is present.
//        inflater.inflate(R.menu.menu, menu);
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        handleIntent(intent);
//        super.onNewIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            Log.i("TAG", query);
//            //use the query to search
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!isAttached) {
            rv = (RecyclerView) noteFragment.getView().findViewById(R.id.mainRecyclerView);
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rv.setAdapter(nla);
            isAttached = true;
        }
        Log.i("TAG", "START");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titleText);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayShowTitleEnabled(false);
//        if(toolbar.getMenu() == null){
//            Log.i("TAG", "NULLMENU");
//        }
//        MenuInflater inflater = getMenuInflater();
//        // Inflate menu to add items to action bar if it is present.
//        inflater.inflate(R.menu.menu, toolbar.getMenu());
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if(toolbar.getMenu() != null){
//            Log.i("TAG", "NOTNULLMENU");
//        }
//        SearchView searchView =
//                (SearchView) toolbar.getMenu().findItem(R.id.search).getActionView();
//        if(searchView == null){
//            Log.i("TAG", "SEARCHNULL");
//        }
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.note:
                        noteFragmentSetup();
                        return true;
                    case R.id.search:
                        // onSearchRequested();
                        return true;
                    case R.id.label:
                        labelFragmentSetup();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        System.exit(0);
        super.onBackPressed();
    }

    private boolean NEW;
    private Note changed_note;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "ONRESULT");
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        boolean delete = data.getBooleanExtra("delete", false);
        if (delete) {
            if (!NEW) {
                list.remove(changed_note);
                nla = new NoteListAdapter(list, this);
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
            Log.i("TAG", "TITLE " + title);
            list.add(0, note);
            noteDBHelper.addNote(note);
        } else {
            changed_note.setColor(color);
            changed_note.setText(text);
            changed_note.setTitle(title);
            changed_note.setCommon_access(access);
            changed_note.setLabels(labels);
            noteDBHelper.updateNote(changed_note);
            Log.i("TAG", "CHANGED");
        }
        noteDBHelper.updateLabels(username);
        nla = new NoteListAdapter(list, this);
        rv.setAdapter(nla);
    }

    public void onFABClick(View v) {

        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        NEW = true;
        intent.putExtra("new", true);
        startActivityForResult(intent, 1);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("TAG", "OK");
        Log.i("TAG", query);
        if (!isNoteFragment) {
            if (!query.trim().equals("") && !Label.getLabels().contains(query)) {
                Label.addLabel(query);
                Toast.makeText(MainActivity.this, "Label added!", Toast.LENGTH_SHORT).show();
                noteDBHelper.updateLabels(username);
            }
        }
        return false;
    }

    private boolean isNoteFragment = true;

    @Override
    public boolean onQueryTextChange(String newText) {
        if (isNoteFragment) {
            textlength = newText.length();
            List<Note> sort = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (textlength <= list.get(i).getTitle().length()) {
                    if (list.get(i).getTitle().toLowerCase().contains(newText.toLowerCase().trim())) {
                        sort.add(list.get(i));
                        Log.i("TAG", "INSORT");
                    }
                }
            }
            rv.setAdapter(new NoteListAdapter(sort, MainActivity.this));
        } else {
            textlength = newText.length();
            ArrayList<String> sort = new ArrayList<>();
            for (int i = 0; i < Label.getLabels().size(); i++) {
                if (textlength <= Label.getLabel(i).length()) {
                    if (Label.getLabel(i).toLowerCase().contains(newText.toLowerCase().trim())) {
                        sort.add(Label.getLabel(i));
                    }
                }
            }
            listView.setAdapter(new CustomAlertAdapter(MainActivity.this, sort, sort));
        }
        Log.i("TAG", "UP");
        return false;
    }
}
