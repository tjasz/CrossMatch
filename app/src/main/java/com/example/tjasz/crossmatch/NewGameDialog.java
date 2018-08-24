package com.example.tjasz.crossmatch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class NewGameDialog extends Dialog implements View.OnClickListener {
    public MainActivity main_activity;
    private SeekBar seekbar;

    public NewGameDialog(MainActivity a) {
        super(a);
        // TODO Auto-generated constructor stub
        main_activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_game);

        seekbar = (SeekBar) findViewById(R.id.boardsize_seekbar);
        int max_board_size = main_activity.getResources().obtainTypedArray(R.array.category_colors).length();
        seekbar.setMax(max_board_size - GameBoard.min_size);

        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                main_activity.set_board_size_and_start_game(seekbar.getProgress()  + GameBoard.min_size);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
