package com.example.iosdev.nhl97;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    //Array for adapter to populate a listview

    String[] listContent={"Add Game", "Manage Teams", "Statistics", "Start new season"};

    FrameLayout main;
    FrameLayout feed;
    FrameLayout statistics;
    private ListView lv;
    private FragmentManager fragmentManager;
    ArrayAdapter<String> myAdapter;
    static ListFragment lf;

    //Method to get instance of this fragment

    public static ListFragment getInstance() {
        Log.v("getInstance", "tff");
        return lf;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lf = this;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,
                container, false);
        v.getBackground().setAlpha(180);
        //Creating an populating ArrayAdapter


            myAdapter = new
                    ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1,
                    listContent);

        lv = (ListView) v.findViewById(R.id.left_list);


        //Adapter attached to listview

        lv.setAdapter(myAdapter);

        //OnitemClickListener for choosing visible fragments in UI

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Log.v("itemclick", position + "");
                String item = listContent[ position ];
                Log.v("itemclick", item + "");
                if (item.equals("Add Game")){

                    MainFragment mainFragment = new MainFragment();
                    FeedFragment feedFragment = new FeedFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrameLayout, mainFragment)
                            .replace(R.id.feedFrameLayout, feedFragment)
                            .commit();
                    MainActivity.getInstance().setStatisticsVisibility(false);
                } else if(item.equals("Manage Teams")){
                    ManageTeamsFragment manageTeamsFragmentTest = ManageTeamsFragment.getInstance();
                    TeamFeedFragment teamFeedFragmentTest = TeamFeedFragment.getInstance();
                    if(manageTeamsFragmentTest != null || teamFeedFragmentTest != null){
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFrameLayout, manageTeamsFragmentTest)
                                .replace(R.id.feedFrameLayout, teamFeedFragmentTest)
                                .commit();
                        MainActivity.getInstance().setStatisticsVisibility(false);
                    }else {
                        ManageTeamsFragment manageTeamsFragment = new ManageTeamsFragment();
                        TeamFeedFragment teamFeedFragment = new TeamFeedFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFrameLayout, manageTeamsFragment)
                                .replace(R.id.feedFrameLayout, teamFeedFragment)
                                .commit();
                        MainActivity.getInstance().setStatisticsVisibility(false);
                    }
                } else if(item.equals("Statistics")){

                    StatisticsFragment statisticsFragment = new StatisticsFragment();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrameLayout, statisticsFragment)
                            .commit();

                    MainActivity.getInstance().setStatisticsVisibility(true);

                } else {
                    NewSeasonFragment newSeasonFragment = new NewSeasonFragment();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrameLayout, newSeasonFragment)
                            .commit();
                    MainActivity.getInstance().setStatisticsVisibility(false);
                }
            }
        });
        return v;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



}
