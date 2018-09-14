package com.example.tjasz.crossmatch;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {
    private static final String overall_key = "CrossMatchPreferences";
    private static final String board_size_key = "BoardSize";
    private static final String opponent_decision_time_key = "OpponentDecisionTime";
    private static final String use_ai_key = "UseComputerOpponent";

    private static final int default_board_size = 4;
    private static final int default_opponent_decision_time = 2000;
    private static final boolean default_use_ai = true;

    public static int get_board_size(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(overall_key, MODE_PRIVATE);
        return preferences.getInt(board_size_key, default_board_size);
    }

    public static int get_opponent_decision_time(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(overall_key, MODE_PRIVATE);
        return preferences.getInt(opponent_decision_time_key, default_opponent_decision_time);
    }

    public static boolean get_use_computer_opponent(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(overall_key, MODE_PRIVATE);
        return preferences.getBoolean(opponent_decision_time_key, default_use_ai);
    }
}
