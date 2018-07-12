package com.example.tjasz.crossmatch;

public class GameBoard {
    public GameBoard()
    {
        set_size(default_size);
    }
    public GameBoard(int size)
    {
        set_size(size);
    }

    // manage the size of the square game board
    private static final int default_size = 6;
    private static int size_;
    public static int size()
    {
        return size_;
    }
    public static void set_size(int new_size)
    {
        if (new_size > 0) {
            size_ = new_size;
        }
        else
        {
            throw new RuntimeException("An attempt to set a non-positive board size was observed.");
        }
    }
}
