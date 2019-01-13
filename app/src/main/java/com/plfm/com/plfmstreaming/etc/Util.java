package com.plfm.com.plfmstreaming.etc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


import static android.content.Context.ALARM_SERVICE;

public class Util {

    public static final String PREFS_NAME = "_ReminderIbadahPref";
    private static final String TAG = "Util";
    private static final int DAILY_REMINDER_REQUEST_CODE = 4000;

    public static String getSharedPreferenceString(Context c, String preference, String defaultValue) {
        try {
            SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
            return settings.getString(preference, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static final String getCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static int getDimenResources(Context context, int resource){
        return (int) (context.getResources().getDimension(resource) / context.getResources().getDisplayMetrics().density);
    }

    public static int getSharedPreferenceInteger(Context c, String preference, int defaultValue) {
        try {
            SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
            return settings.getInt(preference, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public static final long dateToMillis(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return -1;
    }

    public static final Date dateToMillisDATE(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }


    public static final int toDIP(Context pContext, float p_value) {
        return (int) ((p_value * pContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static Date getNearestDate(List<Date> dates, Date currentDate) {
        long minDiff = -1, currentTime = currentDate.getTime();
        Date minDate = null;
        for (Date date : dates) {
            long diff = Math.abs(currentTime - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = date;
            }
        }
        return minDate;
    }


    private static boolean isCloserToNextDate(Date originalDate, Date previousDate, Date nextDate) {
        if(previousDate.after(nextDate))
            throw new IllegalArgumentException("previousDate > nextDate");
        return ((nextDate.getTime() - previousDate.getTime()) / 2 + previousDate.getTime() <= originalDate.getTime());
    }


    public static boolean getSharedPreferenceBoolean(Context c, String preference, boolean defaultValue) {
        try {
            SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
            return settings.getBoolean(preference, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void setSharedPreference(Context c, String preference, String prefValue) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(preference, prefValue);
        editor.commit();
    }

    public static void setSharedPreference(Context c, String preference, boolean prefValue) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(preference, prefValue);
        editor.commit();
    }

    public static void removeSharedPreference(Context c, String preference) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(preference);
        editor.commit();
    }

    public static void clearSharedPreference(Context c) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public static void setReminder(Context context, Class<?> cls, int hour, int min)
    {
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);
        // cancel already scheduled reminders
        cancelReminder(context,cls);

        if(setcalendar.before(calendar)){
            setcalendar.add(Calendar.DATE,1);
        }

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                DAILY_REMINDER_REQUEST_CODE, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.d(TAG, "setReminder: "+setcalendar);
    }

       public static Double parseDouble(String value) {

        if (value == null) {
            value = "0";
        } else if (value.isEmpty()) {
            value = "0";
        } else if (value.contains(",")){
            value = value.replace(",", ".");
        } else if (Double.parseDouble(value) < 0) {
            value = "0";
        } else if (value.equalsIgnoreCase("nan")) {
            value = "0";
        }

        return Double.parseDouble(value);
    }

    public static float parseFloat(String value) {

        if (value == null) {
            value = "0";
        } else if (value.isEmpty()) {
            value = "0";
        } else if (value.contains(",")){
            value = value.replace(",", ".");
        } else if (Double.parseDouble(value) < 0) {
            value = "0";
        } else if (value.equalsIgnoreCase("nan")) {
            value = "0";
        }

        float f = Float.parseFloat(value);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float twoDigitsF = Float.valueOf(decimalFormat.format(f).replace(",","")); // output is

        return twoDigitsF;
    }


    public static Integer parseInteger(String value) {
        try {
            if (value == null) {
                value = "0";
            } else if (value.isEmpty()) {
                value = "0";
            } else if (Double.parseDouble(value) < 0) {
                value = "0";
            } else if (value.equalsIgnoreCase("nan")) {
                value = "0";
            }
            float tmp = Float.parseFloat(value);
            return Math.round(tmp);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public static Long parseLong(String value) {
        if (value == null) {
            value = "0";
        } else if (value.isEmpty()) {
            value = "0";
        } else if (Double.parseDouble(value) < 0) {
            value = "0";
        } else if (value.equalsIgnoreCase("nan")) {
            value = "0";
        }

        return Long.parseLong(value);
    }


    public static String getTimeAgo2(Date past){

        try
        {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
//            Date past = format.parse("2016.02.05 AD at 23:59:30");
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
//

            Log.d(TAG, "getTimeAgo2: ");
          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

            if(seconds<60)
            {
                System.out.println(seconds+" seconds ago");
                return seconds+" seconds ago";
            }
            else if(minutes<60)
            {
                System.out.println(minutes+" minutes ago");
                return minutes+" minutes ago";
            }
            else if(hours<24)
            {
                System.out.println(hours+" hours ago");

                return hours+" hours ago";
            }
            else
            {
                System.out.println(days+" days ago");

                return days+" days ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }


        return "";
    }


    public static String getTimeAgo(long pastMillis, long currentMillis) {
        long duration = currentMillis - pastMillis;
        long ONE_SECOND = 1000;
        long ONE_MINUTE = ONE_SECOND * 60;
        long ONE_HOUR = ONE_MINUTE * 60;
        long ONE_DAY = ONE_HOUR * 24;

        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                return temp + " hari yang lalu";
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                return temp + " jam yang lalu";
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                return temp + " menit yang lalu";
            } else {
                return "beberapa detik yang lalu";
            }
        }

        return "beberapa detik yang lalu";
    }


    public static void cancelReminder(Context context, Class<?> cls)
    {
        // Disable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }




    public static String getFromParamOutbox(String param, String key){
        Uri uri = Uri.parse("http://www.aaa.com?"+(param).replace("#",""));
        String value =  uri.getQueryParameter(key);
        return value;
    }





}
