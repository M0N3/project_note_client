package com.monz.project_note.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monz.project_note.app.R;
import com.monz.project_note.app.User;
import com.monz.project_note.app.database.NoteDBHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends Activity {

    private EditText login;

    private EditText password;

    private User user;

    private RequestQueue requestQueue;

    private String url;

    private NoteDBHelper noteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(R.layout.sign_in_layout);
        noteDBHelper = new NoteDBHelper(this);
        requestQueue = Volley.newRequestQueue(this);
        url = getString(R.string.server_url);

        String name = noteDBHelper.getActiveUser();
        // Если возвращается не пустой имя - значит юзер активный
        // и сразу переходим к главному активити
        if (!name.equals("")) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
    }

    public void signInClick(View v) {

        login = (EditText) findViewById(R.id.loginEdit);
        password = (EditText) findViewById(R.id.passwordEdit);

        // Пароль и имя должны иметь больше 4 символов
        if (password.length() < 4 || login.length() < 4) {
            Toast.makeText(this,
                    "User does not exist or the incorrect password is entered!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Отправляем запрос на сервер
        JsonRequest jsonRequest = new JsonObjectRequest(
                StringRequest.Method.POST, url + "/signin",
                "{ \"name\": \"" + login.getText().toString() + "\", \"pass\": \"" + password.getText().toString() + "\"}",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user = new User(response.getString("name"), response.getString("pass"));
                            // Проверяем, если юзера нет в базе, но он есть на сервере
                            // добавляем его в базу
                            if (!noteDBHelper.updateUser(user.getUsername(), true)) {
                                noteDBHelper.addUser(user, true);
                            }
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra("name", user.getUsername());
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Log.i("TAG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignInActivity.this,
                        "User does not exist or the incorrect password is entered!",
                        Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonRequest);
        requestQueue.start();

    }

    public void registrationClick(View v) {
        Intent intent = new Intent(SignInActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}
