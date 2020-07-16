package com.example.knitting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    ImageView ivDetailImage;
    ListView gridColumns;

    Pattern pattern;
    List<List<Boolean>> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        pattern = (Pattern) Parcels.unwrap(getIntent().getParcelableExtra(Pattern.class.getSimpleName()));
        values = pattern.getPattern();

        ivDetailImage = findViewById(R.id.ivDetailImage);
        gridColumns = findViewById(R.id.gridColumns);
        PatternGridAdapter adapter = new PatternGridAdapter(this, values);
        gridColumns.setAdapter(adapter);

        Glide.with(this).load(pattern.getImage().getUrl()).into(ivDetailImage);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        try {
//            getSupportActionBar().setLogo(drawableFromUrl(pattern.getImage().getUrl()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
      //  getSupportActionBar().setLogo(Integer.parseInt(pattern.getImage().getUrl()));
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
}