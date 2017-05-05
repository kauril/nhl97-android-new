package com.example.iosdev.nhl97;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by iosdev on 2.3.2017.
 */

public class TeamProvider extends ContentProvider {

    private TeamOpenHelper dbHelper = null;
    public static final String PROVIDER_NAME = "com.example.iosdev.nhl97.TeamProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/team");
    public static final String _ID = "_id";
    private static final int TEAM = 1;
    private static final int TEAM_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "team", TEAM);
        uriMatcher.addURI(PROVIDER_NAME, "team/#", TEAM_ID);
    }
    private SQLiteDatabase db;
    private static HashMap<String, String> TEAM_PROJECTION_MAP;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        SeasonNameOpenHelper seasonNameOpenHelper = new SeasonNameOpenHelper(getContext());
        seasonNameOpenHelper.getWritableDatabase();
        TeamContract.getSeasonName(context);

        dbHelper = new TeamOpenHelper(context);

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
        qb.setTables(TeamContract.TEAM_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case TEAM:
                qb.setProjectionMap(TEAM_PROJECTION_MAP);
                break;

            case TEAM_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                Log.v("fdsf", "fdf");
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){

            sortOrder = "_id";
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */

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
        db.insert(TeamContract.TEAM_TABLE_NAME, null, values);

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
        Log.v("Insert", uri.toString() + "");
        db.update(TeamContract.TEAM_TABLE_NAME, values, selection, selectionArgs);

        return 0;
    }


}
