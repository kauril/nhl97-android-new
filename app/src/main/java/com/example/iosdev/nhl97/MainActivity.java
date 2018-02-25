package com.example.iosdev.nhl97;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;


//MainActivity initializes application UI and works as an interface to the DBservice

public class MainActivity extends AppCompatActivity implements NewSeasonFragment.OnFragmentInteractionListener,
        TeamStatisticsFragment.OnFragmentInteractionListener,
        OverallStatisticsFragment.OnFragmentInteractionListener, CompareTeamStatisticsFragment.OnFragmentInteractionListener,
        StatisticsFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener, FeedFragment.OnFragmentInteractionListener,
        ManageTeamsFragment.OnFragmentInteractionListener, TeamFeedFragment.OnFragmentInteractionListener{

    MainFragment mf;
    ListFragment lf;
    FeedFragment ff;
    Toolbar toolbar;
    static Typeface nhlFont;
    TextView title;
    public boolean isCompareTeamsFragmentInitialized = false;

    SeasonNameOpenHelper seasonNameOpenHelper;

    LinearLayout defaultLayout;
    LinearLayout statisticsLayout;

    //MainActivity instance constructor
    private static MainActivity ma = null;
    public static MainActivity getInstance() {
        Log.v("getInstance", "ma");
        return ma;
    }

    //DBService initialization
    DBService mService;
    boolean mBound = false;
    public final static String EXTRA_MESSAGE = "Extra";
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DBService.class);
        String message = "hihihi";
        intent.putExtra(EXTRA_MESSAGE, message);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void addGameToNode(Game game) throws IOException {

        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            mService.addGameToTheNode(game);

        }
    }

    public void attachPlayerToTheTeamNode(Team team) throws IOException {

        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            mService.attachPlayerToTheTeamNode(team);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mf = new MainFragment();
        lf = new ListFragment();
        ff = new FeedFragment();

        /*LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_main, null);*/

        nhlFont = Typeface.createFromAsset(this.getAssets(),  "fonts/NHL.ttf");
        ma = this;
        initUI(savedInstanceState);
    }

    //Public Method for handling Application Ui initialization and also used when orientation is changed
    //as there is need to avoid onCreate method on that situation

    public void initUI(Bundle savedInstanceState){
        setContentView(R.layout.activity_main);
        defaultLayout = (LinearLayout) findViewById(R.id.defaultLayout);
        statisticsLayout = (LinearLayout) findViewById(R.id.statisticslayout);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.getBackground().setAlpha(100);

        if (savedInstanceState == null) {
            Log.v("savedi", "null");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.listFrameLayout, new ListFragment(), "list");
            ft.add(R.id.mainFrameLayout, new MainFragment(), "main");
            ft.add(R.id.feedFrameLayout, new FeedFragment(), "feed");
            ft.add(R.id.statisticsFrameLayout, new StatisticsFragment(), "statistics");

            ft.commit();
        } else {
            Log.v("savedi", "notnull");
            /*ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag("list");
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("main");
            FeedFragment feedFragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag("feed");
            StatisticsFragment statisticsFragment = (StatisticsFragment) getSupportFragmentManager().findFragmentByTag("statistics");*/
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            //ft.replace(R.id.listFrameLayout, listFragment, "list");
            ft.replace(R.id.mainFrameLayout, new MainFragment(), "main");
            //ft.replace(R.id.feedFrameLayout, feedFragment, "feed");
            //ft.replace(R.id.statisticsFrameLayout, statisticsFragment, "statistics");

            ft.commit();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //In manifest is declared that when screensize or orientation is changed the application handles those
    //situation itself. This is for avoiding activity to restart itself everytime orietation or screensize changes.

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

Log.v("orientation", "chanfged");
            //setContentView(R.layout.activity_main);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){

            Log.e("On Config Change","LANDSCAPE");
        }else{

            Log.e("On Config Change","PORTRAIT");
        }



    }



    public void setStatisticsVisibility(boolean isStatisticsVisible){
        if(isStatisticsVisible) {
            defaultLayout.setVisibility(View.GONE);
            statisticsLayout.setVisibility(View.VISIBLE);
        } else {
            defaultLayout.setVisibility(View.VISIBLE);
            statisticsLayout.setVisibility(View.GONE);
        }
    }
}
