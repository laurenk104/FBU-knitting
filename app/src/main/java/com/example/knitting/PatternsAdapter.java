package com.example.knitting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PatternsAdapter extends RecyclerView.Adapter<PatternsAdapter.ViewHolder> {

    Context context;
    List<Pattern> patterns;

    // Pass in the context and list of patterns
    public  PatternsAdapter(Context context, List<Pattern> patterns) {
        this.context = context;
        this.patterns = patterns;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pattern, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Pattern pattern = patterns.get(position);
        // Bind the tweet with view holder
        holder.bind(pattern);
    }

    @Override
    public int getItemCount() {
        return patterns.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(Pattern pattern) {
            tvName.setText(pattern.getName());
            Log.d("image", "" + pattern);
            Glide.with(context).load(pattern.getImage().getUrl()).into(ivImage);
        }
    }
}
