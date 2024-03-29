package com.example.tjasz.crossmatch;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the GameBoard functionality.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameBoardTest {
    private void assertArrayEqualsHex(long[] expected, long[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i)
        {
            assertEquals("expected: 0x" + String.format("%x", expected[i]) +
                    " but was: 0x" + String.format("%x", actual[i]), expected[i], actual[i]);
        }
    }

    private void assertEqualsGB(GameBoard a, GameBoard b)
    {
        assertEquals(a.size(), b.size());
        assertEquals(a.unclaimed_tiles(), b.unclaimed_tiles());
        assertEquals(a.game_state(), b.game_state());
        assertEquals(a.get_last_tile(), b.get_last_tile());

        for (int i = 0; i < a.size()*a.size(); ++i)
        {
            assertEquals(a.get_cell_first_category(i), b.get_cell_first_category(i));
            assertEquals(a.get_cell_second_category(i), b.get_cell_second_category(i));
            assertEquals(a.get_cell_state(i), b.get_cell_state(i));
        }
    }

    @Test
    public void set_size() {
        GameBoard gb = new GameBoard(4);
        assertEquals(4, gb.size());

        gb.set_size(6);
        assertEquals(6, gb.size());
    }

    @Test
    public void factors() {
        // when a square is possible, both factors should be sqrt(size())
        GameBoard gb = new GameBoard(1);
        assertEquals(1, gb.smaller_factor());
        assertEquals(1, gb.larger_factor());
        gb.set_size(4);
        assertEquals(2, gb.smaller_factor());
        assertEquals(2, gb.larger_factor());
        gb.set_size(9);
        assertEquals(3, gb.smaller_factor());
        assertEquals(3, gb.larger_factor());

        // when the size() is prime, only 1 x size() rectangles are possible
        gb.set_size(2);
        assertEquals(1, gb.smaller_factor());
        assertEquals(2, gb.larger_factor());
        gb.set_size(3);
        assertEquals(1, gb.smaller_factor());
        assertEquals(3, gb.larger_factor());
        gb.set_size(5);
        assertEquals(1, gb.smaller_factor());
        assertEquals(5, gb.larger_factor());

        // when one type of larger rectangle is possible, the factors should be that
        gb.set_size(6);
        assertEquals(2, gb.smaller_factor());
        assertEquals(3, gb.larger_factor());
        gb.set_size(8);
        assertEquals(2, gb.smaller_factor());
        assertEquals(4, gb.larger_factor());
        gb.set_size(10);
        assertEquals(2, gb.smaller_factor());
        assertEquals(5, gb.larger_factor());

        // when multiple larger rectangles are possible, they chosen ones should be closer to square
        // ex: 12 should be factored as 3x4 and not 2x6
        gb.set_size(12);
        assertEquals(3, gb.smaller_factor());
        assertEquals(4, gb.larger_factor());
        gb.set_size(18);
        assertEquals(3, gb.smaller_factor());
        assertEquals(6, gb.larger_factor());
        gb.set_size(20);
        assertEquals(4, gb.smaller_factor());
        assertEquals(5, gb.larger_factor());
    }

    @Test
    public void num_clusters() {
        // The number of clusters are the total number of winning clusters of size size():
        // the number of rows, columns, both diagonals, and rectangles.
        // Use the same set of squares, primes, simple factors, and multi factors as in the factors() test.

        // for a 1x1 board, there is only 1 square
        GameBoard gb = new GameBoard(1);
        assertEquals(1, gb.num_clusters());
        // for a 2x2 board, there are 2 rows, 2 columns, 2 diagonals
        gb.set_size(2);
        assertEquals(6, gb.num_clusters());
        // for a 3x3 board, there are 3 rows, 3 columns, 2 diagonals
        gb.set_size(3);
        assertEquals(8, gb.num_clusters());
        // for a 4x4 board, there are 4 rows, 4 columns, 2 diagonals, and 9 squares of 2x2
        gb.set_size(4);
        assertEquals(19, gb.num_clusters());
        // for a 5x5 board, there are 5 rows, 5 columns, 2 diagonals, and no rectangles
        gb.set_size(5);
        assertEquals(12, gb.num_clusters());
        // for a 6x6 board, there are 6 rows, 6 columns, 2 diagonals, 20 2x3 rectangles, and 20 3x2 rectangles
        gb.set_size(6);
        assertEquals(54, gb.num_clusters());
        // for a 8x8 board, there are 8 rows, 8 columns, 2 diagonals, 35 2x4 rectangles, and 35 4x2 rectangles
        gb.set_size(8);
        assertEquals(88, gb.num_clusters());
        // for a 9x9 board, there are 9 rows, 9 columns, 2 diagonals, and 49 squares of 3x3
        gb.set_size(9);
        assertEquals(69, gb.num_clusters());
        // for a 10x10 board, there are 10 rows, 10 columns, 2 diagonals, 54 2x5 rectangles, and 54 5x2 rectangles
        gb.set_size(10);
        assertEquals(130, gb.num_clusters());
        // for a 12x12 board, there are 12 rows, 12 columns, 2 diagonals, 90 3x4 rectangles, and 90 4x3 rectangles
        gb.set_size(12);
        assertEquals(206, gb.num_clusters());
    }

    @Test
    public void is_edge() {
        // Edge cells are at the beginning or end of rows or columns.

        GameBoard gb = new GameBoard(1);
        assertTrue(gb.is_edge(0));

        gb.set_size(2);
        assertTrue(gb.is_edge(0));
        assertTrue(gb.is_edge(1));
        assertTrue(gb.is_edge(2));
        assertTrue(gb.is_edge(3));

        gb.set_size(3);
        // first row
        assertTrue(gb.is_edge(0));
        assertTrue(gb.is_edge(1));
        assertTrue(gb.is_edge(2));
        // second row; middle cell is not an edge
        assertTrue(gb.is_edge(3));
        assertFalse(gb.is_edge(4));
        assertTrue(gb.is_edge(5));
        // last row
        assertTrue(gb.is_edge(6));
        assertTrue(gb.is_edge(7));
        assertTrue(gb.is_edge(8));


        gb.set_size(4);
        // first row
        assertTrue(gb.is_edge(0));
        assertTrue(gb.is_edge(1));
        assertTrue(gb.is_edge(2));
        assertTrue(gb.is_edge(3));
        // second row; middle two cells are not edges
        assertTrue(gb.is_edge(4));
        assertFalse(gb.is_edge(5));
        assertFalse(gb.is_edge(6));
        assertTrue(gb.is_edge(7));
        // third row; middle two cells are not edges
        assertTrue(gb.is_edge(8));
        assertFalse(gb.is_edge(9));
        assertFalse(gb.is_edge(10));
        assertTrue(gb.is_edge(11));
        // last row
        assertTrue(gb.is_edge(12));
        assertTrue(gb.is_edge(13));
        assertTrue(gb.is_edge(14));
        assertTrue(gb.is_edge(15));
    }

    @Test
    public void playtest3x3() {
        GameBoard gb = new GameBoard(3);
        // Leave commented out; do not shuffle the category assignments
        //gb.init_game();

        // ensure all cells are unclaimed
        for (int i = 0; i < gb.size() * gb.size(); ++i)
        {
            assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(i));
        }

        // ensure only edges are valid moves
        // first row
        assertTrue(gb.is_valid_move(0));
        assertTrue(gb.is_valid_move(1));
        assertTrue(gb.is_valid_move(2));
        // second row; middle cell is not an edge, not a valid move
        assertTrue(gb.is_valid_move(3));
        assertFalse(gb.is_valid_move(4));
        assertTrue(gb.is_valid_move(5));
        // last row
        assertTrue(gb.is_valid_move(6));
        assertTrue(gb.is_valid_move(7));
        assertTrue(gb.is_valid_move(8));

        // check initial state
        assertTrue(gb.is_fresh_board());
        assertTrue(gb.is_player_one_turn());
        assertFalse(gb.is_player_two_turn());
        assertEquals(8, gb.current_legal_moves());
        assertEquals(9, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

        gb.play(0);
        // ensure cell states
        assertEquals(GameBoard.CellState.PlayerOne, gb.get_cell_state(0));
        for (int i = 1; i < gb.size() * gb.size(); ++i)
        {
            assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(i));
        }
        // check game state
        assertFalse(gb.is_fresh_board());
        assertFalse(gb.is_player_one_turn());
        assertTrue(gb.is_player_two_turn());
        assertEquals(4, gb.current_legal_moves());
        assertEquals(8, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

        gb.play(1);
        // ensure cell states
        assertEquals(GameBoard.CellState.PlayerOne, gb.get_cell_state(0));
        assertEquals(GameBoard.CellState.PlayerTwo, gb.get_cell_state(1));
        for (int i = 2; i < gb.size() * gb.size(); ++i)
        {
            assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(i));
        }
        // check game state
        assertFalse(gb.is_fresh_board());
        assertTrue(gb.is_player_one_turn());
        assertFalse(gb.is_player_two_turn());
        assertEquals(3, gb.current_legal_moves());
        assertEquals(7, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

        gb.play(4);
        // ensure cell states
        assertEquals(GameBoard.CellState.PlayerOne, gb.get_cell_state(0));
        assertEquals(GameBoard.CellState.PlayerTwo, gb.get_cell_state(1));
        assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(2));
        assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(3));
        assertEquals(GameBoard.CellState.PlayerOne, gb.get_cell_state(4));
        for (int i = 5; i < gb.size() * gb.size(); ++i)
        {
            assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(i));
        }
        // check game state
        assertFalse(gb.is_fresh_board());
        assertFalse(gb.is_player_one_turn());
        assertTrue(gb.is_player_two_turn());
        assertEquals(3, gb.current_legal_moves());
        assertEquals(6, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

        gb.play(5);
        // check game state
        assertEquals(3, gb.current_legal_moves());
        assertEquals(5, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

        gb.play(8);
        // check game state
        assertEquals(0, gb.current_legal_moves());
        assertEquals(4, gb.unclaimed_tiles());
        assertTrue(gb.game_over());
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());

        // test to_bitset
        BitSetPlus bits = gb.to_bitset();
        long[] actual = bits.toLongArray();
        //                    8 1 0 0 2 1 0 0 2 1  8   7   6   5   4   3   2   1   0    3            8
        long[] expected = {0b00_010000100100001001_100001110110010101000011001000010000_00000011L, 0b000010L};
        assertArrayEqualsHex(expected, actual);
        GameBoard copy = GameBoard.from_bitset(bits);
        assertEqualsGB(gb, copy);

        // re-init and check initial state
        gb.init_game();

        // ensure all cells are unclaimed
        for (int i = 0; i < gb.size() * gb.size(); ++i)
        {
            assertEquals(GameBoard.CellState.Unclaimed, gb.get_cell_state(i));
        }

        // ensure only edges are valid moves
        // first row
        assertTrue(gb.is_valid_move(0));
        assertTrue(gb.is_valid_move(1));
        assertTrue(gb.is_valid_move(2));
        // second row; middle cell is not an edge, not a valid move
        assertTrue(gb.is_valid_move(3));
        assertFalse(gb.is_valid_move(4));
        assertTrue(gb.is_valid_move(5));
        // last row
        assertTrue(gb.is_valid_move(6));
        assertTrue(gb.is_valid_move(7));
        assertTrue(gb.is_valid_move(8));

        // check initial state
        assertTrue(gb.is_fresh_board());
        assertTrue(gb.is_player_one_turn());
        assertFalse(gb.is_player_two_turn());
        assertEquals(8, gb.current_legal_moves());
        assertEquals(9, gb.unclaimed_tiles());
        assertFalse(gb.game_over());
        assertEquals(GameBoard.GameState.InProgress, gb.game_state());

    }

    @Test
    public void bitset_conversion() {
        GameBoard gb = new GameBoard(2);
        BitSetPlus bits = gb.to_bitset();
        long[] arr = bits.toLongArray();
        //                   -1       0 0 0 0  3 2 1 0  2
        long[] expected = {0b11111111_00000000_11100100_00000010L};
        assertArrayEqualsHex(expected, arr);
        GameBoard copy = GameBoard.from_bitset(bits);
        assertEqualsGB(gb, copy);

        gb.set_size(3);
        bits = gb.to_bitset();
        arr = bits.toLongArray();
        //                    -1       0 0 0 0 0 0 0 0 0  8   7   6   5   4   3   2   1   0    3      -1
        long[] expected3 = {0b11_000000000000000000_100001110110010101000011001000010000_00000011L, 0b111111L};
        assertArrayEqualsHex(expected3, arr);
        copy = GameBoard.from_bitset(bits);
        assertEqualsGB(gb, copy);

        gb.set_size(4);
        bits = gb.to_bitset();
        arr = bits.toLongArray();
        long[] expected4 = {0xDCBA9876543210_04L, 0xFF_00000000_FEL};
        assertArrayEqualsHex(expected4, arr);
        copy = GameBoard.from_bitset(bits);
        assertEqualsGB(gb, copy);
    }

    @Test
    public void determine_game_state() {
        GameBoard gb = new GameBoard(4);
        BitSetPlus bits = gb.to_bitset();
        long[] arr = bits.toLongArray();

        long[] expected4 = {0xDCBA9876543210_04L, 0xFF_00000000_FEL};
        assertArrayEqualsHex(expected4, arr);

        // bits at offset 72 through 104 are the cell states
        // set column 0 to player 1
        bits.set_range(72, 32, 0x010101a9);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());
        // set column 1 to player 1
        bits.set_range(72, 32, 0x040404a6);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());
        // set column 2 to player 1
        bits.set_range(72, 32, 0x1010109a);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());
        // set column 3 to player 1
        bits.set_range(72, 32, 0x4040406a);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());
        // set row 0 to player 2
        bits.set_range(72, 32, 0x010101aa);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerTwoWon, gb.game_state());
        // set row 1 to player 2
        bits.set_range(72, 32, 0x0101aa01);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerTwoWon, gb.game_state());
        // set row 2 to player 2
        bits.set_range(72, 32, 0x01aa0101);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerTwoWon, gb.game_state());
        // set row 3 to player 2
        bits.set_range(72, 32, 0xaa010101);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerTwoWon, gb.game_state());
        // set negative diagonal to player 1
        bits.set_range(72, 32, 0x0104106a);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerOneWon, gb.game_state());
        // set positive diagonal to player 2
        bits.set_range(72, 32, 0x80200856);
        gb = GameBoard.from_bitset(bits);
        assertEquals(GameBoard.GameState.PlayerTwoWon, gb.game_state());
        // TODO squares, rectangles, last move has no followup
        // TODO draw game
        // TODO in progress
    }
}
