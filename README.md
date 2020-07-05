# CrossMatch

This is the code repository for the Android game application
which was later branded as [Tesserekt](https://play.google.com/store/apps/details?id=com.tjasz.crossmatch).

Tessrekt is a turn-based game that can be described as something like
tic-tac-toe with Uno rules.

# Game Play

Start the game by tapping any tile
on the outer ring of the game board to claim it.
Players alternate turns claiming tiles.
A player must claim a tile that matches either the color or the numeral of the
previously claimed tile.

In the image below, the black player last claimed Yellow 1.
On its turn, the white player may claim any remaining 1 (Green and Blue)
or any remaining Yellow (2, 3, and 4).

![Game play illustration.](/app/src/main/res/drawable/help_neighbor.png)

# Winning

A player wins by claiming all the tiles in a row, column,
diagonal, or 2x2 square of tiles.
A player may also win by claiming a tile which cannot legally be followed.

In the image below, the white player has won by claiming
all tiles in the bottom row.

![Game play illustration.](/app/src/main/res/drawable/help_win_row.png)

In the image below, the white player has won by claiming
all tiles in the rightmost column.

![Game play illustration.](/app/src/main/res/drawable/help_win_col.png)

In the image below, the white player has won by claiming
all tiles on the negative diagonal.

![Game play illustration.](/app/src/main/res/drawable/help_win_diag.png)

In the image below, the white player has won by claiming
all tiles in the upper left 2x2 square.

![Game play illustration.](/app/src/main/res/drawable/help_win_square.png)

In the image below, the white player has won by claiming
the Green 3 tile, which is the last Green and last 3.
This leaves no legal moves for the black player's following turn.

![Game play illustration.](/app/src/main/res/drawable/help_win_noop.png)

# Draw Game

A draw game is possible if all 16 tiles have been claimed,
but no single row, column, diagonal, or 2x2 square has been claimed wholly by any player.

![Game play illustration.](/app/src/main/res/drawable/help_draw.png)
