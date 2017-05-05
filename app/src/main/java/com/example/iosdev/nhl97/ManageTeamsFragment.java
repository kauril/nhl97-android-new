package com.example.iosdev.nhl97;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageTeamsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageTeamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageTeamsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    //Fragment for players to choose team

    NumberPicker tp;
    DataForUI dfu = new DataForUI();
    static Typeface nhlFont;
    TextView teamName;
    TextView pickYourTeam;
    TextView allTeamsSelected;
    EditText nameEditText;
    Cursor c;
    String[] teams;
    String[] chars;
    String[] idArray;
    String[] players;
    String[] teamsWithPlayers;
    Button addName;
    String name;
    TeamOpenHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;

    private String team;
    private String teamChar;
    private String id;

    private static ManageTeamsFragment mtf = null;

    public static ManageTeamsFragment getInstance() {
        Log.v("getInstance", "tff");
        return mtf;

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mtf = this;
        nhlFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/NHL.ttf");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        View view = inflater.inflate(R.layout.fragment_manage_teams, container, false);
        pickYourTeam = (TextView) view.findViewById(R.id.pick_your_team);
        teamName = (TextView) view.findViewById(R.id.teamName);
        allTeamsSelected = (TextView) view.findViewById(R.id.all_teams_selected);
        tp = (NumberPicker) view.findViewById(R.id.teamPicker);
        customizeNumberPicker(tp);
        tp.setMinValue(0);
        addName = (Button) view.findViewById(R.id.addname);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);

        dbHelper = new TeamOpenHelper(getContext());



        Log.v("inflateee","sadas");
        tp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        getLoaderManager().initLoader(0, null, this);


        addName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.v("addname", "clicked");
                name = nameEditText.getText().toString();
                if (name.matches("")) {
                    Toast.makeText(getActivity(), "You must enter your name", Toast.LENGTH_SHORT).show();
                    return;
                } else if(Arrays.asList(players).contains(name)){
                    Toast.makeText(getActivity(), name + " has already team", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    updateTeams();
                    nameEditText.setText("");
                    TeamFeedFragment.getInstance().restartLoader();
                }
                Log.v("name", name + team + "");
            }
        });

        return view;
    }

    public void updateTeams(){
        ContentValues values = new ContentValues();
        values.put(TeamContract.KEY_TEAM, team);
        values.put(TeamContract.KEY_PLAYER, name);
        values.put(TeamContract.KEY_CHAR, teamChar);
        values.put(TeamContract.KEY_IS_RESERVED, "1");
        getActivity().getContentResolver().update(
                TeamProvider.CONTENT_URI,
                values,
                "team=?",
                new String[]{team}
        );
        values.clear();

        Team newTeam = new Team(name, team, teamChar);


        try {
            MainActivity.getInstance().attachPlayerToTheTeamNode(newTeam);
        } catch (IOException e) {
            e.printStackTrace();
        }




        String sql = TeamContract.createTeamTable(team);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(sql);



        for (int i = 0; i < dfu.teams.length; i++) {
            if (team.equals(dfu.teams[i])){

            } else {
               String insert = TeamContract.initializeTeamTable(team, dfu.teams[i]);

                sqLiteDatabase = dbHelper.getWritableDatabase();
                sqLiteDatabase.execSQL(insert);

            }
        }

        for (int i = 0; i < players.length; i++) {

                    String  insertOldTeamsAndPlayersToNewTeam= TeamContract.addPlayerToTeamTable(team, players[i],teamsWithPlayers[i]);
                    String insertNewTeamAndPlayerToOldTeams = TeamContract.addPlayerToTeamTable(teamsWithPlayers[i], name ,team);
                    sqLiteDatabase = dbHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL(insertOldTeamsAndPlayersToNewTeam);
                    sqLiteDatabase.execSQL(insertNewTeamAndPlayerToOldTeams);
        };
        dbHelper.close();
        getLoaderManager().restartLoader(0, null, this);

        //Restarts Application to recreate fragmentPagerAdapter and to avoid app crashing
        Intent mStartActivity = new Intent(getContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);


        /*String moro = StatisticsFragment.getInstance().makeFragmentName(0);
        String moro1 = StatisticsFragment.getInstance().makeFragmentName(1);
        String moro2 = StatisticsFragment.getInstance().makeFragmentName(2);
        Log.v("tag", moro + "");
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(moro);
        Fragment fragment1 = getActivity().getSupportFragmentManager().findFragmentByTag(moro1);
        Fragment fragment2 = getActivity().getSupportFragmentManager().findFragmentByTag(moro2);
        if(fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if(fragment1 != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment1).commit();
        if(fragment2 != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment2).commit();

        StatisticsFragment.getInstance().notifyDataChanges();*/



    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ///////////////////////////////////  CURSOR LOADER   ///////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    static final String[] PROJECTION = new String[] {"char", "team", "_id", "isReserved", "player"};
    String[] selectionArgs = {"0"};
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                TeamProvider.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<String> teamsFromDB = new ArrayList<String>();
        ArrayList<String> charsFromDB = new ArrayList<String>();
        ArrayList<String> idFromDB = new ArrayList<String>();
        ArrayList<String> playersFromDB = new ArrayList<String>();
        ArrayList<String> teamWithPlayersFromDB = new ArrayList<String>();


        try {

            while (data.moveToNext()) {

                if(data.getString(3).equals("1")){
                    if(data.getString(4).equals(null)){

                    }else{
                        playersFromDB.add(data.getString(4));
                    }
                    teamWithPlayersFromDB.add(data.getString(1));
                } else {
                    idFromDB.add(data.getString(2));
                    teamsFromDB.add(data.getString(1));
                    charsFromDB.add(data.getString(0));
                }
            }
        } finally {
            data.close();
        }


        teams = (String[]) teamsFromDB.toArray(new String[teamsFromDB.size()]);
        teamsWithPlayers = (String[]) teamWithPlayersFromDB.toArray(new String[teamsFromDB.size()]);
        chars = (String[]) charsFromDB.toArray(new String[charsFromDB.size()]);
        idArray = (String[]) charsFromDB.toArray(new String[charsFromDB.size()]);
        players = (String[]) playersFromDB .toArray(new String[playersFromDB.size()]);



        if(teams.length <= 0){
            tp.setVisibility(View.GONE);
            teamName.setVisibility(View.GONE);
            pickYourTeam.setVisibility(View.GONE);
            nameEditText.setVisibility(View.GONE);
            addName.setVisibility(View.GONE);
            allTeamsSelected.setVisibility(View.VISIBLE);
        } else {

            Log.v("teamname", team + "");

            id = idArray[0];

            tp.setMaxValue(chars.length - 1);
            tp.setDisplayedValues(chars);
            tp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    team = teams[newVal];
                    teamName.setText(team);
                    teamChar = chars[newVal];
                    id = idArray[newVal];

                }
            });
            int currentNumberPickerValue = tp.getValue();
            team = teams[currentNumberPickerValue];
            teamChar = chars[currentNumberPickerValue];
            teamName.setText(team);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ManageTeamsFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageTeamsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageTeamsFragment newInstance(String param1, String param2) {
        ManageTeamsFragment fragment = new ManageTeamsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
}
