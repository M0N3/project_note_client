package com.monz.project_note.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monz.project_note.app.R;
import com.monz.project_note.app.User;
import com.monz.project_note.app.database.NoteDBHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends Activity {

    private EditText login;

    private EditText password;

    private EditText password2;

    private User user;

    private RequestQueue requestQueue;

    private String url;

    private NoteDBHelper noteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        url = getString(R.string.server_url);
        requestQueue = Volley.newRequestQueue(this);
        noteDBHelper = new NoteDBHelper(this);
    }

    public void acceptClick(View v) {

        login = (EditText) findViewById(R.id.loginEdit);
        password = (EditText) findViewById(R.id.passwordEdit);
        password2 = (EditText) findViewById(R.id.password2Edit);

        // проверки на правильность ввода
        if (password.length() < 4 || login.length() < 4) {
            Toast.makeText(this, "Login or Password must be more than 4 characters!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            return;
        }

        // Запрос на сервер
        JsonRequest jsonRequest = new JsonObjectRequest(
                StringRequest.Method.PUT, url + "/registration",
                "{ \"name\": \"" + login.getText().toString() + "\", \"pass\": \"" + password.getText().toString() + "\"}",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("TAG", "User " + response.getString("name") + " created successfully");
                            user = new User(response.getString("name"), response.getString("pass"));
                            // Добавляем юзера в БД
                            noteDBHelper.addUser(user, true);
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
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
                Toast.makeText(RegistrationActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "Registration error. User exists "+ error.toString());
            }
        });
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }

}
