package com.example.tjasz.crossmatch;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    private GridView gameboard_gridview;
    private Button new_game_button;

    private GameBoard game_board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        game_board = new GameBoard();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameboard_gridview = findViewById(R.id.gameboard_gridview);
        gameboard_gridview.setNumColumns(game_board.size());
        gameboard_gridview.setAdapter(new ButtonAdapter(this));

        new_game_button = findViewById(R.id.newgame_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new_game();
            }
        });
    }

    private void new_game()
    {
        game_board.init_game();
        update_display();
    }

    private void update_display()
    {
        ButtonAdapter adapter = (ButtonAdapter) gameboard_gridview.getAdapter();
        adapter.notifyDataSetChanged();
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
            Button btn;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                btn = new Button(mActivity);
                btn.setPadding(8, 8, 8, 8);
            }
            else {
                btn = (Button) convertView;
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
                }
            });

            return btn;
        }
    }
}
