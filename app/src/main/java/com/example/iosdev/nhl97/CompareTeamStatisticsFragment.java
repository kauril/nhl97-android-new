package com.example.iosdev.nhl97;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Float.NaN;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompareTeamStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompareTeamStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompareTeamStatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Store instance variables
    private String title;
    private int page;
    View view;
    public boolean isNewTeamAdded = false;


    String team1;
    String player1;
    String team2;
    String player2;

    String[] teams;
    String[] chars;
    String[] players;



    NumberPicker np1,np2;
    LinearLayout stats;
    TextView name1TV;
    TextView name2TV;
    TextView team1TV;
    TextView team2TV;
    TextView team1Wins;
    TextView team2Wins;
    TextView team1gf;
    TextView team2gf;
    TextView team1sf;
    TextView team2sf;
    TextView team1gp;
    TextView team2gp;
    TextView team1sp;
    TextView team2sp;
    TextView team1otw;
    TextView team2otw;
    TextView team1sow;
    TextView team2sow;
    TextView team1gpg;
    TextView team2gpg;
    TextView team1spg;
    TextView team2spg;
    TextView team1hw;
    TextView team1gw;
    TextView team2hw;
    TextView team2gw;

    //Reference declarations

    TeamOpenHelper dbHelper;
    SQLiteDatabase db;
    static CompareTeamStatisticsFragment ctsf = null;

    public static CompareTeamStatisticsFragment getInstance() {

        return ctsf;

    }


    //Declaring typeface to set custom font to numberpickers
    static Typeface nhlFont;

    private OnFragmentInteractionListener mListener;

    public CompareTeamStatisticsFragment() {
        // Required empty public constructor
    }


    public static CompareTeamStatisticsFragment newInstance(int page, String title) {
        CompareTeamStatisticsFragment fragment = new CompareTeamStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctsf = this;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_compare_team_statistics, container, false);


        //UI element attachments

        stats = (LinearLayout) view.findViewById(R.id.stats);
        stats.setVisibility(View.GONE);

        name1TV = (TextView) view.findViewById(R.id.name1TV);
        name2TV = (TextView) view.findViewById(R.id.name2TV);
        team1TV = (TextView) view.findViewById(R.id.team1TV);
        team2TV = (TextView) view.findViewById(R.id.team2TV);
        team1Wins = (TextView) view.findViewById(R.id.team1Wins);
        team2Wins = (TextView) view.findViewById(R.id.team2Wins);
        team1gf = (TextView) view.findViewById(R.id.team1gf);
        team2gf = (TextView) view.findViewById(R.id.team2gf);
        team1sf = (TextView) view.findViewById(R.id.team1sf);
        team2sf = (TextView) view.findViewById(R.id.team2sf);
        team1gp = (TextView) view.findViewById(R.id.team1gp);
        team2gp = (TextView) view.findViewById(R.id.team2gp);
        team1sp = (TextView) view.findViewById(R.id.team1sp);
        team2sp = (TextView) view.findViewById(R.id.team2sp);
        team1otw = (TextView) view.findViewById(R.id.team1otw);
        team2otw = (TextView) view.findViewById(R.id.team2otw);
        team1sow = (TextView) view.findViewById(R.id.team1sow);
        team2sow = (TextView) view.findViewById(R.id.team2sow);
        team1gpg = (TextView) view.findViewById(R.id.team1gpg);
        team2gpg = (TextView) view.findViewById(R.id.team2gpg);
        team1spg = (TextView) view.findViewById(R.id.team1spg);
        team2spg = (TextView) view.findViewById(R.id.team2spg);
        team1hw = (TextView) view.findViewById(R.id.team1hw);
        team2hw = (TextView) view.findViewById(R.id.team2hw);
        team1gw = (TextView) view.findViewById(R.id.team1gw);
        team2gw = (TextView) view.findViewById(R.id.team2gw);



        //Reference to TeamOpenHelper for further calls to database

        dbHelper = new TeamOpenHelper(getContext());

        //initialization of cursorloader
        getLoaderManager().initLoader(0, null, this);


        return view;
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
            np1 = (NumberPicker) view.findViewById(R.id.np1);
            np2 = (NumberPicker) view.findViewById(R.id.np2);

            //Numberpickers are customized to get team logos on then

            customizeNumberPicker(np1);
            customizeNumberPicker(np2);

            //Disables numberpicker editing

            np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            //team picker 1 initialization
            MainActivity.getInstance().isCompareTeamsFragmentInitialized = true;
            np1.setMinValue(0);

                np1.setMaxValue(chars.length - 1);
                np1.setDisplayedValues(chars);



            Log.v("teams", Arrays.toString(teams));
            Log.v("players", Arrays.toString(players));



            //Guestteampicker onvaluechange listener
            np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    team1 = teams[newVal];
                    player1 = players[newVal];
                    name1TV.setText(player1);
                    team1TV.setText(team1);


                    if(team1.equals(team2)){
                        stats.setVisibility(View.GONE);
                    } else {
                        populateStats();
                    }

                }
            });

            //Initial value is attached to textview
            int currentNumberPickerValueGuest = np1.getValue();
            player1 = players[currentNumberPickerValueGuest];
            team1 = teams[currentNumberPickerValueGuest];
            name1TV.setText(player1);
            team1TV.setText(team1);

            //team picker 2 initialization

            np2.setMinValue(0);

                np2.setMaxValue(chars.length - 1);
                np2.setDisplayedValues(chars);



            //Hometeampicker onvaluechange listener
            np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    team2 = teams[newVal];
                    player2 = players[newVal];
                    name2TV.setText(player2);
                    team2TV.setText(team2);
                    if(team1.equals(team2)){
                        stats.setVisibility(View.GONE);
                    } else {
                        populateStats();
                    }


                }
            });

            //Initial value is attached to textview
            int currentNumberPickerValueHome = np2.getValue();
            player2 = players[currentNumberPickerValueHome];
            team2 = teams[currentNumberPickerValueHome];
            name2TV.setText(player2);
            team2TV.setText(team2);
            if(team1.equals(team2)){
                stats.setVisibility(View.GONE);
            } else {
                populateStats();
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void populateStats(){
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT wins, goals_for, shots_for, overtime_wins, shootout_wins,  home_wins,  guest_wins FROM '" + team1 + "' WHERE team = '" + team2 + "';";
        Cursor cursor = null;
        int wins = 0;
        int gf = 0;
        int sf = 0;
        int otw = 0;
        int sow = 0;
        int hw = 0;
        int gw = 0;

        cursor = db.rawQuery(sql, null);


        if(cursor.moveToFirst()) {
            wins = cursor.getInt(cursor.getColumnIndex("wins"));
            gf = cursor.getInt(cursor.getColumnIndex("goals_for"));
            sf = cursor.getInt(cursor.getColumnIndex("shots_for"));
            otw = cursor.getInt(cursor.getColumnIndex("overtime_wins"));
            sow = cursor.getInt(cursor.getColumnIndex("shootout_wins"));
            hw = cursor.getInt(cursor.getColumnIndex("home_wins"));
            gw = cursor.getInt(cursor.getColumnIndex("guest_wins"));

            float gp = ((float) gf) / sf * 100;

            String gpString = String.format("%2.00f", gp);
            if(gpString.equals("NaN")){
                gpString = "-";
            }


            Log.v("gp", gp + "dsdd");
            team1Wins.setText(wins + "");
            team1gf.setText(gf + "");
            team1sf.setText(sf + "");
            team1gp.setText(gpString + "%");
            team1otw.setText(otw + "");
            team1sow.setText(sow + "");
            team1hw.setText(hw + "");
            team1gw.setText(gw + "");

        }else{
            Log.v("cursor","null");
        }
        cursor.close();
        db = dbHelper.getReadableDatabase();
        String sql1 = "SELECT wins, goals_for, shots_for, overtime_wins, shootout_wins, home_wins, guest_wins FROM '" + team2 + "' WHERE team = '" + team1 + "';";
        Cursor cursor1 = null;

        int wins1 = 0;
        int gf1 = 0;
        int sf1 = 0;
        int otw1 = 0;
        int sow1 = 0;
        int hw1 = 0;
        int gw1 = 0;
        cursor1 = db.rawQuery(sql1, null);


        if(cursor1.moveToFirst()) {
            wins1 = cursor1.getInt(cursor1.getColumnIndex("wins"));
            gf1 = cursor1.getInt(cursor1.getColumnIndex("goals_for"));
            sf1 = cursor1.getInt(cursor1.getColumnIndex("shots_for"));
            otw1 = cursor1.getInt(cursor1.getColumnIndex("overtime_wins"));
            sow1 = cursor1.getInt(cursor1.getColumnIndex("shootout_wins"));
            hw1 = cursor1.getInt(cursor1.getColumnIndex("home_wins"));
            gw1 = cursor1.getInt(cursor1.getColumnIndex("guest_wins"));
            float gp1 = ((float) gf1) / sf1 * 100;
            String gpString1 = String.format("%2.00f", gp1);

            if(gpString1.equals("NaN")){
                gpString1 = "-";
            }

            Log.v("getseasonName", wins1 + "dsdd");
            team2Wins.setText(wins1 + "");
            team2gf.setText(gf1 + "");
            team2sf.setText(sf1 + "");
            team2gp.setText(gpString1 + "%");
            team2otw.setText(otw1 + "");
            team2sow.setText(sow1 + "");
            team2hw.setText(hw1 + "");
            team2gw.setText(gw1 + "");


            float sp = (100 - ((float) gf1) / sf1 * 100);
            String spString = String.format("%2.00f", sp);
            if(spString.equals("NaN")){
                spString = "-";
            }
            team1sp.setText(spString + "%");

            float sp1 = (100 - ((float) gf) / sf * 100);
            String spString1 = String.format("%2.00f", sp1);
            if(spString1.equals("NaN")){
                spString1 = "-";
            }
            team2sp.setText(spString1 + "%");

            float gpg = (((float) gf) / (wins + wins1));
            String gpgString = String.format("%2.02f", gpg);
            if(gpgString.equals("NaN")){
                gpgString = "-";
            }
            team1gpg.setText(gpgString);

            float gpg1 = (((float) gf1) / (wins + wins1));
            String gpgString1 = String.format("%2.02f", gpg1);
            if(gpgString1.equals("NaN")){
                gpgString1 = "-";
            }
            team2gpg.setText(gpgString1);

            float spg = (((float) sf) / (wins + wins1));
            String spgString = String.format("%2.02f", spg);
            if(spgString.equals("NaN")){
                spgString = "-";
            }
            team1spg.setText(spgString);

            float spg1 = (((float) sf1) / (wins + wins1));
            String spgString1 = String.format("%2.02f", spg1);
            if(spgString1.equals("NaN")){
                spgString1 = "-";
            }
            team2spg.setText(spgString1);
        } else {
            Log.v("cursor1","null");
        }
        cursor.close();


        stats.setVisibility(View.VISIBLE);
    }

    //Method to restart cursorloader
    public void restartLoader(){
        Log.v("comper", "loadre reset");
        getLoaderManager().restartLoader(0, null, this);
        populateStats();
    }

    //Method to init cursorloader
    public void initLoader(){
        Log.v("comper", "loadre reset");
        getLoaderManager().initLoader(0, null, this);
        populateStats();
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
}
