package com.example.knitting.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.knitting.Fragments.ImageFragment;
import com.example.knitting.Fragments.PatternFragment;
import com.example.knitting.Pattern;

public class PatternFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Pattern", "Image" };
    private Context context;
    private Pattern pattern;

    public PatternFragmentPagerAdapter(FragmentManager fm, Context context, Pattern pattern) {
        super(fm);
        this.context = context;
        this.pattern = pattern;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new PatternFragment(pattern);
        } else {
            return new ImageFragment(pattern);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
