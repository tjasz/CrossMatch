package com.example.tjasz.crossmatch;

import android.util.Log;

import java.io.PipedOutputStream;

public class Opponent {
    public static int get_move(GameBoard board)
    {
        if (!board.is_player_two_turn())
        {
            throw new RuntimeException("Opponent.get_move() called on incorrect turn.");
        }

        // first maximizing step is outside, to get best move from it
        int best_move = 0; // TODO GameBoard should except when opponent play illegal moves
        double max_value = Double.NEGATIVE_INFINITY;
        // iterate over the possible moves
        for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
        {
            if (board.is_valid_move(i))
            {
                GameBoard copy = new GameBoard(board);
                copy.play(i);
                // TODO parameterize depth
                double value = alphabeta(copy, 3, max_value, Double.POSITIVE_INFINITY, false);
                if (value >= max_value)
                {
                    best_move = i;
                    max_value = value;
                }
            }
        }
        return best_move;
    }

    // TODO name local variables in this function better
    // https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    private static double alphabeta(GameBoard board, int depth, double alpha, double beta, boolean maximize)
    {
        if (depth == 0 || board.game_over())
        {
            double val = get_board_value(board);
            // Log.d("BOARD_VALUE", Double.toString(val));
            return val;
        }

        double v;
        if (maximize)
        {
            v = Double.NEGATIVE_INFINITY;
            // iterate over the possible moves
            for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
            {
                if (board.is_valid_move(i))
                {
                    GameBoard copy = new GameBoard(board);
                    copy.play(i);
                    v = Math.max(v, alphabeta(copy, depth-1, alpha, beta, !maximize));
                    alpha = Math.max(alpha, v);
                    if (alpha > beta)
                    {
                        break;
                    }
                }
            }
            return v;
        }
        else // minimizing
        {
            v = Double.POSITIVE_INFINITY;
            // iterate over the possible moves
            for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
            {
                if (board.is_valid_move(i))
                {
                    GameBoard copy = new GameBoard(board);
                    copy.play(i);
                    v = Math.min(v, alphabeta(copy, depth-1, alpha, beta, !maximize));
                    beta = Math.min(beta, v);
                    if (alpha > beta)
                    {
                        break;
                    }
                }
            }
            return v;
        }
    }
    private static double get_board_value(GameBoard board)
    {
        if (board.game_over())
        {
            // player 2 won - positive value
            if (board.is_player_one_turn())
            {
                return board.unclaimed_tiles();
            }
            // player 1 won - negative value
            return -1.0 * board.unclaimed_tiles();
        }
        // heuristic evaluation based on longest unblocked clusters
        int total_seq_lens = 0;
        for (int seq_len = board.size() - 1; seq_len > 0; seq_len--)
        {
            // if player one has an unblocked sequence at this size, subtract a corresponding fractional value
            int num_p1_sequences = board.sequence_length_counts.get(-seq_len+board.size());
            if (num_p1_sequences > 0)
            {
                total_seq_lens += -num_p1_sequences*seq_len*seq_len;
            }
            // same for player two (Opponent, positive)
            int num_p2_sequences = board.sequence_length_counts.get(seq_len+board.size());
            if (num_p2_sequences > 0)
            {
                total_seq_lens += num_p2_sequences*seq_len*seq_len;;
            }
        }
        return total_seq_lens/( (double) board.num_clusters()*board.size()*board.size());
    }
}
