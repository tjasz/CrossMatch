package com.example.tjasz.crossmatch;

import java.util.ArrayList;

public class GameBoard {
    // construct from an integer, another GameBoard, or by default
    public GameBoard(int size)
    {
        cell_states = new ArrayList<>();
        category_assignments = new ArrayList<>();
        init_game(size);
    }
    public GameBoard(GameBoard source)
    {
        this(source.size());
    }
    public GameBoard()
    {
        this(default_size);
    }

    // manage the positive size of the square game board
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
        cell_states.clear();
        category_assignments.clear();
        final int array_size = size()*size();
        for (int i = 0; i < array_size; ++i)
        {
            category_assignments.add(i);
            cell_states.add(CellState.Unclaimed);
        }
        // shuffle the mapping of spatial grid onto the categorical grid
        java.util.Collections.shuffle(category_assignments);

        // set the number of moves to 0
        moves_ = 0;
    }

    public void init_game(int new_size)
    {
        set_size(new_size);
        init_game();
    }

    public static boolean is_edge(int position)
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
        return category_assignments.get(first_dim*size()+second_dim);
    }


    private int moves_;
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
}
