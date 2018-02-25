package com.example.iosdev.nhl97;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    //Fragment with custom listview presenting latest added games

    private static FeedFragment ff = null;
    public boolean shouldInitialize = false;


    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;
    ListView lv;
    Cursor c;

    SQLiteDatabase db;
    ResultOpenHelper roHelper;



    //Instance constructor

    public static FeedFragment getInstance() {
        Log.v("getInstance", "ff");
        return ff;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ff = this;



        //Initializing Broadcast Receiver
        bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //Broadcast Receiver
    public static final String RECEIVE_JSON = "bc";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RECEIVE_JSON)) {
                String message = intent.getStringExtra("json");
                //Do something with the string

                getLoaderManager().restartLoader(0, null, getInstance());


            }
        }
    };
    LocalBroadcastManager bManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        view.getBackground().setAlpha(180);
        //Initializing Simplecursoradapter
        String[] fromFields = new String[]{"guestTeam", "guestGoals", "homeGoals", "homeTeam", "date"};
        int[] toFields = new int[]{R.id.text1, R.id.text2, R.id.text4, R.id.text5, R.id.text6};
        roHelper = new ResultOpenHelper(getContext());
        mAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.result_list,
                null,
                fromFields,
                toFields,
                0
        );

        lv = (ListView) view.findViewById(R.id.list);
        lv.setAdapter(mAdapter);

        //ContextMenu initialization

        registerForContextMenu(lv);

        //Calling Loader manager to fill Simplecursoradapter
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////                 CreateContexMenu                    //////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Delete");
        menu.add(0, v.getId(), 0, "Delete");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        if(item.getTitle()=="Delete"){
            c.moveToPosition(index);

            final String gName = c.getString(c.getColumnIndex("guestTeam"));
            final String hName = c.getString(c.getColumnIndex("homeTeam"));
            final String name = gName + " vs. " + hName;
            final String _id = c.getString(c.getColumnIndex("_id"));;


            new AlertDialog.Builder(getContext())
                    .setTitle("Delete game")
                    .setMessage("Are you sure you want to delete " + name + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v("deleteAlert",_id + "");
                            deleteGame(_id);
                            MainFragment.getInstance().resultCount = MainFragment.getInstance().resultCount -1;
                            ContentValues values = new ContentValues();
                            values.put(ResultContract.KEY_GUEST_TEAM, " ");
                            values.put(ResultContract.KEY_HOME_TEAM, " ");
                            values.put(ResultContract.KEY_GUEST_GOALS, 0);
                            values.put(ResultContract.KEY_HOME_GOALS, 0);
                            values.put(ResultContract.KEY_GUEST_SHOTS, 0);
                            values.put(ResultContract.KEY_HOME_SHOTS, 0);
                            values.put(ResultContract.KEY_OVERTIME, 0);
                            values.put(ResultContract.KEY_SHOOTOUTS, 0);
                            values.put(ResultContract.KEY_IS_DUMMY, 1);
                            values.put(ResultContract.KEY_DATE, " ");

                            //insert new row after deleting one
                            getActivity().getContentResolver().insert(
                                    ResultProvider.CONTENT_URI,
                                    values
                            );

                            MainFragment.getInstance().isResultDeleted = true;
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }else{
            return false;
        }
        return true;
    }

    //Public method for deleting row from resultDB

    public void deleteGame(String id){
        Log.v("deleteGame",id + "");
        getActivity().getContentResolver().delete(
                ResultProvider.CONTENT_URI,
                "name=?",
                new String[]{id});
        getLoaderManager().restartLoader(0, null, this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ///////////////////////////////////  CURSOR LOADER   ///////////////////////////////////////
    ///////////////////////////////////                  ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    static final String[] PROJECTION = new String[] {"_id", "guestTeam",
            "homeTeam", "guestGoals", "homeGoals", "date", "_id"};
    String[] selectionArgs = {"0"};
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                ResultProvider.CONTENT_URI,
                PROJECTION,
                "is_dummy=?",
                selectionArgs,
                "date DESC"
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null){
            mAdapter.swapCursor(data);
            c = data;
            Log.v("datacount", data.getCount() + "");
            MainFragment.getInstance().resultCount = data.getCount();
        } else {
            shouldInitialize = true;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public void restartLoader(){


            getLoaderManager().restartLoader(0, null, this);

    }



    //Lifecycle methods

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

    // Rest is some Boilerplate code
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

    // Required empty public constructor

    public FeedFragment() {

    }



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
}
