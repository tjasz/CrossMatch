package com.example.tjasz.crossmatch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class NewGameDialog extends Dialog implements View.OnClickListener {
    public MainActivity main_activity;
    private NumberPicker boardsize_getter;

    public NewGameDialog(MainActivity a) {
        super(a);
        main_activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_game);

        boardsize_getter = (NumberPicker) findViewById(R.id.boardsize_getter);
        int max_board_size = main_activity.getResources().obtainTypedArray(R.array.category_colors).length();
        boardsize_getter.setMinValue(GameBoard.min_size);
        boardsize_getter.setMaxValue(max_board_size);
        boardsize_getter.setValue(main_activity.get_board_size());
        boardsize_getter.setWrapSelectorWheel(false);
        boardsize_getter.refreshDrawableState();

        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                main_activity.set_board_size_and_start_game(boardsize_getter.getValue());
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
