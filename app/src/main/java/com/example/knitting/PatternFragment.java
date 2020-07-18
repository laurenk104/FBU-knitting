package com.example.knitting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.parceler.Parcels;

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
        PatternGridAdapter adapter = new PatternGridAdapter(getContext(), values);
        gridPattern.setAdapter(adapter);
        gridPattern.setNumColumns(values[0].length);
    }
}
