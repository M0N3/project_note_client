package com.monz.project_note.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Андрей on 26.01.2016.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
    }
    public void signInClick(View v){
        setContentView(R.layout.activity_main);
    }
    public void registrationClick(View v){
        setContentView(R.layout.registration_layout);
    }
}
