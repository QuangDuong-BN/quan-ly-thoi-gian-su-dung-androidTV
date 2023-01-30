package com.example.appblockr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CreatePassWord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass_word);
    }

    public void onClickSubmit(View view) {
        EditText password = (EditText) findViewById(R.id.password);
        EditText repeatpassword = (EditText) findViewById(R.id.repeatpassword);
        String text1 = password.getText().toString();
        String text2 = repeatpassword.getText().toString();

        if (text1.equals(text2)) {

            SharedPreferences preferences = getSharedPreferences("PREFERENCES" ,MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("password", text1);
            editor.apply();
            Toast.makeText(CreatePassWord.this, "Thanh Cong", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(CreatePassWord.this, MainActivity.class);
            startActivity(myIntent);

        } else
            Toast.makeText(CreatePassWord.this, "Mat khau khong trung khop", Toast.LENGTH_SHORT).show();

    }

}