package com.example.tjasz.crossmatch;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the GameBoard functionality.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameBoardTest {
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
}
