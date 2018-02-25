package com.example.iosdev.nhl97;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by iosdev on 23.2.2017.
 */

public class ResultOpenHelper extends SQLiteOpenHelper {



    public ResultOpenHelper(Context context) {
        super(context, ResultContract.DATABASE_NAME, null, ResultContract.DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ResultContract.RESULT_TABLE_CREATE);
        for (int i = 0; i < 10; i++) {

            ContentValues values = new ContentValues();
            values.put(ResultContract.KEY_GUEST_TEAM, " ");
            values.put(ResultContract.KEY_HOME_TEAM, " ");
            values.put(ResultContract.KEY_GUEST_GOALS, 0);
            values.put(ResultContract.KEY_HOME_GOALS, 0);
            values.put(ResultContract.KEY_GUEST_SHOTS, 0);
            values.put(ResultContract.KEY_HOME_SHOTS, 0);
            values.put(ResultContract.KEY_OVERTIME, 0);
            values.put(ResultContract.KEY_SHOOTOUTS, 0);
            values.put(ResultContract.KEY_IS_DUMMY, 1);
            values.put(ResultContract.KEY_DATE, " ");


            db.insert(ResultContract.RESULT_TABLE_NAME, null, values);
            values.clear();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ResultContract.RESULT_TABLE_NAME);
        onCreate(db);
    }

    public void delete(int id)
    {
        Log.v("deleteOpenHelper",id + "");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ResultContract.RESULT_TABLE_NAME, "_id=\"" + id + "\"", null);

    }
}
