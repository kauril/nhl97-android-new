package com.example.iosdev.nhl97;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by iosdev on 22.2.2017.
 */

public class ResultProvider extends ContentProvider {

    private ResultOpenHelper dbHelper = null;
    public static final String PROVIDER_NAME = "com.example.iosdev.nhl97.ResultProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/result");
    public static final String _ID = "_id";
    private static final int RESULT = 1;
    private static final int RESULT_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "result", RESULT);
        uriMatcher.addURI(PROVIDER_NAME, "result/#", RESULT_ID);
    }
    private SQLiteDatabase db;
    private static HashMap<String, String> RESULT_PROJECTION_MAP;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new ResultOpenHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ResultContract.RESULT_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case RESULT:
                qb.setProjectionMap(RESULT_PROJECTION_MAP);
                break;

            case RESULT_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){

            sortOrder = "date";
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.moveToFirst();
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.v("Insert", values.toString() + "");
        db.insert(ResultContract.RESULT_TABLE_NAME, null, values);

        //String message = "after " + values.toString() + " added";
        //FeedFragment.getInstance().statusBarNotification(message);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String idString = selectionArgs[0];
        Log.v("deleteProvider",idString + "");
        Integer id = Integer.parseInt(idString);


        dbHelper.delete(id);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        db.update(ResultContract.RESULT_TABLE_NAME, values, selection, selectionArgs);





        return 0;
    }


}
