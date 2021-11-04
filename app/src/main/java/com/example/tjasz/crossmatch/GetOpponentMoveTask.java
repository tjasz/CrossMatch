package com.example.tjasz.crossmatch;

import android.os.AsyncTask;
import android.util.Pair;

import java.util.Calendar;

public class GetOpponentMoveTask extends AsyncTask<Void, Void, Pair<Integer, Double>> {
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
    protected Pair<Integer, Double> doInBackground(Void... nothing)
    {
        return Opponent.get_move(activity_.get_game_board(), activity_.get_search_depth(), activity_.computer_first(), activity_.get_mistake_prevalence());
    }

    @Override
    protected void onPostExecute(Pair<Integer, Double> result_pair)
    {
        activity_.get_game_board().play(result_pair.first);
        activity_.tell_optimal_ai_value(result_pair.second);
        activity_.update_display();
        activity_.tell_opponent_decision_time(Calendar.getInstance().getTimeInMillis() - start_time);
    }
}
