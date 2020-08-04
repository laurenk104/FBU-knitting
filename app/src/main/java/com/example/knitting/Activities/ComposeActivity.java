package com.example.knitting.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
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

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.knitting.Pattern;
import com.example.knitting.R;
import com.example.knitting.Stitch;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String URL = "https://automl.googleapis.com/v1beta1/projects/1042358022323/locations/us-central1/models/IOD502610954312220672:predict";

    boolean[][] pattern;

    EditText tvEditName;
    Button btnTakePic;
    Button btnSelectPic;
    ImageView ivPreview;
    Button btnGo;
    Button btnSelect;

    AsyncHttpClient client;

    List<Stitch> stitches;

    public final String APP_TAG = "ComposeActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        tvEditName = findViewById(R.id.tvEditName);
        btnTakePic = findViewById(R.id.btnTakePic);
        btnSelectPic = findViewById(R.id.btnSelectPic);
        ivPreview = findViewById(R.id.ivPreview);
        btnGo = findViewById(R.id.btnGo);
        btnSelect = findViewById(R.id.btnSelect);

        client = new AsyncHttpClient();

        stitches = new ArrayList<>();

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
            }
        });
        btnSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                pattern = generatePattern();
                verify(pattern);
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SelectActivity.class);
                startActivity(intent);
            }
        });

        try {
            authenticate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void authenticate() throws IOException {
        InputStream is = getAssets().open("knitting-auth.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String json = new String(buffer, "UTF-8");

        FileOutputStream fos = openFileOutput("temp.json", MODE_PRIVATE);
        fos.write(json.getBytes());
        fos.close();

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(getFilesDir() + "/temp.json"))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Log.d("storage", storage.getOptions().getCredentials().toString());
        final Credentials auth = storage.getOptions().getCredentials();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Map<String, List<String>> metadata = auth.getRequestMetadata();
                    Map<String, String> cred = new HashMap<>();
                    cred.put("Authorization", metadata.get("Authorization").get(0));
                    postImage(cred);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void postImage(Map<String, String> cred) throws IOException {
        InputStream is = getAssets().open("request.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String json = new String(buffer, "UTF-8");

        RequestHeaders headers = new RequestHeaders();
        headers.putAll(cred);

        client.post(URL, headers, null, json, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray jsonArray = json.jsonObject.getJSONArray("payload");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject stitch = jsonArray.getJSONObject(i);
                        JSONObject coords = stitch.getJSONObject("imageObjectDetection").getJSONObject("boundingBox")
                                .getJSONArray("normalizedVertices").getJSONObject(0);
                        String name = stitch.getString("displayName");
                        double x = (!coords.has("x")) ? 0.0 : coords.getDouble("x");
                        double y = (!coords.has("y")) ? 0.0 : coords.getDouble("y");
                        stitches.add(new Stitch(name, x, y));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("onFailure", response);
                Toast.makeText(getBaseContext(), "Could not make pattern", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void makeRows(List<List<Stitch>> stitches2d, List<Stitch> row, int x) {
        if (stitches.size() > 1) {
            int newX = stitches.get(1).getX();
            row.add(stitches.remove(0));
            if (x - newX < 80) {
                makeRows(stitches2d, row, newX);
            } else {
                stitches2d.add(row);
                makeRows(stitches2d, new ArrayList<Stitch>(), newX);
            }
        } else {
            row.add(stitches.get(0));
            stitches2d.add(row);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean[][] generatePattern() {
        Collections.sort(stitches);
//        for (Stitch stitch : stitches) {
//            Log.d("stitch", stitch.toString());
//        }

        List<List<Stitch>> stitches2d = new ArrayList<>();
        makeRows(stitches2d, new ArrayList<Stitch>(), stitches.get(0).getX());

        int[] modeArray = new int[stitches2d.size()];
        for (int i = 0; i < stitches2d.size(); i++) {
            List<Stitch> row = stitches2d.get(i);
            Log.d("row", row.toString());
            modeArray[i] = row.size();
        }
        int mode = findStitchCount(modeArray, modeArray.length);

        boolean[][] pat = new boolean[stitches2d.size()][mode];
        for (int i = 0; i < stitches2d.size(); i++) {
            for (int j = 0; j < stitches2d.get(i).size(); j++) {
                Stitch stitch = stitches2d.get(i).get(j);
                int column = (stitch.getX() - 1) / 10;
                pat[i][column] = stitch.isKnit();
            }
        }
        return pat;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int findStitchCount(int[] a, int n) {
        // The output array b[] will have sorted array int []b = new int[n];

        // variable to store max of input array which will to have size of count array
        int max = Arrays.stream(a).max().getAsInt();

        // auxiliary(count) array to store count. Initialize count array as 0. Size of count array will be equal to (max + 1).
        int t = max + 1;
        int[] count = new int[t];
        for (int i = 0; i < t; i++) {
            count[i] = 0;
        }

        // Store count of each element of input array
        for (int i = 0; i < n; i++) {
            count[a[i]]++;
        }

        // mode is the index with maximum count
        int mode = 0;
        int k = count[0];
        for (int i = 1; i < t; i++) {
            if (count[i] > k) {
                k = count[i];
                mode = i;
            }
        }
        return mode;
    }

    public void verify(boolean[][] patternArray) {
        String name = tvEditName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile == null || ivPreview.getDrawable() == null) {
            Toast.makeText(this, "Must include an image", Toast.LENGTH_SHORT).show();
            return;
        }
        makeNewPattern(patternArray, photoFile);
    }

    public void makeNewPattern(boolean[][] patternArray, File photoFile) {
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
                    Toast.makeText(getBaseContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
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
        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(photoFile));
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load the selected image into a preview
            ivPreview.setImageBitmap(selectedImage);
        }
    }

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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