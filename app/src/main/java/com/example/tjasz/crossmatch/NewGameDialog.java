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
        /*seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int boardsize = GameBoard.default_size;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                boardsize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/


        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                main_activity.set_board_size_and_start_game(seekbar.getProgress()  + GameBoard.default_size);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
