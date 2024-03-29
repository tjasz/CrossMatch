package com.example.tjasz.crossmatch;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private Random random;
    private GridView gameboard_gridview;
    private ProgressBar progress_bar;
    private TextView game_status_textview;
    private Button new_game_button;

    private GameBoard game_board;
    private boolean use_ai;
    private boolean computer_first = false;

    public GameBoard get_game_board()
    {
        return game_board;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Intent intent = new Intent(GameActivity.this, HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game_board = new GameBoard(Preferences.get_board_size(this));
        random = new Random();

        game_status_textview = findViewById(R.id.game_status_textview);

        gameboard_gridview = findViewById(R.id.gameboard_gridview);
        gameboard_gridview.setAdapter(new ButtonAdapter(this));

        new_game_button = findViewById(R.id.newgame_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GameActivity.this.new_game();
                if (!game_board.game_over() && use_ai && computer_first)
                {
                    progress_bar.setVisibility(View.VISIBLE);
                    game_status_textview.setVisibility(View.INVISIBLE);
                    new GetOpponentMoveTask(GameActivity.this).execute();
                }
            }
        });

        progress_bar = (ProgressBar) findViewById(R.id.loading_wheel);

        update_display();

        new_game();
        if (!game_board.game_over() && use_ai && computer_first)
        {
            progress_bar.setVisibility(View.VISIBLE);
            game_status_textview.setVisibility(View.INVISIBLE);
            new GetOpponentMoveTask(GameActivity.this).execute();
        }
    }

    public int get_board_size()
    {
        return game_board.size();
    }

    public void new_game()
    {
        // set the decision time factor to an invalid value
        // use initial search depth until it can be based on data
        opponent_decision_time_factor = -1.0;
        use_ai = Preferences.get_use_computer_opponent(this);
        target_opponent_decision_time = Preferences.get_opponent_decision_time(this);
        // determine if computer goes first
        Preferences.FirstMove first_move = Preferences.get_first_move(this);
        if (Preferences.FirstMove.Human == first_move)
        {
            computer_first = false;
        }
        else if (Preferences.FirstMove.AI == first_move)
        {
            computer_first = true;
        }
        else if (Preferences.FirstMove.Alternating == first_move)
        {
            computer_first = !computer_first;
        }
        else if (Preferences.FirstMove.Random == first_move)
        {
            computer_first = (random.nextInt() %2 == 0);
        }
        else if (Preferences.FirstMove.Winner == first_move)
        {
            if ((GameBoard.GameState.PlayerOneWon == game_board.game_state()) == computer_first)
            {
                computer_first = true;
            }
            else
            {
                computer_first = false;
            }
        }
        else if (Preferences.FirstMove.Loser == first_move)
        {
            if ((GameBoard.GameState.PlayerOneWon == game_board.game_state()) == computer_first)
            {
                computer_first = false;
            }
            else
            {
                computer_first = true;
            }
        }
        game_board.set_size(Preferences.get_board_size(this));
        game_board.init_game();
        update_display();
    }

    public void update_display()
    {
        gameboard_gridview.setNumColumns(game_board.size());
        ButtonAdapter adapter = (ButtonAdapter) gameboard_gridview.getAdapter();
        Pair<Integer, Integer> gameover_color_and_string = CategoryDisplay.gameover_color_and_string(
                game_board.game_state(), use_ai, computer_first
        );
        game_status_textview.setTextColor(gameover_color_and_string.first);
        game_status_textview.setText(gameover_color_and_string.second);

        if (game_board.is_fresh_board())
        {
            game_status_textview.setText(getString(R.string.last_tile) + "-");
            game_status_textview.setTextColor(Color.BLACK);
        }
        else if (!game_board.game_over()) {
            char last_tile_char = CategoryDisplay.second_dim_to_char(
                    game_board.get_cell_second_category(game_board.get_last_tile()));
            // Add emoji to display AI's assessment of game.
            // * If perfect play will result in AI win, display devil emoji.
            // * If perfect play will result in draw, display flat face.
            // * If perfect play will lead to user win, display crying emoji.
            // * If unknown outcome due to insufficient search depth, display no emoji.
            String emoji = "";
            if (get_mistake_prevalence() > 5 && optimal_ai_value >= 1)
            {
                emoji = computer_first ? "\uD83D\uDE22" : "\uD83D\uDE08";
            }
            else if (get_mistake_prevalence() > 5 && optimal_ai_value == 0)
            {
                emoji = "\uD83D\uDE10";
            }
            else if (optimal_ai_value <= -1)
            {
                emoji = computer_first ? "\uD83D\uDE08" : "\uD83D\uDE22";
            }
            game_status_textview.setText(emoji + getString(R.string.last_tile) +
                    Character.toString(last_tile_char));
            game_status_textview.setTextColor(CategoryDisplay.first_dim_to_color(
                    game_board.get_cell_first_category(game_board.get_last_tile())));
        }
        adapter.notifyDataSetChanged();
        progress_bar.setVisibility(View.INVISIBLE);
        game_status_textview.setVisibility(View.VISIBLE);
    }

    private long target_opponent_decision_time = 2000;
    private double opponent_decision_time_factor;
    private int current_search_depth;

    public void tell_opponent_decision_time(long millis)
    {
        Log.i("DECISION", "millis: " + Long.toString(millis));
        Log.i("DECISION", "current_search_depth: " + Integer.toString(current_search_depth));
        // time is proportionate to branching_factor ^ depth
        // so t = k * b^d
        // solving for k: k = t / (b^d)
        opponent_decision_time_factor = millis / Math.pow(game_board.max_branching_factor(), current_search_depth);
        Log.i("DECISION", "opponent_decision_time_factor: " + Double.toString(opponent_decision_time_factor));
    }

    private double optimal_ai_value;

    public void tell_optimal_ai_value(double optimal_value)
    {
        optimal_ai_value = optimal_value;
        Log.i("DECISION", "optimal_value: " + Double.toString(optimal_ai_value));
    }

    private int get_initial_search_depth()
    {
        double estimated_factor = 3;
        current_search_depth = (int) Math.round((Math.log(target_opponent_decision_time) - Math.log(estimated_factor))
                / Math.log(game_board.max_branching_factor()));
        return current_search_depth;
    }

    public boolean computer_first()
    {
        return computer_first;
    }

    public int get_search_depth()
    {
        // use initial search depth until it can be based on data
        if (opponent_decision_time_factor < 0)
        {
            current_search_depth = get_initial_search_depth();
            return current_search_depth;
        }
        // time is proportionate to branching_factor ^ depth
        // so t = k * b^d
        // solving for depth: d = (ln t - ln k)/(ln b)
        // TODO k can be factored out of these; maybe not if branching factor varies, though
        // ln k = ln t - d ln b
        // so d_1 = (ln t_1 - ln t_0) / (ln b) + d_0

        current_search_depth = (int) Math.round((Math.log(target_opponent_decision_time) - Math.log(opponent_decision_time_factor))
                / Math.log(game_board.max_branching_factor()));
        Log.i("DECISION", "new search depth: " + Integer.toString(current_search_depth));
        return current_search_depth;
    }

    int get_mistake_prevalence()
    {
        return Preferences.get_mistake_prevalence(this);
    }



    // http://www.stealthcopter.com/blog/2010/09/android-creating-a-custom-adapter-for-gridview-buttonadapter/
    public class ButtonAdapter extends BaseAdapter {
        private GameActivity mActivity;

        // Gets the context so it can be used later
        public ButtonAdapter(GameActivity c) {
            mActivity = c;
        }

        // Total number of things contained within the adapter
        public int getCount() {
            return mActivity.game_board.size() * mActivity.game_board.size();
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
            if (mActivity.game_board.is_valid_move(position))
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
                    if (!mActivity.game_board.game_over() && use_ai)
                    {
                        progress_bar.setVisibility(View.VISIBLE);
                        game_status_textview.setVisibility(View.INVISIBLE);
                        new GetOpponentMoveTask(GameActivity.this).execute();
                    }
                }
            });
            // set background color based on cell state
            GameBoard.CellState cell_state = mActivity.game_board.get_cell_state(position);
            // set the style of the tile based on state and enabled/disabled
            btn.setBackgroundResource(
                    CategoryDisplay.tile_background_resource(cell_state,
                            btn.isEnabled(),
                            position == mActivity.game_board.get_last_tile(),
                            computer_first));

            return btn;
        }
    }
}
