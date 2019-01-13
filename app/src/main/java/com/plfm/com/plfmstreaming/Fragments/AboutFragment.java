package com.plfm.com.plfmstreaming.Fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plfm.com.plfmstreaming.R;
import com.plfm.com.plfmstreaming.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutFragment extends Fragment  {

    private TextView txtv_version;
    private TextView txtv_last_update;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_about, container, false);

        txtv_version = view.findViewById(R.id.txtv_version);
        txtv_last_update = view.findViewById(R.id.txtv_last_update);

        String dateinstall = getInstallDate();
        txtv_last_update.setText("last update " + dateinstall);

        String version = "";
        int buildNumber = 0;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
            buildNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtv_version.setText("version " + version);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setOnBundleSelected(new MainActivity.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {

                Log.d("aaaa", "onBundleSelect: "+bundle.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        }


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private String getInstallDate() {
        PackageManager packageManager = getActivity().getPackageManager();
        long installTimeInMilliseconds; // install time is conveniently provided in milliseconds

        Date installDate = null;
        String installDateString = null;

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            installTimeInMilliseconds = packageInfo.lastUpdateTime;

            long val = installTimeInMilliseconds;
            Date date = new Date(val);
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMMM yyyy");
            String dateText = df2.format(date);
            System.out.println(dateText);

            return dateText;

        } catch (PackageManager.NameNotFoundException e) {
            installDate = new Date(0);
            installDateString = installDate.toString();
        }

        return installDateString;
    }
}
