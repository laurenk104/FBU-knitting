package com.example.knitting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.knitting.Pattern;
import com.example.knitting.R;

import java.util.List;

public class DefaultAdapter extends BaseAdapter {

    Context context;
    List<Pattern> patterns;

    // Pass in the context and list of patterns
    public  DefaultAdapter(Context context, List<Pattern> patterns) {
        this.context = context;
        this.patterns = patterns;
    }

    @Override
    public int getCount() {
        return patterns.size();
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
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_default, null);
        }

        TextView tvDefault = view.findViewById(R.id.tvDefault);
        ImageView ivDefault = view.findViewById(R.id.ivDefault);

        tvDefault.setText(patterns.get(i).getName());
        Glide.with(context).load(patterns.get(i).getImage().getUrl()).into(ivDefault);

        return view;
    }
}
