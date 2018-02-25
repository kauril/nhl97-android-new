package com.example.iosdev.nhl97;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by iosdev on 2.3.2017.
 */

public class TeamOpenHelper extends SQLiteOpenHelper {

    DataForUI dfu = new DataForUI();
    Context mcontext;

    public TeamOpenHelper(Context context) {
        super(context, TeamContract.DATABASE_NAME, null, TeamContract.DATABASE_VERSION);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(TeamContract.TEAM_TABLE_CREATE);
        for (int i = 0; i < dfu.teams.length; i++) {
            Log.v("writingdb", dfu.teams[i] +"");
            ContentValues values = new ContentValues();
            values.put(TeamContract.KEY_IS_RESERVED, 0);
            values.put(TeamContract.KEY_PLAYER, "");
            values.put(TeamContract.KEY_TEAM, dfu.teams[i]);
            values.put(TeamContract.KEY_CHAR, dfu.teams2[i]);
            values.put(TeamContract.KEY_GAMES_PLAYED, 0);
            values.put(TeamContract.KEY_WINS, 0);
            values.put(TeamContract.KEY_LOSES, 0);
            values.put(TeamContract.KEY_GOALS_FOR, 0);
            values.put(TeamContract.KEY_GOALS_AGAINST, 0);
            values.put(TeamContract.KEY_SHOTS_FOR, 0);
            values.put(TeamContract.KEY_SHOTS_AGAINST, 0);
            values.put(TeamContract.KEY_OVERTIMES, 0);
            values.put(TeamContract.KEY_OVERTIME_WINS, 0);
            values.put(TeamContract.KEY_OVERTIME_LOSES, 0);
            values.put(TeamContract.KEY_SHOOTOUTS, 0);
            values.put(TeamContract.KEY_SHOOTOUT_WINS, 0);
            values.put(TeamContract.KEY_SHOOTOUT_LOSES, 0);
            values.put(TeamContract.KEY_HOME_WINS, 0);
            values.put(TeamContract.KEY_HOME_LOSES, 0);
            values.put(TeamContract.KEY_GUEST_WINS, 0);
            values.put(TeamContract.KEY_GUEST_LOSES, 0);

            db.insert(TeamContract.TEAM_TABLE_NAME, null, values);
            values.clear();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TeamContract.TEAM_TABLE_NAME);
        onCreate(db);
    }

    public void delete(int id)
    {
        Log.v("deleteOpenHelper",id + "");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TeamContract.TEAM_TABLE_NAME, "_id=\"" + id + "\"", null);

    }
}
