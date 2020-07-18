package com.example.knitting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageFragment extends Fragment {

    Pattern pattern;
    ImageView ivDetailImage;

    public ImageFragment(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivDetailImage = view.findViewById(R.id.ivDetailImage);
        Glide.with(getContext()).load(pattern.getImage().getUrl()).into(ivDetailImage);
    }
}