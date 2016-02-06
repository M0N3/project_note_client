package com.monz.project_note.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Андрей on 30.01.2016.
 */
public class SignInActivity extends Activity {
    private EditText login;
    private EditText password;
    private User user;
    private RequestQueue requestQueue;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
        requestQueue = Volley.newRequestQueue(this);
        url = getString(R.string.server_url);
    }

    public void signInClick(View v) {
        login = (EditText) findViewById(R.id.loginEdit);
        password = (EditText) findViewById(R.id.passwordEdit);
        if (password.length() == 0 || login.length() == 0) {
            Toast.makeText(this, "Wrong Input!", Toast.LENGTH_LONG).show();
            return;
        }
        JsonRequest jsonRequest = new JsonObjectRequest(
                StringRequest.Method.POST, url + "/signin",
                "{ \"name\": \"" + login.getText().toString() + "\", \"pass\": \"" + password.getText().toString() + "\"}",
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("TAG", "User " + response.getString("name") + " authenticated successfully");
                    user = new User(response.getString("name"), response.getString("pass"));
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
                Log.i("TAG", "Authentication error "+ error.toString());
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
