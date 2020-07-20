package com.example.knitting.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.Arrays;

public class ComposeActivity extends AppCompatActivity {

    boolean[][] stockinette;
    boolean[][] oneRibbing;

    EditText tvEditName;
    Button btnTakePic;
    ImageView ivPreview;
    Button btnStockinette;
    Button btnRibbing;

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        tvEditName = findViewById(R.id.tvEditName);
        btnTakePic = findViewById(R.id.btnTakePic);
        ivPreview = findViewById(R.id.ivPreview);
        btnStockinette = findViewById(R.id.btnStockinette);
        btnRibbing = findViewById(R.id.btnRibbing);

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
            }
        });

        btnStockinette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewPattern(stockinette, "stockinette.jpg", photoFile);
            }
        });
        btnRibbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewPattern(oneRibbing, "one-by-one-ribbing.jpg", photoFile);
            }
        });
    }

    public void makeNewPattern(boolean[][] patternArray, String name, File photoFile) {
        final Pattern pattern = new Pattern();
        pattern.setName(tvEditName.getText().toString());
        pattern.setImage(new ParseFile(photoFile));
        pattern.setPattern(patternArray);
        pattern.setUser(ParseUser.getCurrentUser());

        pattern.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ComposeActivity", "Error while saving", e);
                    //Toast.makeText(getBaseContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                tvEditName.setText("");
                ivPreview.setImageResource(0);
                Intent intent = new Intent(ComposeActivity.this, DetailActivity.class);
                intent.putExtra(Pattern.class.getSimpleName(), Parcels.wrap(pattern));
                startActivity(intent);
            }
        });
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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