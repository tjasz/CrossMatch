package com.example.tjasz.crossmatch;

import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;

public class GameBoard {
    // construct from an integer, another GameBoard, or by default
    public GameBoard(int size)
    {
        cell_states = new ArrayList<>();
        category_assignments = new ArrayList<>();
        sequence_length_counts = new ArrayList<>();
        set_size(size);
    }
    public GameBoard(GameBoard source)
    {
        this(source.size());
        for (int i = 0; i < size()*size(); ++i)
        {
            cell_states.set(i, source.cell_states.get(i));
            category_assignments.set(i, source.category_assignments.get(i));
        }
        moves_ = source.moves_;
        game_state_ = source.game_state_;
        last_tile_ = source.last_tile_;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass())
        {
            return false;
        }
        GameBoard rhs = (GameBoard) obj;
        Boolean result = (size_ == rhs.size_ && moves_ == rhs.moves_ &&
                game_state_ == rhs.game_state_ && last_tile_ == rhs.last_tile_);
        for (int i = 0; i < size()*size(); ++i)
        {
            result = (result && category_assignments.get(i) == rhs.category_assignments.get(i) &&
                    cell_states.get(i) == rhs.cell_states.get(i));
        }
        return result;
    }
    @Override
    public int hashCode()
    {
        return to_bitset().hashCode();
    }

    public BitSetPlus to_bitset()
    {
        assert(size() <= 16);
        int catassign_bits = 8;
        if (size() <= 1)
        {
            catassign_bits = 1;
        }
        else if (size() <= 2)
        {
            catassign_bits = 2;
        }
        else if (size() <= 4)
        {
            catassign_bits = 4;
        }
        else if (size() <= 5)
        {
            catassign_bits = 5;
        }
        else if (size() <= 8)
        {
            catassign_bits = 6;
        }
        else if (size() <= 11)
        {
            catassign_bits = 7;
        }
        int nbits = 8 + // 8 for the board size
                catassign_bits * size()*size() + // grid of category assignments
                2 * size()*size() + // grid of cell states
                8; // last tile
        int offset = 0;

        BitSetPlus result = new BitSetPlus(nbits);

        Log.d("BITSET", String.format("adding %d bits at offset %d: %x", 8, offset, size()));
        result.set_range(offset, 8, size());
        offset += 8;
        for (int i = 0; i < size()*size(); ++i)
        {
            int category = category_assignments.get(i).byteValue();
            Log.d("BITSET", String.format("adding %d bits at offset %d: %x", catassign_bits, offset, category));
            result.set_range(offset, catassign_bits, category);
            offset += catassign_bits;
        }
        for (int i = 0; i < size()*size(); ++i)
        {
            int cell = cell_states.get(i).ordinal();
            Log.d("BITSET", String.format("adding %d bits at offset %d: %x", 2, offset, cell));
            result.set_range(offset, 2, cell);
            offset += 2;
        }
        result.set_range(offset, 8, last_tile_);
        offset += 8;

        return result;
    }

    public static GameBoard from_bitset(BitSetPlus bs)
    {
        int offset = 0;
        int size = bs.get_range(offset, 8);
        offset += 8;
        GameBoard gb = new GameBoard(size);
        assert(gb.size() <= 16);

        int catassign_bits = 8;
        if (gb.size() <= 1)
        {
            catassign_bits = 1;
        }
        else if (gb.size() <= 2)
        {
            catassign_bits = 2;
        }
        else if (gb.size() <= 4)
        {
            catassign_bits = 4;
        }
        else if (gb.size() <= 5)
        {
            catassign_bits = 5;
        }
        else if (gb.size() <= 8)
        {
            catassign_bits = 6;
        }
        else if (gb.size() <= 11)
        {
            catassign_bits = 7;
        }

        for (int i = 0; i < gb.size()*gb.size(); ++i)
        {
            int cat = bs.get_range(offset, catassign_bits);
            assert(cat <= gb.size()*gb.size());
            gb.category_assignments.set(i, cat);
            offset += catassign_bits;
        }
        gb.moves_ = 0;
        for (int i = 0; i < gb.size()*gb.size(); ++i)
        {
            int cs_int = bs.get_range(offset, 2);
            assert(cs_int >= 0 && cs_int < CellState.values().length);
            GameBoard.CellState cell_state = GameBoard.CellState.values()[cs_int];
            gb.cell_states.set(i, cell_state);
            if (cell_state != CellState.Unclaimed)
            {
                gb.moves_ += 1;
            }
            offset += 2;
        }
        int last_tile = bs.get_range(offset, 8);
        // TODO this won't work if size actually ever is 16
        gb.last_tile_ = last_tile > gb.size()*gb.size() - 1 ? -1 : last_tile;
        offset += 8;

        gb.game_state_ = gb.determine_game_state();

        return gb;
    }

    // manage the positive size of the square game board
    public static final int min_size = 2;
    private int size_;
    private int smaller_factor_ = -1;
    public int size()
    {
        return size_;
    }
    public void set_size(int new_size)
    {
        if (new_size > 0) {
            size_ = new_size;
            smaller_factor_ = -1;

            // add cell state and category arrays
            cell_states.clear();
            category_assignments.clear();
            final int array_size = size()*size();
            for (int i = 0; i < array_size; ++i)
            {
                category_assignments.add(i);
                cell_states.add(CellState.Unclaimed);
            }
            moves_ = 0;
            last_tile_ = -1;
            game_state_ = GameState.InProgress;
        }
        else
        {
            throw new RuntimeException("An attempt to set a non-positive board size was observed.");
        }
    }
    public int smaller_factor()
    {
        if (smaller_factor_ > 0)
        {
            return smaller_factor_;
        }
        int upper_bound = (int) Math.floor(Math.sqrt(size()));
        for (int candidate = upper_bound; candidate > 1; candidate--)
        {
            if (candidate * (size() / candidate) == size())
            {
                smaller_factor_ = candidate;
                return candidate;
            }
        }
        return 1;
    }
    public int larger_factor()
    {
        return size() / smaller_factor();
    }
    public int num_clusters()
    {
        if (size() == 1)
        {
            return 1;
        }
        int result = 2*size() + 2; // rows, columns, diagonals
        if (smaller_factor() != 1) // do not double count the columns or rows
        {
            // larger_factor() by smaller_factor() clusters
            result += (size()-smaller_factor()+1)*(size()-larger_factor()+1);
            if (smaller_factor() != larger_factor()) // do not double count when looking for squares
            {
                // smaller_factor() by larger_factor() clusters
                result += (size()-larger_factor()+1)*(size()-smaller_factor()+1);
            }
        }
        return result;
    }

    public static enum GameState
    {
        InProgress,
        PlayerOneWon,
        PlayerTwoWon,
        Draw
    }

    public static enum CellState
    {
        Unclaimed,
        PlayerOne,
        PlayerTwo
    }

    // The game board consists of square 2d grids in both space and category
    // that are here represented by 1d arrays

    // tracks the state of each cell in a size() by size() spatial grid
    private ArrayList<CellState> cell_states;
    // tracks the category number of each of those cells
    // effectively mapping from the spatial grid to the categorical grid
    private ArrayList<Integer> category_assignments;

    public void init_game()
    {
        // initialize all cells to unclaimed to begin
        final int array_size = size()*size();
        for (int i = 0; i < array_size; ++i)
        {
            cell_states.set(i, CellState.Unclaimed);
        }
        // shuffle the mapping of spatial grid onto the categorical grid
        java.util.Collections.shuffle(category_assignments);

        // set the number of moves to 0
        moves_ = 0;
        last_tile_ = -1;
        game_state_ = GameState.InProgress;
    }

    public void init_game(int new_size)
    {
        if (new_size != size())
        {
            set_size(new_size);
        }
        init_game();
    }

    public boolean is_edge(int position)
    {
        return (position / size() == 0) || (position / size() == size() - 1) ||
               (position % size() == 0) || (position % size() == size() - 1);
    }

    // get the state of a cell
    public CellState get_cell_state(int index)
    {
        if (index < 0 || index >= size()*size())
        {
            throw new RuntimeException("First dimension index out of range.");
        }
        return cell_states.get(index);
    }

    // get the state of a cell as represented in the 2d spatial grid
    public CellState get_cell_state(int first_dim, int second_dim)
    {
        if (first_dim < 0 || first_dim >= size())
        {
            throw new RuntimeException("First dimension index out of range.");
        }
        if (second_dim < 0 || second_dim >= size())
        {
            throw new RuntimeException("Second dimension index out of range.");
        }
        return get_cell_state(first_dim*size()+second_dim);
    }

    // get the index of the cell in the first categorical dimension
    public int get_cell_first_category(int index)
    {
        if (index < 0 || index >= size()*size())
        {
            throw new RuntimeException("Index out of range.");
        }
        return category_assignments.get(index) / size();
    }

    // get the index of the cell in the first categorical dimension
    public int get_cell_first_category(int first_dim, int second_dim)
    {
        if (first_dim < 0 || first_dim >= size())
        {
            throw new RuntimeException("First dimension index out of range.");
        }
        if (second_dim < 0 || second_dim >= size())
        {
            throw new RuntimeException("Second dimension index out of range.");
        }
        return get_cell_first_category(first_dim*size()+second_dim);
    }

    // get the index of the cell in the second categorical dimension
    public int get_cell_second_category(int index)
    {
        if (index < 0 || index >= size()*size())
        {
            throw new RuntimeException("Index out of range.");
        }
        return category_assignments.get(index) % size();
    }

    // get the index of the cell in the second categorical dimension
    public int get_cell_second_category(int first_dim, int second_dim)
    {
        if (first_dim < 0 || first_dim >= size())
        {
            throw new RuntimeException("First dimension index out of range.");
        }
        if (second_dim < 0 || second_dim >= size())
        {
            throw new RuntimeException("Second dimension index out of range.");
        }
        return get_cell_second_category(first_dim*size()+second_dim);
    }

    public boolean is_valid_move(int index)
    {
        if (index < 0 || index >= size()*size())
        {
            throw new RuntimeException("Index out of range.");
        }
        if (game_over())
        {
            return false;
        }
        if (get_cell_state(index) != CellState.Unclaimed)
        {
            return false;
        }
        if (is_fresh_board())
        {
            return is_edge(index);
        }
        return get_cell_first_category(index) == get_cell_first_category(get_last_tile()) ||
                get_cell_second_category(index) == get_cell_second_category(get_last_tile());
    }


    private int moves_;
    private GameState game_state_;
    public ArrayList<Integer> sequence_length_counts;
    public boolean is_player_one_turn()
    {
        return moves_ % 2 == 0;
    }
    public boolean is_player_two_turn()
    {
        return !is_player_one_turn();
    }
    public boolean is_fresh_board()
    {
        return moves_ == 0;
    }
    public int current_legal_moves()
    {
        if (is_fresh_board())
        {
            return 4*(size() - 1);
        }
        int count = 0;
        for (int i = 0; i < size()*size(); ++i)
        {
            if (is_valid_move(i))
            {
                count++;
            }
        }
        return count;
    }
    public int unclaimed_tiles()
    {
        return size()*size() - moves_;
    }
    public int max_branching_factor()
    {
        // each tile has 2*size() - 1 neighbors,
        // but on any move except the first, at least one has been claimed already
        // the branching factor is less than or equal to 2*size - 2
        // but it must also be less than or equal to the total number of unclaimed tiles
        return Math.min(unclaimed_tiles(), 2*size() - 2);
    }
    // helper to check how many tiles in a cluster are claimed by one player, with the others unclaimed
    // these are unblocked potential wins
    // negative values for player one; positive for player two
    private int length_of_unblocked_sequence(ArrayList<CellState> states)
    {
        if (states.size() != size())
        {
            throw new RuntimeException("Size of states list given to unblocked_sequence() is incorrect.");
        }

        int seq_len = 0;
        CellState first_seen_player = CellState.Unclaimed;
        for (int i = 0; i < states.size(); i++)
        {
            if (states.get(i) != CellState.Unclaimed)
            {
                if (first_seen_player == CellState.Unclaimed)
                {
                    first_seen_player = states.get(i);
                }

                if (first_seen_player == states.get(i))
                {
                    seq_len++;
                }
                else
                {
                    return 0;
                }
            }
        }
        if (first_seen_player == CellState.PlayerOne)
        {
            seq_len = -seq_len;
        }
        return seq_len;
    }
    public boolean game_over()
    {
        return game_state() != GameState.InProgress;
    }
    public GameState game_state()
    {
        return game_state_;
    }
    private GameState determine_game_state()
    {

        int current_seq_len;
        // the sequence length counts give the counts of unblocked segments of length
        // -size(), -size()+1, ... -1, 0, 1, ... size()-1, size()
        // only gets fully populated when game is not over
        sequence_length_counts.clear();
        for (int i = 0; i < 2*size() + 1; i++)
        {
            sequence_length_counts.add(0);
        }
        // game is also over if any rows are wholly claimed by a single player
        for (int row = 0; row < size(); row++)
        {
            ArrayList<CellState> row_states = new ArrayList<>();
            for (int cell = 0; cell < size(); cell++)
            {
                row_states.add(get_cell_state(row, cell));
            }
            current_seq_len = length_of_unblocked_sequence(row_states);
            sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
            if (current_seq_len == -size())
            {
                Log.d("GAMEOVER","Row " + Integer.toString(row) + " claimed by player one");
                return GameState.PlayerOneWon;
            }
            if (current_seq_len == size())
            {
                Log.d("GAMEOVER","Row " + Integer.toString(row) + " claimed by player two");
                return GameState.PlayerTwoWon;
            }
        }
        // game is also over if any columns are wholly claimed by a single player
        for (int col = 0; col < size(); col++)
        {
            ArrayList<CellState> col_states = new ArrayList<>();
            for (int cell = 0; cell < size(); cell++)
            {
                col_states.add(get_cell_state(cell, col));
            }
            current_seq_len = length_of_unblocked_sequence(col_states);
            sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
            if (current_seq_len == -size())
            {
                Log.d("GAMEOVER","Column " + Integer.toString(col) + " claimed by player one");
                return GameState.PlayerOneWon;
            }
            if (current_seq_len == size())
            {
                Log.d("GAMEOVER","Column " + Integer.toString(col) + " claimed by player two");
                return GameState.PlayerTwoWon;
            }
        }
        // game is also over if the positive diagonal is wholly claimed by a single player
        ArrayList<CellState> diag_states = new ArrayList<>();
        for (int col = 0; col < size(); col++)
        {
            diag_states.add(get_cell_state(col, col));
        }
        current_seq_len = length_of_unblocked_sequence(diag_states);
        sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
        if (current_seq_len == -size())
        {
            Log.d("GAMEOVER","Positive diagonal claimed by player one");
            return GameState.PlayerOneWon;
        }
        if (current_seq_len == size())
        {
            Log.d("GAMEOVER","Positive diagonal claimed by player two");
            return GameState.PlayerTwoWon;
        }
        // game is also over if the negative diagonal is wholly claimed by a single player
        diag_states.clear();
        for (int col = 0; col < size(); col++)
        {
            diag_states.add(get_cell_state(col, size()-col-1));
        }
        current_seq_len = length_of_unblocked_sequence(diag_states);
        sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
        if (current_seq_len == -size())
        {
            Log.d("GAMEOVER","Negative diagonal claimed by player one");
            return GameState.PlayerOneWon;
        }
        if (current_seq_len == size())
        {
            Log.d("GAMEOVER","Negative diagonal claimed by player two");
            return GameState.PlayerTwoWon;
        }
        // game is also over if any of the
        // larger_factor() by smaller_factor() clusters are wholly claimed by a single player
        // do not check when size() is prime, as the clusters would be equivalent to rows/columns
        if (smaller_factor() != 1) {
            for (int row = 0; row <= size() - smaller_factor(); row++) {
                for (int col = 0; col <= size() - larger_factor(); col++) {
                    ArrayList<CellState> cluster_states = new ArrayList<>();
                    for (int i = 0; i < smaller_factor(); i++) {
                        for (int j = 0; j < larger_factor(); j++) {
                            cluster_states.add(get_cell_state(row + i, col + j));
                        }
                    }
                    current_seq_len = length_of_unblocked_sequence(cluster_states);
                    sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
                    if (current_seq_len == -size())
                    {
                        Log.d("GAMEOVER", "Cluster of size " +
                                Integer.toString(larger_factor()) + " by " +
                                Integer.toString(smaller_factor()) + " at (" +
                                Integer.toString(row) + ", " + Integer.toString(col) + ") claimed by player one");
                        return GameState.PlayerOneWon;
                    }
                    if (current_seq_len == size())
                    {
                        Log.d("GAMEOVER", "Cluster of size " +
                                Integer.toString(larger_factor()) + " by " +
                                Integer.toString(smaller_factor()) + " at (" +
                                Integer.toString(row) + ", " + Integer.toString(col) + ") claimed by player two");
                        return GameState.PlayerTwoWon;
                    }
                }
            }
            // when size() is not an even square, game is also over if any of the
            // smaller_factor() by larger_factor() clusters are wholly claimed by a single player
            if (larger_factor() != smaller_factor()) {
                for (int row = 0; row <= size() - larger_factor(); row++) {
                    for (int col = 0; col <= size() - smaller_factor(); col++) {
                        ArrayList<CellState> cluster_states = new ArrayList<>();
                        for (int i = 0; i < larger_factor(); i++) {
                            for (int j = 0; j < smaller_factor(); j++) {
                                cluster_states.add(get_cell_state(row + i, col + j));
                            }
                        }
                        current_seq_len = length_of_unblocked_sequence(cluster_states);
                        sequence_length_counts.set(current_seq_len + size(), sequence_length_counts.get(current_seq_len + size())+1);
                        if (current_seq_len == -size())
                        {
                            Log.d("GAMEOVER", "Cluster of size " +
                                    Integer.toString(smaller_factor()) + " by " +
                                    Integer.toString(larger_factor()) + " at (" +
                                    Integer.toString(row) + ", " + Integer.toString(col) + ") claimed");
                            return GameState.PlayerOneWon;
                        }
                        if (current_seq_len == size())
                        {
                            Log.d("GAMEOVER", "Cluster of size " +
                                    Integer.toString(smaller_factor()) + " by " +
                                    Integer.toString(larger_factor()) + " at (" +
                                    Integer.toString(row) + ", " + Integer.toString(col) + ") claimed");
                            return GameState.PlayerTwoWon;
                        }
                    }
                }
            }
        }
        if (current_legal_moves() == 0)
        {
            Log.d("GAMEOVER","No moves left");
            if (unclaimed_tiles() > 0)
            {
                if (is_player_two_turn())
                {
                    return GameState.PlayerOneWon;
                }
                else
                {
                    return GameState.PlayerTwoWon;
                }
            }
            return GameState.Draw;
        }

        return GameState.InProgress;
    }

    private int last_tile_;
    public int get_last_tile()
    {
        return last_tile_;
    }

    public void play(int index)
    {
        if (index < 0 || index >= size()*size())
        {
            throw new RuntimeException("Index out of range.");
        }
        if (!is_valid_move(index))
        {
            throw new RuntimeException("Invalid move played. Last tile categories (" +
                    get_cell_first_category(last_tile_) + ", " + get_cell_second_category(last_tile_) +
                    ") and played categories (" +
                    get_cell_first_category(index) + ", " + get_cell_second_category(index) + "): " + category_assignments);
        }
        if (is_player_one_turn())
        {
            cell_states.set(index, CellState.PlayerOne);
        }
        else
        {
            cell_states.set(index, CellState.PlayerTwo);
        }
        moves_++;
        last_tile_ = index;
        game_state_ = determine_game_state();
    }
}
