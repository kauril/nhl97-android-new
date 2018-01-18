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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamFeedFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{

    SimpleCursorAdapter mAdapter;
    ListView lv;

    TextView teamChar;
    static Typeface nhlFont;
    private static TeamFeedFragment tff = null;

    public static TeamFeedFragment getInstance() {
        Log.v("getInstance", "tff");
        return tff;

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tff = this;



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_feed, container, false);
        view.getBackground().setAlpha(180);
        nhlFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/NHL.ttf");
        String[] fromFields = new String[]{"team", "player", "games_played", "wins", "loses", "goals_for", TeamContract.KEY_GOALS_AGAINST};
        int[] toFields = new int[]{ R.id.team, R.id.player, R.id.games_played, R.id.wins, R.id.loses, R.id.goals_for, R.id.goals_against};
        mAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.team_list,
                null,
                fromFields,
                toFields,
                0
        );









        lv = (ListView) view.findViewById(R.id.teamlist);
        lv.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
        return view;
    }




    static final String[] PROJECTION = new String[] {"_id, char",
            "team", "player", "isReserved", "games_played", "wins", "loses", TeamContract.KEY_GOALS_FOR, TeamContract.KEY_GOALS_AGAINST};

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeamFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeamFeedFragment newInstance(String param1, String param2) {
        TeamFeedFragment fragment = new TeamFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeamFeedFragment() {
        // Required empty public constructor
    }
}
