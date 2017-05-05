package com.example.iosdev.nhl97;

/**
 * Created by sara on 20/04/17.
 */

public class Team {

    public String player;
    public String team;
    public String teamChar;


    public Team() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)
    }

    public Team(String player, String team, String teamChar) {
        this.player = player;
        this.team = team;
        this.teamChar = teamChar;
    }




}
