package com.example.tjasz.crossmatch;

import android.graphics.Color;
import android.util.Pair;

public class CategoryDisplay {
    // TODO this max, array, and first function are useless
    // GameActivity.first_dim_to_color() is used instead
    // because accessing the color array resource requires context
    public static final int MAX_COLORS = 6;
    private static final int colors[] = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
    };
    public static int first_dim_to_color(int index)
    {
        if (index < 0 || index >= GameBoard.size())
        {
            throw new RuntimeException("Index out of range of board size.");
        }
        if (index < 0 || index >= MAX_COLORS)
        {
            throw new RuntimeException("Index out of range of possible colors.");
        }
        return colors[index];
    }
    public static char second_dim_to_char(int index)
    {
        if (index < 0 || index >= GameBoard.size())
        {
            throw new RuntimeException("Index out of range of board size.");
        }
        return (char) (index + '1');
    }

    public static int tile_background_resource(GameBoard.CellState cell_state, boolean enabled, boolean last_tile)
    {
        if (cell_state == GameBoard.CellState.Unclaimed)
        {
            if (enabled)
            {
                return R.drawable.tile_unclaimed_enabled;
            }
            else
            {
                return R.drawable.tile_unclaimed_disabled;
            }
        }
        else if (cell_state == GameBoard.CellState.PlayerOne)
        {
            if (last_tile)
            {
                return R.drawable.tile_claimed_p1_highlighted;
            }
            else
            {
                return R.drawable.tile_claimed_p1;
            }
        }
        else if (cell_state == GameBoard.CellState.PlayerTwo)
        {
            if (last_tile)
            {
                return R.drawable.tile_claimed_p2_highlighted;
            }
            else
            {
                return R.drawable.tile_claimed_p2;
            }
        }
        return R.drawable.tile_unclaimed_disabled;
    }

    public static Pair<Integer, Integer> gameover_color_and_string(GameBoard.GameState game_state, boolean use_ai)
    {
        switch (game_state)
        {
            case InProgress:
                return Pair.create(Color.BLACK, R.string.empty);
            case PlayerOneWon:
                return Pair.create(Color.WHITE, use_ai ? R.string.you_win : R.string.p1_wins);
            case PlayerTwoWon:
                return Pair.create(Color.BLACK, use_ai ? R.string.you_lose : R.string.p2_wins);
            case Draw:
                return Pair.create(Color.BLACK, R.string.draw);
        }
        return Pair.create(Color.BLACK, R.string.empty);
    }
}
