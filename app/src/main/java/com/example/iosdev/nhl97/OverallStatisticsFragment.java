package com.example.iosdev.nhl97;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverallStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverallStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverallStatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    // Store instance variables
    private String title;
    private int page;
    String sortOrder = "games_played DESC";
    String[] sortStatsContent ={"Games Played", "Wins", "Loses", "Player", "Team"};
    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;
    ListView lv;
    Cursor c;
    static OverallStatisticsFragment osf;
    NumberPicker sortStats;

    public static OverallStatisticsFragment getInstance() {

        return osf;

    }






    private OnFragmentInteractionListener mListener;

    public OverallStatisticsFragment() {
        // Required empty public constructor
    }



    public static OverallStatisticsFragment newInstance(int page, String title) {
        OverallStatisticsFragment fragment = new OverallStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        osf = this;
        if (getArguments() != null) {
            page = getArguments().getInt("someInt", 0);
            title = getArguments().getString("someTitle");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_statistics, container, false);

        sortStats = (NumberPicker) view.findViewById(R.id.sortStats);

        sortStats.setMinValue(0);
        sortStats.setMaxValue(sortStatsContent.length - 1);
        sortStats.setDisplayedValues(sortStatsContent);

        sortStats.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //Guestteampicker onvaluechange listener
        sortStats.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                String value = sortStatsContent[newVal];
                if(value.equals("Games Played")){
                    sortOrder = "games_played DESC";
                } else if(value.equals("Wins")){
                    sortOrder = "wins DESC";
                } else if(value.equals("Loses")){
                    sortOrder = "loses DESC";
                } else if(value.equals("Player")){
                    sortOrder = "player ASC";
                } else {
                    sortOrder = "team ASC";
                }
                Log.v("sortOrder", sortOrder);
                restartLoader();


            }
        });

        String[] fromFields = new String[]{"team", "player", "games_played", "wins", "loses"};
        int[] toFields = new int[]{ R.id.team, R.id.player, R.id.games_played, R.id.wins, R.id.loses};

        mAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.team_list_statistics,
                null,
                fromFields,
                toFields,
                0
        );
        lv = (ListView) view.findViewById(R.id.topList);
        lv.setAdapter(mAdapter);

        //Calling Loader manager to fill Simplecursoradapter
        getLoaderManager().initLoader(0, null, this);

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

    static final String[] PROJECTION = new String[] {"_id, char",
            "team", "player", "isReserved", "games_played", "wins", "loses"};

    String[] selectionArgs = {"1"};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                TeamProvider.CONTENT_URI,
                PROJECTION,
                "isReserved=?",
                selectionArgs,
                sortOrder
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
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
