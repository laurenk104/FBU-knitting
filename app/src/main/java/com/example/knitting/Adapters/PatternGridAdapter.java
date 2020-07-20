package com.example.knitting.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternGridAdapter extends BaseAdapter {

    Context context;
    boolean[][] values;
    List<Boolean> unraveledPattern;

    public PatternGridAdapter(Context context, boolean[][] values) {
        this.context = context;
        this.values = values;
        unraveledPattern = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                unraveledPattern.add(values[i][j]);
            }
        }
    }

    @Override
    public int getCount() {
        return unraveledPattern.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView dummyTextView = new TextView(context);
        dummyTextView.setText(unraveledPattern.get(i).toString());
        return dummyTextView;
    }
}
