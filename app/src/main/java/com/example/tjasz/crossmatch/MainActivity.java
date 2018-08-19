package com.example.tjasz.crossmatch;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GridView gameboard_gridview;
    private ProgressBar progress_bar;
    private TextView game_status_textview;
    private Button new_game_button;

    private GameBoard game_board;

    public GameBoard get_game_board()
    {
        return game_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        game_board = new GameBoard();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game_status_textview = findViewById(R.id.game_status_textview);

        gameboard_gridview = findViewById(R.id.gameboard_gridview);
        gameboard_gridview.setNumColumns(game_board.size());
        gameboard_gridview.setAdapter(new ButtonAdapter(this));

        new_game_button = findViewById(R.id.newgame_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new_game();
            }
        });

        progress_bar = (ProgressBar) findViewById(R.id.loading_wheel);

        update_display();
    }

    private void new_game()
    {
        game_board.init_game();
        update_display();
    }

    public void update_display()
    {
        new_game_button.setEnabled(false); // disable button unless game is over
        ButtonAdapter adapter = (ButtonAdapter) gameboard_gridview.getAdapter();
        switch (game_board.game_state())
        {
            case InProgress:
                break;
            case PlayerOneWon:
                game_status_textview.setText(R.string.you_win);
                break;
            case PlayerTwoWon:
                game_status_textview.setText(R.string.you_lose);
                break;
            case Draw:
                game_status_textview.setText(R.string.draw);
                break;
        }
        if (game_board.game_over())
        {
            game_status_textview.setTextColor(Color.BLACK);
            new_game_button.setEnabled(true); // if game is over, can start a new one
        }
        else if (game_board.is_fresh_board())
        {
            game_status_textview.setText(getString(R.string.last_tile) + "-");
            game_status_textview.setTextColor(Color.BLACK);
        }
        else {
            char last_tile_char = CategoryDisplay.second_dim_to_char(
                    game_board.get_cell_second_category(game_board.get_last_tile()));
            game_status_textview.setText(getString(R.string.last_tile) +
                    Character.toString(last_tile_char));
            game_status_textview.setTextColor(CategoryDisplay.first_dim_to_color(
                    game_board.get_cell_first_category(game_board.get_last_tile())));
        }
        adapter.notifyDataSetChanged();
        progress_bar.setVisibility(View.INVISIBLE);
        game_status_textview.setVisibility(View.VISIBLE);
    }

    // http://www.stealthcopter.com/blog/2010/09/android-creating-a-custom-adapter-for-gridview-buttonadapter/
    public class ButtonAdapter extends BaseAdapter {
        private MainActivity mActivity;

        // Gets the context so it can be used later
        public ButtonAdapter(MainActivity c) {
            mActivity = c;
        }

        // Total number of things contained within the adapter
        public int getCount() {
            return GameBoard.size() * GameBoard.size();
        }

        // Require for structure, not really used in my code.
        public Button getItem(int position) {
            return null;
        }

        // Require for structure, not really used in my code. Can
        // be used to get the id of an item in the adapter for
        // manual control.
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position,
                            View convertView, ViewGroup parent) {
            SquareButton btn;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                btn = new SquareButton(mActivity);
                btn.setPadding(8, 8, 8, 8);
            }
            else {
                btn = (SquareButton) convertView;
            }
            btn.setTextColor(CategoryDisplay.first_dim_to_color(
                    mActivity.game_board.get_cell_first_category(position)));
            btn.setText(Character.toString(CategoryDisplay.second_dim_to_char(
                    mActivity.game_board.get_cell_second_category(position))));
            btn.setTypeface(null, Typeface.BOLD);
            btn.setId(position);
            // determine if button is enabled
            if (mActivity.game_board.is_player_one_turn() &&
                mActivity.game_board.is_valid_move(position))
            {
                btn.setEnabled(true);
            }
            else
            {
                btn.setEnabled(false);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mActivity.game_board.play(position);
                    update_display();
                    // TODO get opponent move asynchronously
                    if (!mActivity.game_board.game_over())
                    {
                        progress_bar.setVisibility(View.VISIBLE);
                        game_status_textview.setVisibility(View.INVISIBLE);
                        new GetOpponentMoveTask(MainActivity.this).execute();
                    }
                }
            });
            // set background color based on cell state
            // TODO map these with CategoryDisplay?
            GameBoard.CellState cell_state = mActivity.game_board.get_cell_state(position);
            // set the style of the tile based on state and enabled/disabled
            if (cell_state == GameBoard.CellState.Unclaimed)
            {
                if (btn.isEnabled())
                {
                    btn.setBackgroundResource(R.drawable.tile_unclaimed_enabled);
                }
                else
                {
                    btn.setBackgroundResource(R.drawable.tile_unclaimed_disabled);
                }
            }
            else if (cell_state == GameBoard.CellState.PlayerOne)
            {
                if (position == mActivity.game_board.get_last_tile())
                {
                    btn.setBackgroundResource(R.drawable.tile_claimed_p1_highlighted);
                }
                else
                {
                    btn.setBackgroundResource(R.drawable.tile_claimed_p1);
                }
            }
            else if (cell_state == GameBoard.CellState.PlayerTwo)
            {
                if (position == mActivity.game_board.get_last_tile())
                {
                    btn.setBackgroundResource(R.drawable.tile_claimed_p2_highlighted);
                }
                else
                {
                    btn.setBackgroundResource(R.drawable.tile_claimed_p2);
                }
            }

            return btn;
        }
    }
}
