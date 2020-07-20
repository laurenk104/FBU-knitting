package com.example.knitting.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.knitting.Pattern;
import com.example.knitting.Adapters.PatternGridAdapter;
import com.example.knitting.R;

import java.util.Arrays;

public class PatternFragment extends Fragment {

    GridView gridPattern;
    Pattern pattern;
    boolean[][] values;

    public PatternFragment(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pattern, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        values = pattern.getPattern();
        gridPattern = view.findViewById(R.id.gridPattern);
        final PatternGridAdapter adapter = new PatternGridAdapter(getContext(), values);
        gridPattern.setAdapter(adapter);
        gridPattern.setNumColumns(values[0].length);

        gridPattern.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                int row = position / gridPattern.getNumColumns();
                int column = position % gridPattern.getNumColumns();

                boolean stitch = values[row][column];
                values[row][column] = !stitch;
                pattern.setPattern(values);
                pattern.saveInBackground();

                adapter.clear();
                adapter.addAll(values);
            }
        });
    }
}
