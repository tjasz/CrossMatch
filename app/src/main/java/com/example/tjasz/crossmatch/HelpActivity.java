package com.example.tjasz.crossmatch;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class HelpActivity extends AppCompatActivity  {

    private static final int NUM_PAGES = 7;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void finishWrapper(View v) {
        finish();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HelpFragment.newInstance(
                            R.string.help_header_play,
                            R.string.help_text_play,
                            R.drawable.help_neighbor);
                case 1:
                    return HelpFragment.newInstance(
                            R.string.help_header_win,
                            R.string.help_text_win,
                            R.drawable.help_win_row);
                case 2:
                    return HelpFragment.newInstance(
                            R.string.help_header_win,
                            R.string.help_text_win_col,
                            R.drawable.help_win_col);
                case 3:
                    return HelpFragment.newInstance(
                            R.string.help_header_win,
                            R.string.help_text_win_diag,
                            R.drawable.help_win_diag);
                case 4:
                    return HelpFragment.newInstance(
                            R.string.help_header_win,
                            R.string.help_text_win_square,
                            R.drawable.help_win_square);
                case 5:
                    return HelpFragment.newInstance(
                            R.string.help_header_win,
                            R.string.help_text_win_noop,
                            R.drawable.help_win_noop);
                case 6:
                    return HelpFragment.newInstance(
                            R.string.help_header_draw,
                            R.string.help_text_draw,
                            R.drawable.help_draw);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
