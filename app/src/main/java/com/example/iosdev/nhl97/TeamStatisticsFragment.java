package com.example.iosdev.nhl97;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamStatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // Store instance variables
    private String title;
    private int page;

    String team;
    String player;
    View view;


    String[] teams;
    String[] chars;
    String[] players;

    Boolean isCursorLoaded = false;


    NumberPicker tp;
    TextView nameTV;
    TextView teamTV;
    TextView gamesPlayed;
    TextView winsTV;
    TextView losesTV;
    TextView winningPercent;
    TextView goalsFor;
    TextView goalsAgainst;
    TextView goalsForPerGameTV;
    TextView goalsAgainstPerGameTV;
    TextView goalPercentageTV;
    TextView savePercentageTV;
    TextView shotsFor;
    TextView shotsAgainst;
    TextView shotsForPerGameTV;
    TextView shotsAgainstPerGameTV;
    TextView overtimesTV;
    TextView overtimeWins;
    TextView overtimeLoses;
    TextView shootoutsTV;
    TextView shootoutWins;
    TextView shootoutLoses;
    TextView homeWins;
    TextView homeLoses;
    TextView guestWins;
    TextView guestLoses;


    //Reference declarations

    TeamOpenHelper dbHelper;
    SQLiteDatabase db;
    static TeamStatisticsFragment tsf = null;

    public static TeamStatisticsFragment getInstance() {

        return tsf;

    }

    //Declaring typeface to set custom font to numberpickers
    static Typeface nhlFont;

    private OnFragmentInteractionListener mListener;

    public TeamStatisticsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TeamStatisticsFragment newInstance(int page, String title) {
        TeamStatisticsFragment fragment = new TeamStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tsf = this;
        //Initializing custom font for number pickers
        nhlFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/NHL.ttf");
        if (getArguments() != null) {
            page = getArguments().getInt("someInt", 0);
            title = getArguments().getString("someTitle");
        }
    }

    //Function to set custom font and font size in number pickers

    public static boolean customizeNumberPicker(NumberPicker numberPicker)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setTypeface(nhlFont);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setTextSize(40);
                    ((EditText)child).setTypeface(nhlFont);
                    ((EditText)child).setTextSize(40);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumb", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNum", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumber", e);
                }
            }
        }
        return false;
    }
    public void initializeView(View view){
        tp = (NumberPicker) view.findViewById(R.id.tp);
        nameTV = (TextView) view.findViewById(R.id.nameTV);
        teamTV = (TextView) view.findViewById(R.id.teamTV);
        gamesPlayed = (TextView) view.findViewById(R.id.teamGamesPlayed);
        winsTV = (TextView) view.findViewById(R.id.teamWins);
        losesTV = (TextView) view.findViewById(R.id.teamLoses);
        winningPercent = (TextView) view.findViewById(R.id.teamWinningPercentage);
        goalsFor = (TextView) view.findViewById(R.id.teamGoalsFor);
        goalsAgainst = (TextView) view.findViewById(R.id.teamGoalsAgainst);
        goalsForPerGameTV = (TextView) view.findViewById(R.id.teamGoalsForPerGame);
        goalsAgainstPerGameTV = (TextView) view.findViewById(R.id.teamGoalsAgainstPerGame);
        goalPercentageTV = (TextView) view.findViewById(R.id.teamGoalPersentage);
        savePercentageTV = (TextView) view.findViewById(R.id.teamSavePercentage);
        shotsFor = (TextView) view.findViewById(R.id.teamShotsFor);
        shotsAgainst = (TextView) view.findViewById(R.id.teamShotsAgainst);
        shotsForPerGameTV = (TextView) view.findViewById(R.id.teamShotsForPerGame);
        shotsAgainstPerGameTV = (TextView) view.findViewById(R.id.teamShotsAgainstPerGame);
        overtimesTV = (TextView) view.findViewById(R.id.teamOvertimes);
        overtimeWins = (TextView) view.findViewById(R.id.teamOvertimeWins);
        overtimeLoses = (TextView) view.findViewById(R.id.teamOvertimeLoses);
        shootoutsTV = (TextView) view.findViewById(R.id.teamShootouts);
        shootoutWins = (TextView) view.findViewById(R.id.teamShootoutWins);
        shootoutLoses = (TextView) view.findViewById(R.id.teamShootoutLoses);
        homeWins = (TextView) view.findViewById(R.id.teamHomeWins);
        homeLoses = (TextView) view.findViewById(R.id.teamHomeLoses);
        guestWins = (TextView) view.findViewById(R.id.teamGuestWins);
        guestLoses = (TextView) view.findViewById(R.id.teamGuestLoses);


        //Reference to TeamOpenHelper for further calls to database

        dbHelper = new TeamOpenHelper(getContext());

        //initialization of cursorloader
        if(isCursorLoaded){
            restartLoader();
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_statistics, container, false);
        initializeView(view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ///////////////////////////////////  CURSOR LOADER   ///////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    static final String[] PROJECTION = new String[] {"char", "team", "isReserved", "player"};
    //Only teams assigned with players are putted into team number pickers, selection args are used to
    //find to ones with isReserved=1
    String[] selectionArgs = {"1"};
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                TeamProvider.CONTENT_URI,
                PROJECTION,
                "isReserved=?",
                selectionArgs,
                null
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Arraylists to gather information from cursor

        ArrayList<String> teamsFromDB = new ArrayList<String>();
        ArrayList<String> charsFromDB = new ArrayList<String>();
        ArrayList<String> playersFromDB = new ArrayList<String>();



        //Looping through cursor and putting valus to arraylists

        try {

            while (data.moveToNext()) {
                playersFromDB.add(data.getString(3));
                teamsFromDB.add(data.getString(1));
                charsFromDB.add(data.getString(0));

            }
        } finally {
            data.close();
        }

        //Content of arraylists are putted to arrays; numberpickers doesn't accept arraylists

        teams = (String[]) teamsFromDB.toArray(new String[teamsFromDB.size()]);
        chars = (String[]) charsFromDB.toArray(new String[charsFromDB.size()]);
        players = (String[]) playersFromDB.toArray(new String[playersFromDB.size()]);


        if(teams.length <= 1){

        } else {



            //Numberpicker is customized to get team logos on it

            customizeNumberPicker(tp);


            //Disables numberpicker editing

            tp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


            tp.setMinValue(0);

            tp.setMaxValue(chars.length - 1);
            tp.setDisplayedValues(chars);


            Log.v("teams", Arrays.toString(teams));
            Log.v("players", Arrays.toString(players));


            //Guestteampicker onvaluechange listener
            tp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    team = teams[newVal];
                    player = players[newVal];
                    nameTV.setText(player);
                    teamTV.setText(team);



                        populateStats();


                }
            });

            //Initial value is attached to textview
            int currentNumberPickerValueGuest = tp.getValue();
            player = players[currentNumberPickerValueGuest];
            team = teams[currentNumberPickerValueGuest];
            nameTV.setText(player);
            teamTV.setText(team);
            populateStats();
        }
    }

    public void populateStats(){
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT games_played, wins, loses, goals_for," +
                " goals_against, shots_for, shots_against, overtimes," +
                " overtime_wins, overtime_loses, shootouts, shootout_wins," +
                " shootout_loses, home_wins, home_loses," +
                " guest_wins, guest_loses FROM team WHERE team" +
                " = '" + team + "';";
        Cursor cursor = null;
        int games_played = 0;
        int wins = 0;
        int loses = 0;
        int goals_for = 0;
        int goals_against = 0;
        int shots_for = 0;
        int shots_against = 0;
        int overtimes = 0;
        int overtime_wins = 0;
        int overtime_loses = 0;
        int shootouts = 0;
        int shootout_wins = 0;
        int shootout_loses = 0;
        int home_wins = 0;
        int home_loses = 0;
        int guest_wins = 0;
        int guest_loses = 0;

        cursor = db.rawQuery(sql, null);


        if(cursor.moveToFirst()) {
            games_played = cursor.getInt(cursor.getColumnIndex("games_played"));
            wins = cursor.getInt(cursor.getColumnIndex("wins"));
            loses = cursor.getInt(cursor.getColumnIndex("loses"));
            goals_for = cursor.getInt(cursor.getColumnIndex("goals_for"));
            goals_against = cursor.getInt(cursor.getColumnIndex("goals_against"));
            shots_for = cursor.getInt(cursor.getColumnIndex("shots_for"));
            shots_against = cursor.getInt(cursor.getColumnIndex("shots_against"));
            overtimes = cursor.getInt(cursor.getColumnIndex("overtimes"));
            overtime_wins = cursor.getInt(cursor.getColumnIndex("overtime_wins"));
            overtime_loses = cursor.getInt(cursor.getColumnIndex("overtime_loses"));
            shootouts = cursor.getInt(cursor.getColumnIndex("shootouts"));
            shootout_wins = cursor.getInt(cursor.getColumnIndex("shootout_wins"));
            shootout_loses = cursor.getInt(cursor.getColumnIndex("shootout_loses"));
            home_wins = cursor.getInt(cursor.getColumnIndex("home_wins"));
            home_loses = cursor.getInt(cursor.getColumnIndex("home_loses"));
            guest_wins = cursor.getInt(cursor.getColumnIndex("guest_wins"));
            guest_loses = cursor.getInt(cursor.getColumnIndex("guest_loses"));


            gamesPlayed.setText(games_played + "");
            winsTV.setText(wins + "");
            losesTV.setText(loses + "");
            goalsFor.setText(goals_for + "");
            goalsAgainst.setText(goals_against + "");
            shotsFor.setText(shots_for + "");
            shotsAgainst.setText(shots_against + "");
            overtimesTV.setText(overtimes + "");
            overtimeWins.setText(overtime_wins + "");
            overtimeLoses.setText(overtime_loses + "");
            shootoutsTV.setText(shootouts + "");
            shootoutWins.setText(shootout_wins + "");
            shootoutLoses.setText(shootout_loses + "");
            homeWins.setText(home_wins + "");
            homeLoses.setText(home_loses + "");
            guestWins.setText(guest_wins + "");
            guestLoses.setText(guest_loses + "");



        }else{
            Log.v("cursor","null");
        }
        cursor.close();

        float goalPercentage = ((float) goals_for) / shots_for * 100;

        String goalPercentageString = String.format("%2.00f", goalPercentage);
        if(goalPercentageString.equals("NaN")){
            goalPercentageString = "-";
        }
        goalPercentageTV.setText(goalPercentageString + "%");


            float savePercentage = (100 - ((float) goals_against) / shots_against * 100);
            String spString = String.format("%2.00f", savePercentage);
            if(spString.equals("NaN")){
                spString = "-";
            }
            savePercentageTV.setText(spString + "%");



            float goalsForPerGame = (((float) goals_for) / games_played);
            String gfpgString = String.format("%2.02f", goalsForPerGame);
            if(gfpgString.equals("NaN")){
                gfpgString = "-";
            }
            goalsForPerGameTV.setText(gfpgString);

            float goalsAgainstPerGame = (((float) goals_against) / games_played);
            String gapgString = String.format("%2.02f", goalsAgainstPerGame);
            if(gapgString.equals("NaN")){
                gapgString = "-";
            }
            goalsAgainstPerGameTV.setText(gapgString);



            float shotsForPerGame = (((float) shots_for) / games_played);
            String sfpgString = String.format("%2.02f", shotsForPerGame);
            if(sfpgString.equals("NaN")){
                sfpgString = "-";
            }
            shotsForPerGameTV.setText(sfpgString);

            float shotsAgainstPerGame = (((float) shots_against) / games_played);
            String sapgString = String.format("%2.02f", shotsAgainstPerGame);
            if(sapgString.equals("NaN")){
                sapgString = "-";
            }
            shotsAgainstPerGameTV.setText(sapgString);

            float winningPercentage = (((float) wins) / games_played * 100);
            String wpString = String.format("%2.00f", winningPercentage);
            if(wpString.equals("NaN")){
                wpString = "-";
            }
            winningPercent.setText(wpString + "%");


    }

    //Method to restart cursorloader
    public void restartLoader(){
        Log.v("comper", "loadre reset");
        getLoaderManager().restartLoader(0, null, this);
        populateStats();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Method to detect orietation/screen changes.
    //Requires android:configChanges="orientation|keyboardHidden|screenSize" in manifest file
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Boolean set true. This variable tells in initializeView() method that cursor loader is already
        //initialized and it will be only restarted
        isCursorLoaded = true;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    //Method recreates layout based on correct orientation

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View subview = inflater.inflate(R.layout.fragment_team_statistics, viewGroup);

        initializeView(subview);

    }
}
