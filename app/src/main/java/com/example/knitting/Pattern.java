package com.example.knitting;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

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

    public List<List<Boolean>> getPattern() {
        return getList(KEY_PATTERN);
    }

    public void setPattern(List<Boolean> pattern) {
        put(KEY_PATTERN, pattern);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
