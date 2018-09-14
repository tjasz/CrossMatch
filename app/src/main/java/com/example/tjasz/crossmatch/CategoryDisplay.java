package com.example.tjasz.crossmatch;

import android.graphics.Color;

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
}
