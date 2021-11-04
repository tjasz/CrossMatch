package com.example.tjasz.crossmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    private RadioButton radio_p2_ai;
    private RadioButton radio_p2_human;
    private RadioButton radio_first_move_ai;
    private RadioButton radio_first_move_human;
    private RadioButton radio_first_move_alternating;
    private RadioButton radio_first_move_random;
    private RadioButton radio_first_move_winner;
    private RadioButton radio_first_move_loser;
    private NumberPicker boardsize_getter;
    private NumberPicker difficulty_getter;
    private LinearLayout ai_options_group;
    private LinearLayout boardsize_group;
    private Button button_done;

    private static String[] range_to_displayed_values(int lo, int hi)
    {
        String[] retval = new String[]{"Cakewalk", "Easy", "Moderate", "Difficult", "Diabolical"};
        return retval;
    }

    private static String[] range_to_boardsize_values(int lo, int hi)
    {
        String[] retval = new String[hi - lo + 1];
        for (int i = 0; i < retval.length; ++i)
        {
            retval[i] = (i+lo) + "x" + (i+lo);
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
        boardsize_getter.setMinValue(GameBoard.min_size);
        boardsize_getter.setMaxValue(CategoryDisplay.MAX_BOARD_SIZE);
        boardsize_getter.setValue(Preferences.get_board_size(this));
        boardsize_getter.setWrapSelectorWheel(false);
        boardsize_getter.setDisplayedValues(range_to_boardsize_values(
                boardsize_getter.getMinValue(),
                boardsize_getter.getMaxValue()));
        boardsize_getter.refreshDrawableState();

        boardsize_group = (LinearLayout) findViewById(R.id.boardsize_group);
        button_done = (Button) findViewById(R.id.button_done);
        button_done.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boardsize_group.setVisibility(View.VISIBLE);
                return true;
            }
        });

        difficulty_getter = (NumberPicker) findViewById(R.id.dectime_getter);
        difficulty_getter.setMinValue(2);
        difficulty_getter.setMaxValue(6);
        int difficulty = Preferences.get_mistake_prevalence(this);
        difficulty_getter.setValue(difficulty);
        difficulty_getter.setWrapSelectorWheel(false);
        difficulty_getter.setDisplayedValues(range_to_displayed_values(
                difficulty_getter.getMinValue(),
                difficulty_getter.getMaxValue()));
        difficulty_getter.refreshDrawableState();

        radio_first_move_ai = (RadioButton) findViewById(R.id.radio_first_move_ai);
        radio_first_move_human = (RadioButton) findViewById(R.id.radio_first_move_human);
        radio_first_move_alternating = (RadioButton) findViewById(R.id.radio_first_move_alternating);
        radio_first_move_random = (RadioButton) findViewById(R.id.radio_first_move_random);
        radio_first_move_winner = (RadioButton) findViewById(R.id.radio_first_move_winner);
        radio_first_move_loser = (RadioButton) findViewById(R.id.radio_first_move_loser);
        Preferences.FirstMove first_move = Preferences.get_first_move(this);
        switch(first_move)
        {
            case Human:
                radio_first_move_human.setChecked(true);
                break;
            case AI:
                radio_first_move_ai.setChecked(true);
                break;
            case Alternating:
                radio_first_move_alternating.setChecked(true);
                break;
            case Random:
                radio_first_move_random.setChecked(true);
                break;
            case Winner:
                radio_first_move_winner.setChecked(true);
                break;
            case Loser:
                radio_first_move_loser.setChecked(true);
                break;
        }

        ai_options_group = (LinearLayout) findViewById(R.id.ai_options_group);
        ai_options_group.setVisibility(use_ai ? View.VISIBLE : View.GONE);

    }

    public void onP2RadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_p2_ai:
                if (checked)
                    ai_options_group.setVisibility(View.VISIBLE);
                    break;
            case R.id.radio_p2_human:
                if (checked)
                    ai_options_group.setVisibility(View.GONE);
                    break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Preferences.set_mistake_prevalence(this,
                difficulty_getter.getValue());
        Preferences.set_board_size(this, boardsize_getter.getValue());
        Preferences.set_use_computer_opponent(this, radio_p2_ai.isChecked());
        Preferences.FirstMove first_move;
        first_move = Preferences.FirstMove.Human;
        if (radio_first_move_ai.isChecked())
        {
            first_move = Preferences.FirstMove.AI;
        }
        else if (radio_first_move_alternating.isChecked())
        {
            first_move = Preferences.FirstMove.Alternating;
        }
        else if (radio_first_move_random.isChecked())
        {
            first_move = Preferences.FirstMove.Random;
        }
        else if (radio_first_move_winner.isChecked())
        {
            first_move = Preferences.FirstMove.Winner;
        }
        else if (radio_first_move_loser.isChecked())
        {
            first_move = Preferences.FirstMove.Loser;
        }
        Preferences.set_first_move(this, first_move);
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
