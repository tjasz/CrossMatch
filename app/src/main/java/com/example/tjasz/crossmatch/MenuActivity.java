package com.example.tjasz.crossmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void go_to_game(View v)
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}
