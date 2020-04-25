package com.example.tjasz.crossmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    private RadioButton radio_p2_ai;
    private RadioButton radio_p2_human;
    private NumberPicker boardsize_getter;
    private NumberPicker dectime_getter;

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
        setContentView(R.layout.activity_settings);

        radio_p2_ai = (RadioButton) findViewById(R.id.radio_p2_ai);
        radio_p2_human = (RadioButton) findViewById(R.id.radio_p2_human);
        boolean use_ai = Preferences.get_use_computer_opponent(this);
        radio_p2_ai.setChecked(use_ai);
        radio_p2_human.setChecked(!use_ai);

        boardsize_getter = (NumberPicker) findViewById(R.id.boardsize_getter);
        int max_board_size = getResources().obtainTypedArray(R.array.category_colors).length();
        boardsize_getter.setMinValue(GameBoard.min_size);
        boardsize_getter.setMaxValue(max_board_size);
        boardsize_getter.setValue(Preferences.get_board_size(this));
        boardsize_getter.setWrapSelectorWheel(false);
        boardsize_getter.refreshDrawableState();

        dectime_getter = (NumberPicker) findViewById(R.id.dectime_getter);
        dectime_getter.setMinValue(0);
        dectime_getter.setMaxValue(7);
        int dectime = Preferences.get_opponent_decision_time(this);
        dectime_getter.setValue(dec_time_to_index_value((int) Math.round(dectime/1000.0)));
        dectime_getter.setWrapSelectorWheel(false);
        dectime_getter.setDisplayedValues(range_to_displayed_values(
                dectime_getter.getMinValue(),
                dectime_getter.getMaxValue()));
        dectime_getter.refreshDrawableState();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Preferences.set_opponent_decision_time(this,
                1000*index_value_to_dec_time(dectime_getter.getValue()));
        Preferences.set_board_size(this, boardsize_getter.getValue());
        Preferences.set_use_computer_opponent(this, radio_p2_ai.isChecked());
    }

    // TODO save settings when navigating up via home button or hardware back button

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_done:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }
    }
}
