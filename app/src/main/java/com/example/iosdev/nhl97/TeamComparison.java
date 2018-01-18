package com.example.iosdev.nhl97;

/**
 * Created by sara on 08/01/18.
 */

public class TeamComparison {


        public String player;
        public String team;
        public String teamChar;
        public Integer wins;
        public Integer loses;
        public Integer goals_for;
        public Integer goals_against;
        public Integer shots_for;
        public Integer shots_against;
        public Integer overtimes;
        public Integer overtime_wins;
        public Integer overtime_loses;
        public Integer shootouts;
        public Integer shootout_wins;
        public Integer shootout_loses;
        public Integer home_wins;
        public Integer home_loses;
        public Integer guest_wins;
        public Integer guest_loses;



        public TeamComparison() {
            // Default constructor required for calls to DataSnapshot.getValue(Game.class)
        }

        public TeamComparison(String player, String team, String teamChar, Integer wins, Integer loses, Integer goals_for, Integer goals_against, Integer shots_for, Integer shots_against,
                    Integer overtimes, Integer overtime_wins, Integer overtime_loses, Integer shootouts, Integer shootout_wins,
                    Integer shootout_loses, Integer home_wins, Integer home_loses, Integer guest_wins, Integer guest_loses) {
            this.player = player;
            this.team = team;
            this.teamChar = teamChar;
            this.wins = wins;
            this.loses = loses;
            this.goals_for = goals_for;
            this.goals_against = goals_against;
            this.shots_for = shots_for;
            this.shots_against = shots_against;
            this.overtimes = overtimes;
            this.overtime_wins = overtime_wins;
            this.overtime_loses = overtime_loses;
            this.shootouts = shootouts;
            this.shootout_wins = shootout_wins;
            this.shootout_loses = shootout_loses;
            this.home_wins = home_wins;
            this.home_loses = home_loses;
            this.guest_wins = guest_wins;
            this.guest_loses = guest_loses;
        }




}
