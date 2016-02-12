package com.monz.project_note.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.monz.project_note.app.Adapter.NoteListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RecyclerView rv;

    private List<Note> list;
    private NoteListAdapter nla;
    private NavigationView nv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rv = (RecyclerView) findViewById(R.id.mainRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        nv = (NavigationView) findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_exit_item:
                        moveTaskToBack(true);
                        System.exit(0);
                        return true;
                    case R.id.menu_sign_out_item:
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_label_item:

                        return true;
                    case R.id.menu_note_item:

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


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titleText);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.note:
                    return true;
                    case R.id.search:
                        return true;
                    case R.id.label:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        boolean delete = data.getBooleanExtra("delete", false);
        if (delete)
            return;
        String title = data.getStringExtra("title");
        String text = data.getStringExtra("text");
        String date = data.getStringExtra("date");
        ArrayList<String> labels = data.getStringArrayListExtra("labels");
        boolean access = data.getBooleanExtra("access", true);
        String color = data.getStringExtra("color");
        LinearLayoutManager mn = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        nla = new NoteListAdapter(list, this);

        if (getIntent().getBooleanExtra("change", false)) {
            for (Note n : list) {
                if (n.getId() == getIntent().getIntExtra("id", -1)) {
                    list.remove(n);
                    break;
                }
            }
        }
        list.add(0, new Note(title, text, access, color, getIntent().getStringExtra("name"), date, labels));
        LinearLayout layout = (LinearLayout) findViewById(R.id.label_layout);
        rv.setAdapter(nla);


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
                        intent.putStringArrayListExtra("labels", n.getLabels());
                        startActivityForResult(intent, 1);
                        break;
                    }
                }
                nla.removeAt(position);
            }
        });

    }


    public void onFABClick(View v) {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("new", true);
        startActivityForResult(intent, 1);

    }
}
