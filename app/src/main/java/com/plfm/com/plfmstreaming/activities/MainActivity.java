package com.plfm.com.plfmstreaming.activities;

import android.app.Notification;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.plfm.com.plfmstreaming.Fragments.AboutFragment;
import com.plfm.com.plfmstreaming.Fragments.ChatFragment;
import com.plfm.com.plfmstreaming.Fragments.HomeFragment;
import com.plfm.com.plfmstreaming.Fragments.MaintenanceFragment;
import com.plfm.com.plfmstreaming.R;
import com.plfm.com.plfmstreaming.components.BottomNavigationViewHelper;
import com.plfm.com.plfmstreaming.etc.Const;
import com.plfm.com.plfmstreaming.etc.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.plfm.com.plfmstreaming.etc.Const.PREFS_RELOAD_CHAT;
import static com.plfm.com.plfmstreaming.etc.Const.PREFS_RELOAD_KEYPRESS;

//import co.mobiwise.library.RadioListener;
//import co.mobiwise.library.RadioManager;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String RADIO_URL = "http://103.28.148.18:8810";
//    private static final String SOCKET_URL = "http://plfm.usa.cc";
//    private static final String SOCKET_URL = "http://ec2-3-16-29-123.us-east-2.compute.amazonaws.com:3000";
    private static final String SOCKET_URL = "http://ec2-3-16-29-123.us-east-2.compute.amazonaws.com:3000";
//    private static final String RADIO_URL2 = "http://i.klikhost.net:8810";
//    private String url_radio = "http://103.28.148.18:8810";
    private Fragment fragment;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private MediaPlayer player;

    public List<String> getChat() {
        return chat;
    }

    public void setChat(List<String> chat) {
        this.chat = chat;
    }

    private List<String> chat =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setHasTransientState(false);
        navigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        initializeMediaPlayer();

        loadFragment(new HomeFragment());

        try {
            mSocket = IO.socket(SOCKET_URL);
            mSocket.on(getString(R.string.socket_event_chat), onNewMessage);
            mSocket.connect();

        } catch (URISyntaxException e) {
            final String err= e.getMessage();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void connectingsocket(){
            mSocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username="";
                    String message="";
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        chat.add(username+" : "+message);
                        Intent closecashierdrawer = new Intent();
                        closecashierdrawer.setAction(PREFS_RELOAD_CHAT);
                        MainActivity.this.sendBroadcast(closecashierdrawer);

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error Parse Json catch", Toast.LENGTH_SHORT).show();
                        return;
//
//                        e.printStackTrace();
                    }

                    Toast.makeText(MainActivity.this, "new chat", Toast.LENGTH_SHORT).show();
//                    tv_goal.append(username+"~"+message+"\n");
                }
            });
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(getString(R.string.socket_event_chat), onNewMessage);
    }

    public boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout, fragment)
                    .commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_proses:
                fragment = new MaintenanceFragment();
                break;
            case R.id.navigation_chat:
                fragment = new ChatFragment();
                break;
            case R.id.navigation_about:
                fragment = new AboutFragment();
                break;
        }
        return loadFragment(fragment);
    }

    public void startRadio(){
//starts radio streaming.

        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {

                player.start();
            }
        });

    }

    public void stopRadio(){
//starts radio streaming.
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }
    }

    public void changeTextNotif(String subtitle){
        try{
//            mRadioManager.updateNotification(getString(R.string._101_7_plfm_malang),subtitle,null);
        }catch (NullPointerException e ) {
//            mRadioManager.updateNotification(getString(R.string._101_7_plfm_malang),"Stopped",null);
        }
    }

    public boolean isPlaying(){

        if (player != null ) {
            try{
                return player.isPlaying();
            }catch (NullPointerException e ){
            return false;
            }
        }else {
            return false;
        }
    }


    SelectedBundle selectedBundle;
    public interface SelectedBundle {

        void onBundleSelect(Bundle bundle);
    }
    public void setOnBundleSelected(SelectedBundle selectedBundle) {
        this.selectedBundle = selectedBundle;
    }


    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(RADIO_URL);
        } catch (final IllegalArgumentException e) {
           final String err= e.getMessage();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            });

            e.printStackTrace();
        } catch (IllegalStateException e) {
            final String err= e.getMessage();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        } catch (IOException e) {
            final String err= e.getMessage();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"Pastikan Koneksi Internet anda stabil ,"+ err, Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });
    }

    public void chatSend(String message) {
        if (mSocket.connected()){
//            Toast.makeText(this, "Server Chat : Connected", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Server Chat : Not Connected", Toast.LENGTH_SHORT).show();
            connectingsocket();
        }

        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("username", Util.getSharedPreferenceString(this , Const.PREFS_NAMA, ""));
            studentsObj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(getString(R.string.socket_event_chat), studentsObj.toString());
    }
    
    
}


