package com.example.knitting.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Arrays;

public class ComposeActivity extends AppCompatActivity {

    boolean[][] stockinette;
    boolean[][] oneRibbing;

    EditText tvEditName;
    Button btnStockinette;
    Button btnRibbing;
    Activity currentActivity;

    public ComposeActivity() {
        stockinette = new boolean[14][10];
        for (int i = 0; i < stockinette.length; i++) {
            Arrays.fill(stockinette[i], true);
        }
        oneRibbing = new boolean[14][10];
        for (int i = 0; i < oneRibbing.length; i++) {
            for (int j = 0; j < oneRibbing[i].length; j += 2) {
                oneRibbing[i][j] = true;
            }
        }
        currentActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        tvEditName = findViewById(R.id.tvEditName);
        btnStockinette = findViewById(R.id.btnStockinette);
        btnRibbing = findViewById(R.id.btnRibbing);

        btnStockinette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewPattern(stockinette, "stockinette.jpg");
            }
        });
        btnRibbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewPattern(oneRibbing, "one-by-one-ribbing.jpg");
            }
        });
    }

    public void makeNewPattern(boolean[][] patternArray, String name) {
        final Pattern pattern = new Pattern();
        pattern.setName(tvEditName.getText().toString());
        pattern.setPattern(patternArray);
        pattern.setUser(ParseUser.getCurrentUser());

        byte[] data = "Working at Parse is great!".getBytes();
        ParseFile file = new ParseFile(name, data);
        pattern.setImage(file);

        pattern.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ComposeActivity", "Error while saving", e);
                    //     Toast.makeText(this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                tvEditName.setText("");
                Intent intent = new Intent(currentActivity, DetailActivity.class);
                intent.putExtra(Pattern.class.getSimpleName(), Parcels.wrap(pattern));
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            // Navigate to the home activity
            Intent intent = new Intent(this, MainActivity.class);
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
}