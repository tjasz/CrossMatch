package com.example.tjasz.crossmatch;

import java.util.BitSet;

public class BitSetPlus extends BitSet {

    public BitSetPlus(int nbits) {
        super(nbits);
    }

    void set_range(int fromIndex, int len, int val)
    {
        int initial_mask = 0x01;
        for (int i = 0; i < len; ++i)
        {
            int mask = initial_mask << i;
            set(fromIndex+i, 0 != (val & mask));
        }
    }
}
