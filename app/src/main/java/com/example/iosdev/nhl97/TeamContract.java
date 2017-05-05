package com.example.iosdev.nhl97;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by iosdev on 2.3.2017.
 */

public class TeamContract {

    static SeasonNameOpenHelper dbHelper;
    static TeamOpenHelper dbHelperTeam;
    static String DATABASE_NAME = "";
    static String dateForSeasonName;

    //Method to add new season
    //New season is created by changing the name of DATABASE_NAME variable after which new database is
    //created with new name. Application constructs it's fragments based on database so now all teams
    //available and all new data is added to the new database

    public static void addNewSeason(Context context){

        //Database gets name from current time so it is unique
        Long tsLong = System.currentTimeMillis();
        String ts = getDateForSeasonName(tsLong);
        dbHelper = new SeasonNameOpenHelper(context);
        String sql = "insert into seasonName (name)" +
                "values('" + ts + "');";

        //new row is added to seasonName DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql);
        dbHelper.close();

        //new database name is attaached to DATABASE_NAME variable via this method
        getSeasonName(context);



        //Restarts Application so Databases are reinitialized based on the new database
        //this is not elegant solution but after day of frustration I ended up to this
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);


    }

    //Method to get current time to string
    private static String getDateForSeasonName(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        dateForSeasonName = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return dateForSeasonName;
    }

    //Method to initialize DATABASE_NAME variable
    public static void getSeasonName (Context context){
        dbHelper = new SeasonNameOpenHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //Last row from seasonName is added to DATABASE_NAME variable so there will always be the newest
        //season in application
        String sql = "SELECT name FROM seasonName WHERE _id = (SELECT MAX(_id)  FROM seasonName);";
        Cursor cursor = null;
        String seasonName = "";


        cursor = db.rawQuery(sql, null);

            cursor.moveToFirst();
            seasonName = cursor.getString(cursor.getColumnIndex("name"));
            Log.v("getseasonName", seasonName + "dsdd");
        cursor.close();
        DATABASE_NAME = seasonName;


    }



    static final int DATABASE_VERSION = 1;
    static final String TEAM_TABLE_NAME = "team";

    static final String KEY_TEAM = "team";
    static final String KEY_CHAR = "char";
    static final String KEY_PLAYER = "player";
    static final String KEY_IS_RESERVED = "isReserved";
    static final String KEY_GAMES_PLAYED = "games_played";
    static final String KEY_WINS = "wins";
    static final String KEY_LOSES = "loses";
    static final String KEY_OVERTIME_WINS = "overtime_wins";
    static final String KEY_OVERTIME_LOSES = "overtime_loses";
    static final String KEY_OVERTIMES = "overtimes";
    static final String KEY_GOALS_FOR = "goals_for";
    static final String KEY_GOALS_AGAINST = "goals_against";
    static final String KEY_SHOTS_FOR = "shots_for";
    static final String KEY_SHOTS_AGAINST = "shots_against";
    static final String KEY_SHOOTOUTS = "shootouts";
    static final String KEY_SHOOTOUT_WINS = "shootout_wins";
    static final String KEY_SHOOTOUT_LOSES = "shootout_loses";
    static final String KEY_HOME_WINS = "home_wins";
    static final String KEY_HOME_LOSES = "home_loses";
    static final String KEY_GUEST_WINS = "guest_wins";
    static final String KEY_GUEST_LOSES = "guest_loses";
    static DataForUI dfu;

    //Method to get string to create Team Table

    public static final String TEAM_TABLE_CREATE =
            "create table " + TEAM_TABLE_NAME + " (" +
                    "_id integer primary key autoincrement, " +
                    KEY_IS_RESERVED + " integer, " +
                    KEY_TEAM + " text not null, " +
                    KEY_CHAR + " text not null, " +
                    KEY_GAMES_PLAYED + " integer, " +
                    KEY_WINS + " integer, " +
                    KEY_LOSES + " integer, " +
                    KEY_GOALS_FOR + " integer, " +
                    KEY_SHOTS_FOR + " integer, " +
                    KEY_GOALS_AGAINST + " integer, " +
                    KEY_SHOTS_AGAINST + " integer, " +
                    KEY_OVERTIMES + " integer, " +
                    KEY_OVERTIME_WINS + " integer, " +
                    KEY_OVERTIME_LOSES + " integer, " +
                    KEY_SHOOTOUTS + " integer, " +
                    KEY_SHOOTOUT_WINS + " integer, " +
                    KEY_SHOOTOUT_LOSES + " integer, " +
                    KEY_HOME_WINS + " integer, " +
                    KEY_HOME_LOSES + " integer, " +
                    KEY_GUEST_WINS + " integer, " +
                    KEY_GUEST_LOSES + " integer, " +
                    KEY_PLAYER + " text);";
    /*"_id integer primary key autoincrement, " +


            "home_wins integer, " +
                "home_loses integer, " +
                "guest_wins integer, " +
                "guest_loses integer);"*/

    //Method to get string to update row in team table based on winner of the game

    public static String WinningTeam(String team, int goalsFor, int goalsAgainst, int shotsFor,
                                     int shotsAgainst, int overtimes, int overtimeWins, int shootouts,
                                     int shootoutWins, int homeWins, int guestWins) {
        return "UPDATE " + TEAM_TABLE_NAME
                + " SET " + KEY_GAMES_PLAYED + "=" + KEY_GAMES_PLAYED + "+1,"
                + KEY_WINS + "=" + KEY_WINS + "+1,"
                + KEY_GOALS_FOR + "=" + KEY_GOALS_FOR + "+" + goalsFor +","
                + KEY_SHOTS_FOR + "=" + KEY_SHOTS_FOR + "+" + shotsFor +","
                + KEY_SHOTS_AGAINST + "=" + KEY_SHOTS_AGAINST + "+" + shotsAgainst +","
                + KEY_OVERTIMES + "=" + KEY_OVERTIMES + "+" + overtimes +","
                + KEY_OVERTIME_WINS + "=" + KEY_OVERTIME_WINS + "+" + overtimeWins +","
                + KEY_SHOOTOUTS + "=" + KEY_SHOOTOUTS + "+" + shootouts +","
                + KEY_SHOOTOUT_WINS + "=" + KEY_SHOOTOUT_WINS + "+" + shootoutWins +","
                + KEY_HOME_WINS + "=" + KEY_HOME_WINS + "+" + homeWins +","
                + KEY_GUEST_WINS + "=" + KEY_GUEST_WINS + "+" + guestWins +","
                + KEY_GOALS_AGAINST + "=" + KEY_GOALS_AGAINST + "+" + goalsAgainst
                + " WHERE " + KEY_TEAM  + "='"+team+"'";
    }




    //Method to get string to update row in team table based on loser of the game

    public static String LosingTeam(String team, int goalsFor, int goalsAgainst, int shotsFor,
                                    int shotsAgainst, int overtimes, int overtimeLoses, int shootouts,
                                    int shootoutLoses, int homeLoses, int guestLoses) {
        return "UPDATE " + TEAM_TABLE_NAME
                + " SET " + KEY_GAMES_PLAYED + "=" + KEY_GAMES_PLAYED + "+1,"
                + KEY_LOSES + "=" + KEY_LOSES + "+1,"
                + KEY_GOALS_FOR + "=" + KEY_GOALS_FOR + "+" + goalsFor +","
                + KEY_SHOTS_FOR + "=" + KEY_SHOTS_FOR + "+" + shotsFor +","
                + KEY_SHOTS_AGAINST + "=" + KEY_SHOTS_AGAINST + "+" + shotsAgainst +","
                + KEY_OVERTIMES + "=" + KEY_OVERTIMES + "+" + overtimes +","
                + KEY_OVERTIME_LOSES + "=" + KEY_OVERTIME_LOSES + "+" + overtimeLoses +","
                + KEY_SHOOTOUTS + "=" + KEY_SHOOTOUTS + "+" + shootouts +","
                + KEY_SHOOTOUT_LOSES + "=" + KEY_SHOOTOUT_LOSES + "+" + shootoutLoses +","
                + KEY_HOME_LOSES + "=" + KEY_HOME_LOSES + "+" + homeLoses +","
                + KEY_GUEST_LOSES + "=" + KEY_GUEST_LOSES + "+" + guestLoses +","
                + KEY_GOALS_AGAINST + "=" + KEY_GOALS_AGAINST + "+" + goalsAgainst
                + " WHERE " + KEY_TEAM  + "='"+team+"'";
    }

    //Method to get string to insert row into teams own table table

    public static String initializeTeamTable(String team, String teamToBeAdded) {
        String trimmedTeam;
        String trimmedteamToBeAdded;
        trimmedTeam = team.replace(" ", "_").replace(".","");
        trimmedteamToBeAdded = teamToBeAdded.replace(" ", "_").replace(".","");
        return "insert into " + trimmedTeam + " (" +
                " team, wins, loses, goals_for, goals_against, shots_for, shots_against," +
                "overtimes, overtime_wins, overtime_loses, shootouts, shootout_wins, shootout_loses, home_wins," +
                "home_loses, guest_wins, guest_loses)" +
                "values('" + trimmedteamToBeAdded + "', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 );";
    }

    //Method to get string to insert row into teams own table table

    public static String addPlayerToTeamTable(String teamTable, String player, String whereTeam) {

        return "update " + teamTable + " SET name ='" + player +
                "' WHERE team ='"+whereTeam+"'";
    }

    //Method to get string to create teams own table

    public static String createTeamTable(String team) {

        team = team.replace(" ", "_").replace(".","");;
        return "create table " + team + " (" +
                "_id integer primary key autoincrement, " +
                "team text not null, " +
                "name text, " +
                "wins integer, " +
                "loses integer, " +
                "goals_for integer, " +
                "goals_against integer, " +
                "shots_for integer, " +
                "shots_against integer, " +
                "overtimes integer, " +
                "overtime_wins integer, " +
                "overtime_loses integer, " +
                "shootouts integer, " +
                "shootout_wins integer, " +
                "shootout_loses integer, " +
                "home_wins integer, " +
                "home_loses integer, " +
                "guest_wins integer, " +
                "guest_loses integer);";
    }


    //Method to get string to update row in teams own table based on loser of the game

    public static String updateLosingTeamOwnTable(String losingTeam, String winningTeam, int goalsFor, int goalsAgainst, int shotsFor,
                                    int shotsAgainst, int overtimes, int overtimeLoses,
                                    int shootouts,  int shootoutLoses, int homeWins, int homeLoses,
                                                   int guestWins, int guestLoses) {
        return "UPDATE " + losingTeam
                + " SET loses = loses + 1,"
                + "goals_for = goals_for +"  + goalsFor + ","
                + "goals_against = goals_against +"  + goalsAgainst + ","
                + "shots_for = shots_for +"  + shotsFor + ","
                + "shots_against = shots_against +"  + shotsAgainst + ","
                + "overtimes = overtimes +"  + overtimes + ","
                + "overtime_loses = overtime_loses +"  + overtimeLoses + ","
                + "shootouts = shootouts +"  + shootouts + ","
                + "shootout_loses = shootout_loses +"  + shootoutLoses + ","
                + "home_wins = home_wins +"  + homeWins + ","
                + "home_loses = home_loses +"  + homeLoses + ","
                + "guest_wins = guest_wins +"  + guestWins + ","
                + "guest_loses = guest_loses +"  + guestLoses
                + " WHERE team ='"+winningTeam+"'";
    }

    //Method to get string to update row in teams own table  based on winner of the game

    public static String updateWinningTeamOwnTable(String winningTeam, String losingTeam, int goalsFor, int goalsAgainst, int shotsFor,
                                                   int shotsAgainst, int overtimes, int overtimeWins,
                                                   int shootouts,  int shootoutWins, int homeWins, int homeLoses,
                                                   int guestWins, int guestLoses ) {
        return "UPDATE " + winningTeam
                + " SET wins = wins + 1,"
                + "goals_for = goals_for +"  + goalsFor + ","
                + "goals_against = goals_against +"  + goalsAgainst + ","
                + "shots_for = shots_for +"  + shotsFor + ","
                + "shots_against = shots_against +"  + shotsAgainst + ","
                + "overtimes = overtimes +"  + overtimes + ","
                + "overtime_wins = overtime_wins +"  + overtimeWins + ","
                + "shootouts = shootouts +"  + shootouts + ","
                + "shootout_wins = shootout_wins +"  + shootoutWins + ","
                + "home_wins = home_wins +"  + homeWins + ","
                + "home_loses = home_loses +"  + homeLoses + ","
                + "guest_wins = guest_wins +"  + guestWins + ","
                + "guest_loses = guest_loses +"  + guestLoses
                + " WHERE team ='"+losingTeam+"'";
    }
}

