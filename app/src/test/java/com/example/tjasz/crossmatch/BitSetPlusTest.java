package com.example.tjasz.crossmatch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class BitSetPlusTest {

    private void assertArrayEqualsHex(long[] expected, long[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i)
        {
            assertEquals("expected: 0x" + String.format("%x", expected[i]) +
                    " but was: 0x" + String.format("%x", actual[i]), expected[i], actual[i]);
        }
    }

    @Test
    public void init() {
        BitSetPlus bs = new BitSetPlus(64);
        long[] expected = {};
        assertArrayEqualsHex(expected, bs.toLongArray());
    }
    @Test
    public void set_range_1() {
        BitSetPlus bs = new BitSetPlus(64);
        bs.set_range(0,1,1);
        long[] expected = {1L};
        assertArrayEqualsHex(expected, bs.toLongArray());
    }
    @Test
    public void set_range_2() {
        BitSetPlus bs = new BitSetPlus(64);
        bs.set_range(0,2,2);
        long[] expected = {2L};
        assertArrayEqualsHex(expected, bs.toLongArray());
    }
    @Test
    public void set_range_offset() {
        BitSetPlus bs = new BitSetPlus(64);
        bs.set_range(0,4,2);
        bs.set_range(4,4,0xC);
        long[] expected = {0xC2L};
        assertArrayEqualsHex(expected, bs.toLongArray());
    }
}