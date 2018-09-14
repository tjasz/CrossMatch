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

    private static SharedPreferences get_overall(Context context)
    {
        return context.getSharedPreferences(overall_key, MODE_PRIVATE);
    }

    private static SharedPreferences.Editor get_editor(Context context)
    {
        return get_overall(context).edit();
    }

    public static int get_board_size(Context context)
    {
        SharedPreferences preferences = get_overall(context);
        return preferences.getInt(board_size_key, default_board_size);
    }

    public static void set_board_size(Context context, int size)
    {
        SharedPreferences.Editor editor = get_editor(context);
        editor.putInt(board_size_key, size);
        editor.commit();
    }

    public static int get_opponent_decision_time(Context context)
    {
        SharedPreferences preferences = get_overall(context);
        return preferences.getInt(opponent_decision_time_key, default_opponent_decision_time);
    }

    public static void set_opponent_decision_time(Context context, int decision_time)
    {
        SharedPreferences.Editor editor = get_editor(context);
        editor.putInt(opponent_decision_time_key, decision_time);
        editor.commit();
    }

    public static boolean get_use_computer_opponent(Context context)
    {
        SharedPreferences preferences = get_overall(context);
        return preferences.getBoolean(use_ai_key, default_use_ai);
    }

    public static void set_use_computer_opponent(Context context, boolean use_ai)
    {
        SharedPreferences.Editor editor = get_editor(context);
        editor.putBoolean(use_ai_key, use_ai);
        editor.commit();
    }
}
