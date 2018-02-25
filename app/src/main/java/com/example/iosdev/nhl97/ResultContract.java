package com.example.iosdev.nhl97;

/**
 * Created by iosdev on 23.2.2017.
 */

public class ResultContract {
    static final String DATABASE_NAME = "resultDB";
    static final int DATABASE_VERSION = 1;
    static final String RESULT_TABLE_NAME = "result";

    static final String KEY_GUEST_TEAM = "guestTeam";
    static final String KEY_HOME_TEAM = "homeTeam";
    static final String KEY_GUEST_GOALS = "guestGoals";
    static final String KEY_HOME_GOALS = "homeGoals";
    static final String KEY_GUEST_SHOTS = "guestShots";
    static final String KEY_HOME_SHOTS = "homeShots";
    static final String KEY_OVERTIME = "overTime";
    static final String KEY_SHOOTOUTS = "shootOuts";
    static final String KEY_DATE = "date";
    static final String KEY_IS_DUMMY = "is_dummy";

    public static final String RESULT_TABLE_CREATE =
            "create table " + RESULT_TABLE_NAME + " (" +
                    "_id integer primary key autoincrement, " +
                    KEY_GUEST_TEAM + " text, " +
                    KEY_HOME_TEAM + " text, " +
                    KEY_GUEST_GOALS + " integer, " +
                    KEY_HOME_GOALS + " integer, " +
                    KEY_GUEST_SHOTS + " integer, " +
                    KEY_HOME_SHOTS + " integer, " +
                    KEY_OVERTIME + " text, " +
                    KEY_SHOOTOUTS + " text, " +
                    KEY_IS_DUMMY + " text, " +
                    KEY_DATE + " text);";
    }
