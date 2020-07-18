package com.example.knitting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
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
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Pattern pattern = (Pattern) Parcels.unwrap(getIntent().getParcelableExtra(Pattern.class.getSimpleName()));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(pattern.getName());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PatternFragmentPagerAdapter(getSupportFragmentManager(),
                DetailActivity.this, pattern));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
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