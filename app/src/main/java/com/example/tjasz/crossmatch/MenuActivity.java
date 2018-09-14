package com.example.tjasz.crossmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    private static int index_value_to_dec_time(int x)
    {
        return (int) Math.pow(2, x);
    }

    private static int dec_time_to_index_value(int x)
    {
        return (int) (Math.log(x) / Math.log(2));
    }

    private static String[] range_to_displayed_values(int lo, int hi)
    {
        String[] retval = new String[hi - lo + 1];
        for (int i = 0; i < retval.length; ++i)
        {
            int seconds = index_value_to_dec_time(i);
            if (seconds >= 60)
            {
                retval[i] = seconds / 60 + "m " + seconds % 60 + "s";
            }
            else
            {
                retval[i] = index_value_to_dec_time(i) + " sec";
            }
        }
        return retval;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_play:
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.button_help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
