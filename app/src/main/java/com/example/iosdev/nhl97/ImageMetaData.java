package com.example.iosdev.nhl97;

import android.net.Uri;

/**
 * Created by sara on 04/05/17.
 */

public class ImageMetaData {
    public String guestTeam;
    public Integer guestGoals;
    public Integer homeGoals;
    public String homeTeam;
    public String url;
    public String date;


    public ImageMetaData() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)
    }

    public ImageMetaData(String guestTeam, Integer guestGoals, Integer homeGoals, String homeTeam, String url, String date) {

        this.guestTeam = guestTeam;
        this.guestGoals = guestGoals;
        this.homeGoals = homeGoals;
        this.homeTeam = homeTeam;
        this.url = url;
        this.date = date;

    }
}
