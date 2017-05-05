package com.example.iosdev.nhl97;

/**
 * Public class for creating game object for firebase
 */

public class Game {


    public String guestTeam;
    public Integer guestGoals;
    public Integer homeGoals;
    public String homeTeam;
    public Integer guestShots;
    public Integer homeShots;
    public Boolean isOvertime;
    public Boolean isShootout;
    public String date;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)
    }

    public Game(String guestTeam, Integer guestGoals, Integer homeGoals, String homeTeam, Integer guestShots, Integer homeShots, Boolean isOvertime, Boolean isShootout, String date) {

        this.guestTeam = guestTeam;
        this.guestGoals = guestGoals;
        this.homeGoals = homeGoals;
        this.homeTeam = homeTeam;
        this.guestShots = guestShots;
        this.homeShots = homeShots;
        this.isOvertime = isOvertime;
        this.isShootout = isShootout;
        this.date = date;
    }
}
