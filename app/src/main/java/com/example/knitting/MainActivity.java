package com.example.knitting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private RecyclerView rvPatterns;
    private PatternsAdapter adapter;
    List<Pattern> patterns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patterns = new ArrayList<>();

        rvPatterns = findViewById(R.id.rvPatterns);
        adapter = new PatternsAdapter(this, patterns);
        // Recycler view setup: layout manager and the adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPatterns.setLayoutManager(layoutManager);
        rvPatterns.setAdapter(adapter);

        queryPatterns();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void queryPatterns() {
        ParseQuery<Pattern> query = ParseQuery.getQuery(Pattern.class);
        query.include(Pattern.KEY_USER);
       // query.setLimit(1);
        query.addDescendingOrder(Pattern.KEY_UPDATED);
        query.findInBackground(new FindCallback<Pattern>() {
            @Override
            public void done(List<Pattern> receivedPatterns, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                patterns.addAll(receivedPatterns);
                adapter.notifyDataSetChanged();
            }
        });
    }
}