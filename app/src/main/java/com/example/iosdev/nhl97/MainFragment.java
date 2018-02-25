package com.example.iosdev.nhl97;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

import io.socket.emitter.Emitter;

import static android.R.attr.value;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    //Fragment for adding new game

    //UI element declaration
    Button addGame;
    TextView guestTeamName;
    TextView homeTeamName;
    TextView guest;
    TextView goals;
    TextView home;
    TextView shots;
    TextView selectTeams;
    LinearLayout main;
    Button assignTeams;
    CheckBox overtime;
    CheckBox shootout;
    NumberPicker tpg, gpg, tph, gph, sg, sh;


    //Reference declarations

    TeamOpenHelper dbHelper;
    ResultOpenHelper roHelper;
    SQLiteDatabase sqLiteDatabase;
    private static MainFragment mf = null;
    View v;
    TeamContract teamContract;



    public int resultCount = 0;
    public boolean isResultDeleted = false;
    //Method to get instance of this fragment

    public static MainFragment getInstance() {
        Log.v("getInstance", "tff");
        return mf;

    }

    //Declaring typeface to set custom font to numberpickers
    static Typeface nhlFont;

    //Google firebase references
    //requires <uses-permission android:name="android.permission.INTERNET"/> in manifest file
    FirebaseDatabase db;
    DatabaseReference mDatabase;
    DatabaseReference games;
    DatabaseReference season;

    DatabaseReference urls;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();





    //Class variable declaration


    public Boolean isCursorLoaded = false;


    private String guestTeam;
    private String homeTeam;
    private Integer guestGoals = 0;
    private Integer homeGoals = 0;
    private Integer guestShots = 0;
    private Integer homeShots = 0;
    private Integer gameDuration = 15;
    private boolean isOvertime = false;
    private boolean isShootout = false;
    private String date = "";
    private String playerGuest;
    private String playerHome;
    String[] teams;
    String[] chars;
    String[] players;


    // Required empty public constructor

    public MainFragment() {

    }

    // Variables and methods for taking picture

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String pictureImagePath = "";

    //Initializes camera

    private void dispatchTakePictureIntent() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";

        // Requires <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> in manifest file

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // This method is run after the photo is taken
    // Photo is saved into firebase storage

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureImagePath);
            Log.v("isphotofound", "ispf");
                if(imgFile.exists()){
                    Log.v("Succes", "photofound");
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();
                    Long tsLong = System.currentTimeMillis();
                    String ts = getDate(tsLong);
                    StorageReference sixoneImagesRef = storageRef.child("images/" + ts + ".jpg");
                    UploadTask uploadTask = sixoneImagesRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            String meta = taskSnapshot.toString();


                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            String url = downloadUrl.toString();
                            Long tsLong = System.currentTimeMillis();
                            String ts = getDate(tsLong);
                            ImageMetaData imageMetaData = new ImageMetaData(guestTeam, guestGoals, homeGoals, homeTeam, url, ts);
                            DatabaseReference pushImageUri;
                            pushImageUri = season.child("urls").push();

                            pushImageUri.setValue(imageMetaData);
                            Log.v("dluri", downloadUrl + "");
                        }
                    });

                }
            }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mf = this;

        String databaseName = teamContract.DATABASE_NAME;

        Log.v("databasename mainfragus", " " + databaseName);

        //Initializing google firebase db root and path to /games URL
        mDatabase =  db.getInstance().getReference();
        season = mDatabase.child("Seasoncreated: " + databaseName);
        games = mDatabase.child("games");
        urls = mDatabase.child("urls");

        //Initializing custom font for number pickers
        nhlFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/NHL.ttf");




        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    private void collectGames(Map<String,Object> Game) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : Game.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("guestTeam"));
        }

        System.out.println(phoneNumbers.toString());
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
    public void initializeView(View view) {
        //Number picker and checkBox initialization

        tpg = (NumberPicker) view.findViewById(R.id.teamPickerGuest);
        gpg = (NumberPicker) view.findViewById(R.id.goalPickerGuest);
        tph = (NumberPicker) view.findViewById(R.id.teamPickerHome);
        gph = (NumberPicker) view.findViewById(R.id.goalPickerHome);
        sg = (NumberPicker) view.findViewById(R.id.shotsGuest);
        sh = (NumberPicker) view.findViewById(R.id.shotsHome);
        overtime = (CheckBox) view.findViewById(R.id.overtimeCheckBox);
        shootout = (CheckBox) view.findViewById(R.id.shootoutCheckBox);

        //UI element attachments

        guestTeamName = (TextView) view.findViewById(R.id.guestTeamName);
        homeTeamName = (TextView) view.findViewById(R.id.homeTeamName);
        guest = (TextView) view.findViewById(R.id.guest);
        goals = (TextView) view.findViewById(R.id.goals);
        home = (TextView) view.findViewById(R.id.home);
        shots = (TextView) view.findViewById(R.id.shots);
        selectTeams = (TextView) view.findViewById(R.id.select_teams);
        assignTeams = (Button) view.findViewById(R.id.assignTeams);
        main = (LinearLayout) view.findViewById(R.id.mainLinearLayout);
        main.setVisibility(View.GONE);
        addGame = (Button) view.findViewById(R.id.addBtn);


        //Numberpickers are customized to get team logos on then

        customizeNumberPicker(tpg);
        customizeNumberPicker(tph);

        //initialization of cursorloader
        if(isCursorLoaded){
            restartLoader();
        } else {
            getLoaderManager().initLoader(0, null, this);
        }

        //Reference to TeamOpenHelper for further calls to database

        dbHelper = new TeamOpenHelper(getContext());
        roHelper = new ResultOpenHelper(getContext());

        //Setting min and max values to numberpickers

        gpg.setMinValue(0);
        gpg.setMaxValue(50);
        gph.setMinValue(0);
        gph.setMaxValue(50);
        sg.setMinValue(0);
        sg.setMaxValue(100);
        sh.setMinValue(0);
        sh.setMaxValue(100);

        //Disables numberpicker editing

        tpg.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        gpg.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        tph.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        gph.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        sg.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        sh.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //Checkbox listeners

        overtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOvertime = true;
                    shootout.setChecked(false);
                    shootout.setVisibility(View.VISIBLE);
                } else {
                    isOvertime = false;
                    isShootout = false;
                    shootout.setVisibility(View.GONE);

                }

            }
        });

        shootout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isShootout = true;
                } else {

                    isShootout = false;
                }

            }
        });



        //AddGame button onclick listener

        addGame.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Log.v("Goals",guestGoals + " " + homeGoals + "");
               //Checks that other of teams is winning, DENIK in toast is my friend which I'm going to notify
                   if (homeGoals.equals(guestGoals)) {
                       Toast.makeText(getActivity(), "One of the teams must win DENIK", Toast.LENGTH_SHORT).show();
                       Log.v("Goals1",guestGoals + " " + homeGoals + "");
                       //Check that there is at least equal amount of shots towards goal as there is goals
                   } else if (homeGoals - homeShots > 0 || guestGoals - guestShots > 0) {
                       Toast.makeText(getActivity(), "Check that shots are added correctly DENIK", Toast.LENGTH_SHORT).show();
                   } else {
                       //Checks that teams opponent is not itself
                       if (guestTeam.equals(homeTeam)) {
                           Toast.makeText(getActivity(), guestTeam + " Can't play against itself DENIK", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       Log.v("Goals2",guestGoals + " " + homeGoals + "");
                       //Getting current time
                       Long tsLong = System.currentTimeMillis();
                       String ts = getDate(tsLong);

                       //Initializing overtime and shootout data

                       int overTime = 0;
                       int shootOut = 0;
                       int winningOvertime = 0;
                       int losingOvertime = 0;
                       int winningShootout = 0;
                       int losingShootout = 0;



                       //checking if shootout is checked and that other team wins only by one goal

                       if (isShootout) {
                           if (guestGoals > homeGoals) {
                               if (guestGoals - homeGoals > 1) {
                                   Toast.makeText(getActivity(), "After shootouts team can only win by one goal DENIK", Toast.LENGTH_SHORT).show();
                                   return;
                               } else {
                                   shootOut = 1;
                                   winningShootout = 1;
                                   losingShootout = 1;

                               }

                           } else {
                               if (homeGoals - guestGoals > 1) {
                                   Toast.makeText(getActivity(), "After shootouts team can only win by one goal DENIK", Toast.LENGTH_SHORT).show();
                                   return;
                               } else {
                                   shootOut = 1;
                                   winningShootout = 1;
                                   losingShootout = 1;

                               }
                           }

                       }
                       //checking if overtime is checked and that other team wins only by one goal
                       if (isOvertime) {
                           if (guestGoals > homeGoals) {
                               if (guestGoals - homeGoals > 1) {
                                   Toast.makeText(getActivity(), "After overtime team can only win by one goal DENIK", Toast.LENGTH_SHORT).show();
                                   return;
                               } else {
                                   if (isShootout) {
                                       overTime = 1;
                                       winningOvertime = 0;
                                       losingOvertime = 0;
                                   } else {
                                       overTime = 1;
                                       winningOvertime = 1;
                                       losingOvertime = 1;
                                   }
                               }

                           } else {
                               if (homeGoals - guestGoals > 1) {
                                   Toast.makeText(getActivity(), "After overtime team can only win by one goal DENIK", Toast.LENGTH_SHORT).show();
                                   return;
                               } else {
                                   if (isShootout) {
                                       overTime = 1;
                                       winningOvertime = 0;
                                       losingOvertime = 0;
                                   } else {
                                       overTime = 1;
                                       winningOvertime = 1;
                                       losingOvertime = 1;
                                   }
                               }
                           }

                       }


                       if (homeGoals > guestGoals) {
                           //Checks if home team wins 6-1 or 10-0. If so camera is launched to for taking picture of that event
                           //Taken picture is saved into firebase storage
                           if (homeGoals == 6 && guestGoals == 1 || homeGoals == 10 && guestGoals == 0) {
                               dispatchTakePictureIntent();
                           }
                           //Results are added to team database
                           String sql = TeamContract.WinningTeam(homeTeam, homeGoals, guestGoals, homeShots, guestShots, overTime, winningOvertime, shootOut, winningShootout, 1, 0);
                           String sql1 = TeamContract.LosingTeam(guestTeam, guestGoals, homeGoals, guestShots, homeShots, overTime, losingOvertime, shootOut, losingShootout, 0, 1);
                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql);
                           sqLiteDatabase.execSQL(sql1);
                           dbHelper.close();

                           //season = mDatabase.child("Seasoncreated: " + databaseName);




                           DatabaseReference pushResult;
                           pushResult = season.child("games").push();

                           Game game = new Game(guestTeam, guestGoals, homeGoals, homeTeam, guestShots, homeShots, isOvertime, isShootout, ts);
                           pushResult.setValue(game);

                           // Adding data to winning hometeams general statistics
                           // nää periaatteessa toimii mut saattaa olla et joku sekunti limitti firebases ni pelottaa vähä

                           final int finalOverTime = overTime;
                           final int finalShootOut = shootOut;
                           final int finalWinningOvertime = winningOvertime;
                           final int finalWinningShootout = winningShootout;
                           season.child("teams").child(homeTeam).child("stats").addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team home = dataSnapshot.getValue(Team.class);

                                   Log.v("overwins", home.overtime_wins + "");
                                   Log.v("final", finalOverTime + "");
                                   home.goals_for = home.goals_for + homeGoals;
                                   home.goals_against = home.goals_against + guestGoals;
                                   home.shots_for = home.shots_for + homeShots;
                                   home.shots_against = home.shots_against + guestShots;
                                   home.home_wins = home.home_wins + 1;
                                   home.overtime_wins = home.overtime_wins + finalWinningOvertime;
                                   home.overtimes = home.overtimes + finalOverTime;
                                   home.shootout_wins = home.shootout_wins + finalWinningShootout;
                                   home.shootouts = home.shootouts + finalShootOut;
                                   home.wins = home.wins + 1;

                                   season.child("teams").child(homeTeam).child("stats").setValue(home);

                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to winning hometeams guestteam statistics for a comparison
                           // Adding wins + 1 to hometeams guestteam means that hometeam has won guestTeam

                           season.child("teams").child(homeTeam).child(guestTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team guestTeamInHome = dataSnapshot.getValue(Team.class);


                                   guestTeamInHome.goals_for = guestTeamInHome.goals_for + homeGoals;
                                   guestTeamInHome.goals_against = guestTeamInHome.goals_against + guestGoals;
                                   guestTeamInHome.shots_for = guestTeamInHome.shots_for + homeShots;
                                   guestTeamInHome.shots_against = guestTeamInHome.shots_against + guestShots;
                                   guestTeamInHome.home_wins = guestTeamInHome.home_wins + 1;
                                   guestTeamInHome.overtime_wins = guestTeamInHome.overtime_wins + finalWinningOvertime;
                                   guestTeamInHome.overtimes = guestTeamInHome.overtimes + finalOverTime;
                                   guestTeamInHome.shootout_wins = guestTeamInHome.shootout_wins + finalWinningShootout;
                                   guestTeamInHome.shootouts = guestTeamInHome.shootouts + finalShootOut;
                                   guestTeamInHome.wins = guestTeamInHome.wins + 1;

                                   season.child("teams").child(homeTeam).child(guestTeam).setValue(guestTeamInHome);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to losing guestteams general statistics

                           final int finalLosingOvertime = losingOvertime;
                           final int finalLosingShootout = losingShootout;
                           season.child("teams").child(guestTeam).child("stats").addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team guest = dataSnapshot.getValue(Team.class);

                                   Log.v("overwins", guest.overtime_wins + "");
                                   Log.v("final", finalOverTime + "");
                                   guest.goals_for = guest.goals_for + guestGoals;
                                   guest.goals_against = guest.goals_against + homeGoals;
                                   guest.shots_for = guest.shots_for + guestShots;
                                   guest.shots_against = guest.shots_against + homeShots;
                                   guest.guest_loses = guest.guest_loses + 1;
                                   guest.overtime_loses = guest.overtime_loses + finalLosingOvertime;
                                   guest.overtimes = guest.overtimes + finalOverTime;
                                   guest.shootout_loses = guest.shootout_loses + finalLosingShootout;
                                   guest.shootouts = guest.shootouts + finalShootOut;
                                   guest.loses = guest.loses + 1;

                                   season.child("teams").child(guestTeam).child("stats").setValue(guest);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to losing guestteams hometeam statistics for a comparison
                           // Adding loses + 1 to guestteams hometeam means that guestteam has lost to homeTEam

                           season.child("teams").child(guestTeam).child(homeTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team homeTeamInGuest = dataSnapshot.getValue(Team.class);
                                   homeTeamInGuest.goals_for = homeTeamInGuest.goals_for + guestGoals;
                                   homeTeamInGuest.goals_against = homeTeamInGuest.goals_against + homeGoals;
                                   homeTeamInGuest.shots_for = homeTeamInGuest.shots_for + guestShots;
                                   homeTeamInGuest.shots_against = homeTeamInGuest.shots_against + homeShots;
                                   homeTeamInGuest.guest_loses = homeTeamInGuest.guest_loses + 1;
                                   homeTeamInGuest.overtime_loses = homeTeamInGuest.overtime_loses + finalLosingOvertime;
                                   homeTeamInGuest.overtimes = homeTeamInGuest.overtimes + finalOverTime;
                                   homeTeamInGuest.shootout_loses = homeTeamInGuest.shootout_loses + finalLosingShootout;
                                   homeTeamInGuest.shootouts = homeTeamInGuest.shootouts + finalShootOut;
                                   homeTeamInGuest.loses = homeTeamInGuest.loses + 1;

                                   season.child("teams").child(guestTeam).child(homeTeam).setValue(homeTeamInGuest);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           Log.v("jälkeen", "kyseelyä");
                           //Check how many wins current winner has
                           String checkWins = "SELECT wins FROM team WHERE team" +
                                   " = '" + homeTeam + "';";

                           Cursor cursor = null;
                           int wins = 0;
                           sqLiteDatabase = dbHelper.getReadableDatabase();
                           cursor = sqLiteDatabase.rawQuery(checkWins, null);
                           if(cursor.moveToFirst()) {
                               wins = cursor.getInt(cursor.getColumnIndex("wins"));
                               Log.v("wins", wins + "");
                           }else{
                               Log.v("cursor","null");
                           }
                           cursor.close();
                           //Result is added to winning teams own table
                           String sql3 = TeamContract.updateWinningTeamOwnTable(homeTeam, guestTeam, homeGoals, guestGoals, homeShots, guestShots, overTime, winningOvertime, shootOut, winningShootout,  1, 0, 0 ,0);
                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql3);
                           dbHelper.close();

                           //Result is added to losing teams own table
                           String sql4 = TeamContract.updateLosingTeamOwnTable(guestTeam, homeTeam, guestGoals, homeGoals, guestShots, homeShots, overTime, losingOvertime, shootOut, losingShootout,  0, 0,  0, 1);
                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql4);
                           dbHelper.close();

                           Toast.makeText(getActivity(), guestTeam + " - " + homeTeam + " " + guestGoals + " - " + homeGoals + " added!", Toast.LENGTH_LONG).show();



                       } else if (guestGoals > homeGoals) {
                           //Checks if guest team wins 6-1 or 10-0. If so camera is launched to for taking picture of that event
                           if (guestGoals == 6 && homeGoals == 1 || guestGoals == 10 && homeGoals == 0) {

                               dispatchTakePictureIntent();
                           }

                           DatabaseReference pushResult;
                           pushResult = season.child("games").push();

                           Game game = new Game(guestTeam, guestGoals, homeGoals, homeTeam, guestShots, homeShots, isOvertime, isShootout, ts);
                           pushResult.setValue(game);

                           // Adding data to winning guestTeams general statistics
                           // nää periaatteessa toimii mut saattaa olla et joku sekunti limitti firebases ni pelottaa vähä

                           final int finalOverTime = overTime;
                           final int finalShootOut = shootOut;
                           final int finalWinningOvertime = winningOvertime;
                           final int finalWinningShootout = winningShootout;
                           season.child("teams").child(guestTeam).child("stats").addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team guest = dataSnapshot.getValue(Team.class);

                                   guest.goals_for = guest.goals_for + guestGoals;
                                   guest.goals_against = guest.goals_against + homeGoals;
                                   guest.shots_for = guest.shots_for + guestShots;
                                   guest.shots_against = guest.shots_against + homeShots;
                                   guest.guest_wins = guest.guest_wins + 1;
                                   guest.overtime_wins = guest.overtime_wins + finalWinningOvertime;
                                   guest.overtimes = guest.overtimes + finalOverTime;
                                   guest.shootout_wins = guest.shootout_wins + finalWinningShootout;
                                   guest.shootouts = guest.shootouts + finalShootOut;
                                   guest.wins = guest.wins + 1;

                                   season.child("teams").child(guestTeam).child("stats").setValue(guest);

                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to winning guestteams hometeam statistics for a comparison
                           // Adding wins + 1 to guestteams hometeam means that guestteam has won hometeam

                           season.child("teams").child(guestTeam).child(homeTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team homeTeamInGuest = dataSnapshot.getValue(Team.class);


                                   homeTeamInGuest.goals_for = homeTeamInGuest.goals_for + guestGoals;
                                   homeTeamInGuest.goals_against = homeTeamInGuest.goals_against + homeGoals;
                                   homeTeamInGuest.shots_for = homeTeamInGuest.shots_for + guestShots;
                                   homeTeamInGuest.shots_against = homeTeamInGuest.shots_against + homeShots;
                                   homeTeamInGuest.guest_wins = homeTeamInGuest.guest_wins + 1;
                                   homeTeamInGuest.overtime_wins = homeTeamInGuest.overtime_wins + finalWinningOvertime;
                                   homeTeamInGuest.overtimes = homeTeamInGuest.overtimes + finalOverTime;
                                   homeTeamInGuest.shootout_wins = homeTeamInGuest.shootout_wins + finalWinningShootout;
                                   homeTeamInGuest.shootouts = homeTeamInGuest.shootouts + finalShootOut;
                                   homeTeamInGuest.wins = homeTeamInGuest.wins + 1;

                                   season.child("teams").child(guestTeam).child(homeTeam).setValue(homeTeamInGuest);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to losing hometeams general statistics

                           final int finalLosingOvertime = losingOvertime;
                           final int finalLosingShootout = losingShootout;
                           season.child("teams").child(homeTeam).child("stats").addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team home = dataSnapshot.getValue(Team.class);


                                   home.goals_for = home.goals_for + homeGoals;
                                   home.goals_against = home.goals_against + guestGoals;
                                   home.shots_for = home.shots_for + homeShots;
                                   home.shots_against = home.shots_against + guestShots;
                                   home.home_loses = home.home_loses + 1;
                                   home.overtime_loses = home.overtime_loses + finalLosingOvertime;
                                   home.overtimes = home.overtimes + finalOverTime;
                                   home.shootout_loses = home.shootout_loses + finalLosingShootout;
                                   home.shootouts = home.shootouts + finalShootOut;
                                   home.loses = home.loses + 1;

                                   season.child("teams").child(homeTeam).child("stats").setValue(home);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           // Adding data to losing hometeams guestteam statistics for a comparison
                           // Adding loses + 1 to hometeams guestteam means that hometeam has lost to guestteam

                           season.child("teams").child(homeTeam).child(guestTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   Team guestTeamInHome = dataSnapshot.getValue(Team.class);
                                   guestTeamInHome.goals_for = guestTeamInHome.goals_for + homeGoals;
                                   guestTeamInHome.goals_against = guestTeamInHome.goals_against + guestGoals;
                                   guestTeamInHome.shots_for = guestTeamInHome.shots_for + homeShots;
                                   guestTeamInHome.shots_against = guestTeamInHome.shots_against + guestShots;
                                   guestTeamInHome.home_loses = guestTeamInHome.home_loses + 1;
                                   guestTeamInHome.overtime_loses = guestTeamInHome.overtime_loses + finalLosingOvertime;
                                   guestTeamInHome.overtimes = guestTeamInHome.overtimes + finalOverTime;
                                   guestTeamInHome.shootout_loses = guestTeamInHome.shootout_loses + finalLosingShootout;
                                   guestTeamInHome.shootouts = guestTeamInHome.shootouts + finalShootOut;
                                   guestTeamInHome.loses = guestTeamInHome.loses + 1;

                                   season.child("teams").child(homeTeam).child(guestTeam).setValue(guestTeamInHome);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   // ...
                               }
                           });

                           //Results are added to team database
                           String sql = TeamContract.WinningTeam(guestTeam, guestGoals, homeGoals, guestShots, homeShots, overTime, winningOvertime, shootOut, winningShootout, 0, 1);
                           String sql1 = TeamContract.LosingTeam(homeTeam, homeGoals, guestGoals, homeShots, guestShots, overTime, losingOvertime, shootOut, losingShootout, 1, 0);

                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql);
                           sqLiteDatabase.execSQL(sql1);
                           dbHelper.close();
                           //Check how many wins current winner has
                           String checkWins = "SELECT wins FROM team WHERE team" +
                                   " = '" + homeTeam + "';";

                           Cursor cursor = null;
                           int wins = 0;
                           sqLiteDatabase = dbHelper.getReadableDatabase();
                           cursor = sqLiteDatabase.rawQuery(checkWins, null);
                           if(cursor.moveToFirst()) {
                               wins = cursor.getInt(cursor.getColumnIndex("wins"));
                               Log.v("wins", wins + "");
                           }else{
                               Log.v("cursor","null");
                           }
                           cursor.close();

                           //Result is added to winning teams own tableguestTeam, homeTeam, guestGoals, homeGoals, guestShots, homeShots, overTime, losingOvertime, shootOut, losingShootout
                           String sql3 = TeamContract.updateWinningTeamOwnTable(guestTeam, homeTeam, guestGoals, homeGoals, guestShots, homeShots, overTime, winningOvertime, shootOut, winningShootout, 0, 0, 1, 0);
                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql3);
                           dbHelper.close();

                           //Result is added to losing teams own table
                           String sql4 = TeamContract.updateLosingTeamOwnTable(homeTeam, guestTeam, homeGoals, guestGoals, homeShots, guestShots, overTime, losingOvertime, shootOut, losingShootout, 0, 1, 0, 0);
                           sqLiteDatabase = dbHelper.getWritableDatabase();
                           sqLiteDatabase.execSQL(sql4);
                           dbHelper.close();

                           Toast.makeText(getActivity(), guestTeam + " - " + homeTeam + " " + guestGoals + " - " + homeGoals + " added!", Toast.LENGTH_LONG).show();

                       }




                                ContentValues values = new ContentValues();
                                values.put(ResultContract.KEY_GUEST_TEAM, guestTeam);
                                values.put(ResultContract.KEY_HOME_TEAM, homeTeam);
                                values.put(ResultContract.KEY_GUEST_GOALS, guestGoals);
                                values.put(ResultContract.KEY_HOME_GOALS, homeGoals);
                                values.put(ResultContract.KEY_GUEST_SHOTS, guestShots);
                                values.put(ResultContract.KEY_HOME_SHOTS, homeShots);
                                values.put(ResultContract.KEY_IS_DUMMY, 0);
                                values.put(ResultContract.KEY_OVERTIME, overTime);
                                values.put(ResultContract.KEY_SHOOTOUTS, shootOut);
                                values.put(ResultContract.KEY_DATE, ts);
                                Log.v("resultCount", resultCount + "");
                                getActivity().getContentResolver().insert(
                                        ResultProvider.CONTENT_URI,
                                        values
                                );
                                values.clear();


                      /* getActivity().getContentResolver().insert(
                               ResultProvider.CONTENT_URI,
                               values);
                       values.clear();*/



                       //Push result to node.js

                       /*try {
                           MainActivity.getInstance().addGameToNode(game);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }*/


                       //format goals and shots to avoid game adding multiple times

                       gpg.setValue(0);
                       gph.setValue(0);
                       sg.setValue(0);
                       sh.setValue(0);

                       guestGoals = 0;
                       homeGoals = 0;
                       guestShots = 0;
                       homeShots = 0;

                       isOvertime = false;
                       isShootout = false;

                       overtime.setChecked(false);
                       shootout.setChecked(false);









                       //Update FeedFragment after adding new game

                       FeedFragment.getInstance().restartLoader();

                       //Update CompareStaatisticsFragment if is already initialized
                       if(MainActivity.getInstance().isCompareTeamsFragmentInitialized) {
                           CompareTeamStatisticsFragment.getInstance().restartLoader();
                           OverallStatisticsFragment.getInstance().restartLoader();
                       }
                    }
                }
            }
        );


        games.orderByChild("guestGoals").limitToLast(2).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Game game = dataSnapshot.getValue(Game.class);
                System.out.println(dataSnapshot.getKey() + " huihuihui " + game.guestGoals + " meters tall.");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        //Numberpicker valueChangeListeners


        gpg.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub


                guestGoals = newVal;
            }
        });

        gph.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub


                homeGoals = newVal;
            }
        });
        sg.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

                guestShots = newVal;

            }
        });
        sh.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub


                homeShots = newVal;
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);



        initializeView(v);


        return v;
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
        players = (String[]) playersFromDB .toArray(new String[playersFromDB.size()]);
        Log.v("arrays", Arrays.toString(teams));
        //If there's no players assigned to teams application prompts user to do so first...
        if(teams.length <= 1){
            Log.v("teamsleg", "alle1");
            main.setVisibility(View.GONE);
            selectTeams.setVisibility(View.VISIBLE);
            assignTeams.setVisibility(View.VISIBLE);

            //When new season starts this button assigned to this onclick listener is visible to the user
            //Because any teams hasn't yet attached to players, user must do so first to start adding games

            assignTeams.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.v("assignteams", "clicked");
                    ManageTeamsFragment manageTeamsFragment = new ManageTeamsFragment();
                    TeamFeedFragment teamFeedFragment = new TeamFeedFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrameLayout, manageTeamsFragment)
                            .replace(R.id.feedFrameLayout, teamFeedFragment)
                            .commit();

                }
            });



        } else {




            //... when there's two or more players assigned to teams, numberpickers are visible and
            //populated with those teams
            main.setVisibility(View.VISIBLE);

            //Guestteampicker initialization
            tpg.setMinValue(0);
            tpg.setMaxValue(chars.length - 1);
            tpg.setDisplayedValues(chars);


                //Guestteampicker onvaluechange listener
            tpg.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    guestTeam = teams[newVal];
                    playerGuest = players[newVal];
                    guestTeamName.setText(guestTeam + "\n" + playerGuest );

                }
            });

            //Initial value is attached to textview
            int currentNumberPickerValueGuest = tpg.getValue();
            playerGuest = players[currentNumberPickerValueGuest];
            guestTeam = teams[currentNumberPickerValueGuest];
            guestTeamName.setText(guestTeam + "\n" + playerGuest );

            //Guestteampicker initialization

            tph.setMinValue(0);
            tph.setMaxValue(chars.length - 1);
            tph.setDisplayedValues(chars);

            //Hometeampicker onvaluechange listener
            tph.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    homeTeam = teams[newVal];
                    playerHome = players[newVal];
                    homeTeamName.setText(homeTeam + "\n" + playerHome );


                }
            });

            //Initial value is attached to textview
            int currentNumberPickerValueHome = tpg.getValue();
            playerHome = players[currentNumberPickerValueHome];
            homeTeam = teams[currentNumberPickerValueHome];
            homeTeamName.setText(homeTeam + "\n" + playerHome );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //Method to restart cursorloader
    public void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
    }






    //Fragment lifecycle methods

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

    //Function for getting date



    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return date;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private OnFragmentInteractionListener mListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View subview = inflater.inflate(R.layout.fragment_main, viewGroup);

        initializeView(subview);

    }

}