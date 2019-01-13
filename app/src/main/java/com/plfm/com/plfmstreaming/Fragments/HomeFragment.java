package com.plfm.com.plfmstreaming.Fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.plfm.com.plfmstreaming.R;
import com.plfm.com.plfmstreaming.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_time;
    private ProgressBar playSeekBar;
    private ImageButton imb_play;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_recyclerview, container, false);
        tv_time=view.findViewById(R.id.tv_time);
        imb_play=view.findViewById(R.id.imb_play);
        playSeekBar = view.findViewById(R.id.pg_load);



        if (isPlaying()){
            changeButtonStop();
            imb_play.setTag(new Boolean(true));

        }else {
            imb_play.setTag(new Boolean(false));
            changeButtonPlay();

        }






        imb_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!((MainActivity)getActivity()).isPlaying()){
                    startRadio();
                    imb_play.setTag(new Boolean(true));
                }else {
                    stopRadio();
                    imb_play.setTag(new Boolean(false));
                }



            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setOnBundleSelected(new MainActivity.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {

                final String error = bundle.getString("error");
                if ( error!=null && !error.isEmpty()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Log.d("aaaa", "onBundleSelect: "+bundle.toString());
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        updateTime();

    }



    private void startRadio(){
        MainActivity main = (MainActivity) getActivity();
        main.startRadio();

        changeButtonStop();
    }

    private void stopRadio(){
        MainActivity main = (MainActivity) getActivity();
        main.stopRadio();

        changeButtonPlay();
    }


    public void setMeta(){


    }


    private void changeNotif(String notif){
        MainActivity main = (MainActivity) getActivity();
        main.changeTextNotif(notif);


    }

    private boolean isPlaying(){
        MainActivity main = (MainActivity) getActivity();
       return main.isPlaying();
    }

    private void changeButtonPlay(){
        Drawable tempImage = getResources().getDrawable(R.drawable.ic_play);
        imb_play.setImageDrawable(tempImage);
        playSeekBar.setIndeterminate(false);
        changeNotif("Stopped");
    }

    private void changeButtonStop(){
        Drawable tempImage = getResources().getDrawable(R.drawable.ic_stop_blue_grey_800_24dp);
        imb_play.setImageDrawable(tempImage);
        playSeekBar.setIndeterminate(true);
        changeNotif("Playing");
    }


    public void updateTime() {
        final SimpleDateFormat time = new SimpleDateFormat("hh:mm");
//        final TextView txt_clockText = findViewById(R.id.txt_clock_time);
        time.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        tv_time.setText(time.format(new Date()));

//        final SimpleDateFormat date = new SimpleDateFormat("EEEE, MMM dd");
//        final TextView txt_clock_date = findViewById(R.id.txt_clock_date);
//        date.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        txt_clock_date.setText(date.format(new Date()));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_time.setText(time.format(new Date()));
//                txt_clock_date.setText(date.format(new Date()));
                updateTime();
            }
        }, 30000);
    }

    public void showNotif(String s){

    }

}
