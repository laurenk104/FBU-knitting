package com.example.knitting.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.knitting.Adapters.DefaultAdapter;
import com.example.knitting.Adapters.PatternGridAdapter;
import com.example.knitting.Adapters.PatternsAdapter;
import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private GridView gvDefault;
    private DefaultAdapter adapter;
    List<Pattern> patterns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        patterns = new ArrayList<>();

        gvDefault = findViewById(R.id.gvDefault);
        adapter = new DefaultAdapter(this, patterns);
        gvDefault.setAdapter(adapter);
        gvDefault.setNumColumns(3);

        queryPatterns();

        gvDefault.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Pattern defaultPattern = patterns.get(position);

                final Pattern pattern = new Pattern();
                pattern.setName(defaultPattern.getName());
                pattern.setImage(defaultPattern.getImage());
                pattern.setPattern(defaultPattern.getPattern());
                pattern.setUser(ParseUser.getCurrentUser());

                pattern.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e("SelectActivity", "Error while saving", e);
                            Toast.makeText(getBaseContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(SelectActivity.this, DetailActivity.class);
                        intent.putExtra(Pattern.class.getSimpleName(), Parcels.wrap(pattern));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void queryPatterns() {
        // query user
        ParseQuery<ParseUser> user = ParseUser.getQuery();
        user.whereEqualTo("username", "admin");
        user.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    // Something went wrong.
                    return;
                }
                ParseUser admin = objects.get(0);

                // query patterns
                ParseQuery<Pattern> query = ParseQuery.getQuery(Pattern.class);
                query.addAscendingOrder(Pattern.KEY_CREATED_AT);
                query.whereEqualTo(Pattern.KEY_USER, admin);
                query.findInBackground(new FindCallback<Pattern>() {
                    @Override
                    public void done(List<Pattern> receivedPatterns, ParseException e) {
                        if (e != null) {
                            Log.e("SelectActivity", "Issue with getting posts", e);
                            return;
                        }
                        Log.d("patterns", receivedPatterns.toString());
                        patterns.addAll(receivedPatterns);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
}