package com.example.tjasz.crossmatch;

import android.os.AsyncTask;

public class GetOpponentMoveTask extends AsyncTask<Void, Void, Integer> {
    private MainActivity activity_;
    public GetOpponentMoveTask(MainActivity activity)
    {
        activity_ = activity;
    }
    protected Integer doInBackground(Void... nothing)
    {
        return Opponent.get_move(activity_.get_game_board());
    }

    protected void onPostExecute(Integer move_index)
    {
        activity_.get_game_board().play(move_index);
        activity_.update_display();
    }
}
