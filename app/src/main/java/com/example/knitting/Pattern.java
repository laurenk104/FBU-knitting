package com.example.knitting;

import android.util.Log;
import android.widget.ListView;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parcel(analyze = Pattern.class)
@ParseClassName("Pattern")
public class Pattern extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_PATTERN = "pattern";
    public static final String KEY_USER = "user";
    public static final String KEY_UPDATED = "updatedAt";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public boolean[][] getPattern() {
        List<List<Boolean>> values = getList(KEY_PATTERN);
        boolean[][] valuesArray = new boolean[values.size()][];
        for (int i = 0; i < values.size(); i++) {
            List<Boolean> row = values.get(i);
            boolean[] newRow = new boolean[row.size()];
            for (int j = 0; j < row.size(); j++) {
                newRow[j] = row.get(j);
            }
            valuesArray[i] = newRow;
        }
        return valuesArray;
    }

    public void setPattern(boolean[][] pattern) {
        List<List<Boolean>> values = new ArrayList<>();
        for (int i = 0; i < pattern.length; i++) {
            List<Boolean> row = new ArrayList<>();
            boolean[] oldRow = pattern[i];
            for (int j = 0; j < oldRow.length; j++) {
                row.add(oldRow[j]);
            }
            values.add(row);
        }
        put(KEY_PATTERN, values);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
