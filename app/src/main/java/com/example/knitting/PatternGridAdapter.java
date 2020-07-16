package com.example.knitting;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

public class PatternGridAdapter extends BaseAdapter {

    Context context;
    List<List<Boolean>> values;

    public PatternGridAdapter(Context context, List<List<Boolean>> values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
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
        dummyTextView.setText(values.get(i).toString());
        return dummyTextView;
    }
}
