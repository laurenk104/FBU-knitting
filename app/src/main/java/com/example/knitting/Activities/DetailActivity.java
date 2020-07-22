package com.example.knitting.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.knitting.Adapters.PatternFragmentPagerAdapter;
import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Pattern pattern = (Pattern) Parcels.unwrap(getIntent().getParcelableExtra(Pattern.class.getSimpleName()));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(pattern.getName());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PatternFragmentPagerAdapter(getSupportFragmentManager(),
                DetailActivity.this, pattern));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Button btnName = (Button) findViewById(R.id.btnName);
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                NewNameActivity editNameDialogFragment = new NewNameActivity(getSupportActionBar(), pattern);
                editNameDialogFragment.show(fm, "fragment_edit_name");
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