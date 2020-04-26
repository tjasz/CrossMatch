package com.example.tjasz.crossmatch;

import android.os.AsyncTask;

import java.util.Calendar;

public class GetOpponentMoveTask extends AsyncTask<Void, Void, Integer> {
    private GameActivity activity_;
    long start_time;

    public GetOpponentMoveTask(GameActivity activity)
    {
        activity_ = activity;
    }

    @Override
    protected void onPreExecute()
    {
        start_time = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected Integer doInBackground(Void... nothing)
    {
        return Opponent.get_move(activity_.get_game_board(), activity_.get_search_depth(), true);
    }

    @Override
    protected void onPostExecute(Integer move_index)
    {
        activity_.get_game_board().play(move_index);
        activity_.update_display();
        activity_.tell_opponent_decision_time(Calendar.getInstance().getTimeInMillis() - start_time);
    }
}
