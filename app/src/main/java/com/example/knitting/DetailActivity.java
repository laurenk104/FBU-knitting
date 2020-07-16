package com.example.knitting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    ImageView ivDetailImage;
    ListView gridColumns;
    GridView gridPattern;

    Pattern pattern;
    boolean[][] values;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        pattern = (Pattern) Parcels.unwrap(getIntent().getParcelableExtra(Pattern.class.getSimpleName()));
        values = pattern.getPattern();

        ivDetailImage = findViewById(R.id.ivDetailImage);
        gridPattern = findViewById(R.id.gridPattern);
        PatternGridAdapter adapter = new PatternGridAdapter(this, values);
        gridPattern.setAdapter(adapter);
        gridPattern.setNumColumns(values[0].length);

     //   Glide.with(this).load(pattern.getImage().getUrl()).into(ivDetailImage);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    drawable = drawableFromUrl(pattern.getImage().getUrl());
//                    Log.d("test", "" + drawable);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();

        getSupportActionBar().setLogo(drawable);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(pattern.getName());
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
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
}