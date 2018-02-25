package com.example.iosdev.nhl97;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

import static com.example.iosdev.nhl97.R.string.date;



public class SeasonNameOpenHelper extends SQLiteOpenHelper {

    String dateForSeasonName;
    Context mcontext;
    static String SEASONNAME_TABLE_NAME = "seasonName";

    public SeasonNameOpenHelper(Context context) {
        super(context, "seasonNameDB", null, 1);
        mcontext = context;

    }

    public static final String SEASONNAME_TABLE_CREATE =
            "create table "+ SEASONNAME_TABLE_NAME + " (" +
                    "_id integer primary key autoincrement, " +
                    "name text not null);";

    private String getDateForSeasonName(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        dateForSeasonName = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return dateForSeasonName;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

            db.execSQL(SEASONNAME_TABLE_CREATE);

            Long tsLong = System.currentTimeMillis();
            String ts = getDateForSeasonName(tsLong);
            String[] separated = ts.split(" ");
            Log.v("separated1", separated[0]);
            Log.v("separated2", separated[1]);


            Log.v("ts", ts + "");


            ContentValues values = new ContentValues();
            values.put("name", ts);

            db.insert(SEASONNAME_TABLE_NAME, null, values);
            values.clear();



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SEASONNAME_TABLE_NAME);
        onCreate(db);
    }

    public void delete(int id)
    {
        Log.v("deleteOpenHelper",id + "");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SEASONNAME_TABLE_NAME, "_id=\"" + id + "\"", null);

    }
}
