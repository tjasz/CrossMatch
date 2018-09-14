package com.example.tjasz.crossmatch;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class NewGameDialog extends Dialog implements View.OnClickListener {
    public GameActivity game_activity;
    private NumberPicker boardsize_getter;
    private NumberPicker dectime_getter;

    public NewGameDialog(GameActivity a) {
        super(a);
        game_activity = a;
    }

    private static int index_value_to_dec_time(int x)
    {
        return (int) Math.pow(2, x);
    }

    private static int dec_time_to_index_value(int x)
    {
        return (int) (Math.log(x) / Math.log(2));
    }

    private static String[] range_to_displayed_values(int lo, int hi)
    {
        String[] retval = new String[hi - lo + 1];
        for (int i = 0; i < retval.length; ++i)
        {
            int seconds = index_value_to_dec_time(i);
            if (seconds >= 60)
            {
                retval[i] = seconds / 60 + "m " + seconds % 60 + "s";
            }
            else
            {
                retval[i] = index_value_to_dec_time(i) + " sec";
            }
        }
        return retval;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_game);

        boardsize_getter = (NumberPicker) findViewById(R.id.boardsize_getter);
        int max_board_size = game_activity.getResources().obtainTypedArray(R.array.category_colors).length();
        boardsize_getter.setMinValue(GameBoard.min_size);
        boardsize_getter.setMaxValue(max_board_size);
        boardsize_getter.setValue(Preferences.get_board_size(game_activity));
        boardsize_getter.setWrapSelectorWheel(false);
        boardsize_getter.refreshDrawableState();

        dectime_getter = (NumberPicker) findViewById(R.id.dectime_getter);
        dectime_getter.setMinValue(0);
        dectime_getter.setMaxValue(7);
        int dectime = Preferences.get_opponent_decision_time(game_activity);
        dectime_getter.setValue(dec_time_to_index_value((int) Math.round(dectime/1000.0)));
        dectime_getter.setWrapSelectorWheel(false);
        dectime_getter.setDisplayedValues(range_to_displayed_values(
                dectime_getter.getMinValue(),
                dectime_getter.getMaxValue()));
        dectime_getter.refreshDrawableState();

        Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                Preferences.set_opponent_decision_time(game_activity, 1000*index_value_to_dec_time(dectime_getter.getValue()));
                Preferences.set_board_size(game_activity, boardsize_getter.getValue());
                game_activity.new_game();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
