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

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String URL = "https://automl.googleapis.com/v1beta1/projects/1042358022323/locations/us-central1/models/IOD502610954312220672:predict";

    boolean[][] patternArray;

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
                try {
                    authenticate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SelectActivity.class);
                startActivity(intent);
            }
        });
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
        final Credentials auth = storage.getOptions().getCredentials();

        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getBytes() throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(photoFile);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postImage(Map<String, String> cred) throws IOException {
     //   String bytes = "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";
        String bytes = getBytes();
        Log.d("bytes", bytes);

        InputStream is = getAssets().open("request.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String json = new String(buffer, "UTF-8");
        String replaced = json.replace("YOUR_BASE64_ENCODED_IMAGE_BYTES", bytes);

        RequestHeaders headers = new RequestHeaders();
        headers.putAll(cred);

        client.post(URL, headers, null, replaced, new JsonHttpResponseHandler() {
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
                        Log.d("stitches", stitch.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                generatePattern();
                verify();
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
    public void generatePattern() {
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
        patternArray = pat;
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

    public void verify() {
        String name = tvEditName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile == null || ivPreview.getDrawable() == null) {
            Toast.makeText(this, "Must include an image", Toast.LENGTH_SHORT).show();
            return;
        }
        makeNewPattern(photoFile);
    }

    public void makeNewPattern(File photoFile) {
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