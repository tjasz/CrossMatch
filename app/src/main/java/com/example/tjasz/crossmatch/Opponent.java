package com.example.tjasz.crossmatch;

public class Opponent {
    public static int get_move(GameBoard board)
    {
        if (!board.is_player_two_turn())
        {
            throw new RuntimeException("Opponent.get_move() called on incorrect turn.");
        }
        // TODO make sophisticated choice with minimax, alphabeta pruning, heuristic
        for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
        {
            if (board.is_valid_move(i))
            {
                return i;
            }
        }
        return 0;
    }
}
