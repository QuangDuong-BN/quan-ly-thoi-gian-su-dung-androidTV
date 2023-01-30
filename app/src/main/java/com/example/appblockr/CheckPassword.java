package com.example.appblockr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
    }

    public void onClickSubmit1(View view) {

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String password = preferences.getString("password", "");


        EditText editText = (EditText) findViewById(R.id.password);

        String input_password = editText.getText().toString();
        if (input_password.equals(password)) {
            Toast.makeText(CheckPassword.this, "Thanh Cong", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(CheckPassword.this, ShowAllApps
                    .class);
            startActivity(myIntent);
        } else
            Toast.makeText(CheckPassword.this, "Mat Khau Sai", Toast.LENGTH_SHORT).show();

    }

}