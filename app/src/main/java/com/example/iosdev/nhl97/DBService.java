package com.example.iosdev.nhl97;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by iosdev on 24.2.2017.
 */

public class DBService extends Service {


    // Service for networking, not in use at the moment


    //Socket mSocket;


    private final IBinder mBinder = new LocalBinder();
    private String guestTeam = "";
    private String homeTeam = "";
    private Integer guestGoals = 0;
    private Integer homeGoals = 0;
    private Integer guestShots = 0;
    private Integer homeShots = 0;
    private Integer gameDuration = 15;
    private boolean isOvertime = false;
    private boolean isShootout = false;

    DatabaseReference mDatabase;
    DatabaseReference games;
    FirebaseDatabase db;


    public class LocalBinder extends Binder {
        DBService getService() {
            return DBService.this;
        }
    }


    public IBinder onBind(Intent intent) {
        /*try {
            mSocket = IO.socket("http://10.0.2.2:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.v("insocket", "emitting  ");
                mSocket.emit("message", "hellloo");

            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.v("insocket", "messaggeee  ");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.v("insocket", "disconnecting  ");
            }

        });

        mSocket.connect();*/
        return mBinder;
    }
    private void collectGames(Map<String,Object> Game) {

        ArrayList<String> guestTeams = new ArrayList<>();
        ArrayList<String> homeTeams = new ArrayList<>();

        for (Map.Entry<String, Object> entry : Game.entrySet()){


            Map game = (Map) entry.getValue();
            //Get phone field and append to list
            guestTeams.add((String) game.get("guestTeam"));
            homeTeams.add((String) game.get("homeTeam"));
        }

        //System.out.println(guestTeams.toString());
        //System.out.println(homeTeams.toString());
    }




    public class getDataFromFirebase extends AsyncTask<String, Integer, String> {

        // these Strings / or String are / is the parameters of the task, that can be handed over via the excecute(params) method of AsyncTask
        protected String doInBackground(String... params) {
            games.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        collectGames((Map<String, Object>) dataSnapshot.getValue());
                    } else {
                        Log.v("Datasnapshot","null");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return null;
        }

        // this is called whenever you call puhlishProgress(Integer), for example when updating a progressbar when downloading stuff
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        // the onPostexecute method receives the return type of doInBackGround()
        protected void onPostExecute(String result) {
            /*Log.v("onPostExecute",result.toString() + "");

            String date = "";
            try {
                JSONObject object = new JSONObject(result);
                JSONArray results  = object.getJSONArray("Sheet1");


                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    Integer id = r.getInt("_id");
                    String dbGuestTeam = r.getString("guestTeam");
                    String dbHomeTeam = r.getString("homeTeam");
                    Integer dbGuestGoals = r.getInt("guestGoals");
                    Integer dbHomeGoals = r.getInt("homeGoals");
                    Integer dbGuestShots = r.getInt("guestShots");
                    Integer dbHomeShots = r.getInt("homeShots");
                    Integer dbGameDuration = r.getInt("gameDuration");
                    boolean dbIsOvertime = r.getBoolean("overtime");
                    boolean dbIsShootout = r.getBoolean("shootout");
                    String dbDate = r.getString("date");
                    Log.v("values", dbDate +dbGuestTeam+dbHomeTeam+dbGameDuration+dbGuestGoals+dbGuestShots+dbHomeGoals+dbHomeShots+dbIsOvertime+dbIsShootout+"dsd");
                    ContentValues values = new ContentValues();
                    values.put(ResultContract.KEY_GUEST_TEAM, dbGuestTeam);
                    values.put(ResultContract.KEY_HOME_TEAM, dbHomeTeam);
                    values.put(ResultContract.KEY_GUEST_GOALS, dbGuestGoals);
                    values.put(ResultContract.KEY_HOME_GOALS, dbHomeGoals);
                    values.put(ResultContract.KEY_GUEST_SHOTS, dbGuestShots);
                    values.put(ResultContract.KEY_HOME_SHOTS, dbHomeShots);
                    values.put(ResultContract.KEY_GAME_DURATION, dbGameDuration);
                    values.put(ResultContract.KEY_OVERTIME, dbIsOvertime);
                    values.put(ResultContract.KEY_SHOOTOUTS, dbIsShootout);
                    values.put(ResultContract.KEY_DATE, dbDate);



                    getContentResolver().insert(
                            ResultProvider.CONTENT_URI,
                            values);
                    values.clear();
                }
                Intent RTReturn = new Intent("bc");
                RTReturn.putExtra("json", "Database downloaded succesfully");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(RTReturn);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // do something with the result, for example display the received Data in a ListView
            // in this case, "result" would contain the "someLong" variable returned by doInBackground();*/
        }
    }


    public class AsyncTAttachPlayerToTheTeamNode extends AsyncTask<Void,Void,Void>{
        private Team team;

        // a constructor so that you can pass the object and use
        AsyncTAttachPlayerToTheTeamNode(Team team){
            this.team = team;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("myTag", team.toString());
            Log.d("myTag", team.player.toString());
            Log.d("myTag", team.player.toString());
            Log.d("myTag", team.player.toString());

            try {
                // localhost url http://10.0.2.2:8080/addplayer
                URL url = new URL("http://nhl97.jelastic.metropolia.fi/addplayer"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("player", team.player.toString());
                postDataParams.put("team", team.team.toString());
                postDataParams.put("teamChar", team.teamChar.toString());


                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return null;

                }
                else {
                    return null;
                }
            }
            catch(Exception e){
                return null;
            }

        }


    }


    public class AsyncTAddGameToTheNode extends AsyncTask<Void,Void,Void>{
        private Game game;

        // a constructor so that you can pass the object and use
        AsyncTAddGameToTheNode(Game game){
            this.game = game;
        }

        protected Void doInBackground(Void... params) {
            Log.d("myTag", game.toString());
            Log.d("myTag", game.guestTeam.toString());

            try {
                // localhost url http://10.0.2.2:8080/addgame
                URL url = new URL("http://nhl97.jelastic.metropolia.fi/addgame"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("guestTeam", game.guestTeam.toString());
                postDataParams.put("guestGoals", game.guestGoals);
                postDataParams.put("homeGoals", game.homeGoals);
                postDataParams.put("homeTeam", game.homeTeam.toString());
                postDataParams.put("guestShots", game.guestShots);
                postDataParams.put("homeShots", game.homeShots);
                postDataParams.put("isOvertime", game.isOvertime.booleanValue());
                postDataParams.put("isShootout", game.isShootout.booleanValue());
                postDataParams.put("date", game.date.toString());

                Log.e("params",postDataParams.toString());

                //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.e("https :) " , "Code: " + responseCode);
                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();

                    return null;

                }
                else {
                    Log.e("https? " , "Code: " + responseCode);
                    return null;
                }
            }
            catch(Exception e){
                Log.e("dead", "worst: " + e);
                return null;
            }

        }
        protected void onProgressUpdate(Void... progress) {
            //setProgressPercent(progress[0]);
        }





    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    public void addGameToTheNode(Game game){

        new AsyncTAddGameToTheNode(game).execute();
    }

    public void attachPlayerToTheTeamNode(Team team){

        new AsyncTAttachPlayerToTheTeamNode(team).execute();
    }
}

