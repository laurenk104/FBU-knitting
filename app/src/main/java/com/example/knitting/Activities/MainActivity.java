package com.example.knitting.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.knitting.Activities.ComposeActivity;
import com.example.knitting.Activities.LoginActivity;
import com.example.knitting.Adapters.PatternsAdapter;
import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            // Navigate to the home activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.compose) {
            // Navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryPatterns() {
        ParseQuery<Pattern> query = ParseQuery.getQuery(Pattern.class);
        query.include(Pattern.KEY_USER);
        query.addDescendingOrder(Pattern.KEY_UPDATED);
        query.whereEqualTo(Pattern.KEY_USER, ParseUser.getCurrentUser());
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