package com.example.tjasz.crossmatch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewGameDialog extends Dialog implements View.OnClickListener {
    public MainActivity main_activity;
    public Button start;

    public NewGameDialog(MainActivity a) {
        super(a);
        // TODO Auto-generated constructor stub
        main_activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_game);
        start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                main_activity.new_game();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
