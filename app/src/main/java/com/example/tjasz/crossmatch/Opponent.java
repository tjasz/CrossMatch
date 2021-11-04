package com.example.tjasz.crossmatch;

import android.util.Log;
import android.util.Pair;

import java.io.PipedOutputStream;
import java.util.Random;
import java.util.Vector;

public class Opponent {
    static Random rand;

    public static Pair<Integer, Double> get_move(GameBoard board, int depth, boolean is_player_one, int mistake_prevalence)
    {
        if (board.is_player_one_turn() != is_player_one)
        {
            throw new RuntimeException("Opponent.get_move() called on incorrect turn.");
        }

        // if it's time for a random mistake, choose it instead of the best move
        rand = new Random();
        if (mistake_prevalence <= 5 && rand.nextDouble() < 1.0 / mistake_prevalence)
        {
            Log.i("DECISION", "making mistake!");
            Vector<Integer> moves = new Vector<Integer>();
            for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i) {
                if (board.is_valid_move(i)) {
                    moves.add(i);
                }
            }
            return new Pair<Integer, Double>(moves.elementAt(rand.nextInt(moves.size())), 0.0);
        }

        // first maximizing step is outside, to get best move from it
        double optimal_value;
        int best_move = 0; // TODO GameBoard should except when opponent play illegal moves
        if (is_player_one) // minimize
        {
            optimal_value = Double.POSITIVE_INFINITY;
            // iterate over the possible moves
            for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
            {
                if (board.is_valid_move(i))
                {
                    GameBoard copy = new GameBoard(board);
                    copy.play(i);
                    // TODO parameterize depth
                    double value = alphabeta(copy, depth, Double.NEGATIVE_INFINITY, optimal_value, true);
                    if (value < optimal_value)
                    {
                        best_move = i;
                        optimal_value = value;
                    }
                }
            }
        }
        else // maximize
        {
            optimal_value = Double.NEGATIVE_INFINITY;
            // iterate over the possible moves
            for (int i = 0; i < GameBoard.size()*GameBoard.size(); ++i)
            {
                if (board.is_valid_move(i))
                {
                    GameBoard copy = new GameBoard(board);
                    copy.play(i);
                    // TODO parameterize depth
                    double value = alphabeta(copy, depth, optimal_value, Double.POSITIVE_INFINITY, false);
                    if (value >= optimal_value)
                    {
                        best_move = i;
                        optimal_value = value;
                    }
                }
            }
        }
        return new Pair<Integer, Double>(best_move, optimal_value);
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
        // if game is over, return a value based on the winner
        // and how long it took them to win
        // alternative: base value only on winner, and not on number of moves
        if (board.game_over())
        {
            // player 2 won - positive value. > 1
            if (board.game_state() == GameBoard.GameState.PlayerTwoWon)
            {
                // return 1 + the number of unclaimed tiles
                // adding one allows this to be a better outcome than a draw with value 0
                return 1.0 * (1 + board.unclaimed_tiles());
            }
            // player 1 won - negative value. < -1
            else if (board.game_state() == GameBoard.GameState.PlayerOneWon)
            {
                // return -1 * (1 + the number of unclaimed tiles)
                // adding one allows this to be a worse outcome than a draw with value 0
                return -1.0 * (1 + board.unclaimed_tiles());
            }
            // return 0 for a draw
            return 0.0;
        }
        // if game is in progress:
        // heuristic evaluation based on total unblocked clusters, weighted by length
        // absolute value should be less than 1
        // alternative: consider returning a value based on just the longest unblocked cluster
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
