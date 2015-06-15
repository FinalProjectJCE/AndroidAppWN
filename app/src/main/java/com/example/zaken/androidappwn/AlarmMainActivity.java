package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AlarmMainActivity extends Activity {
    private Window wind;
    private MediaPlayer alarmSound;
    private SweetAlertDialog noticeAlertType;
    private String ContentTextForAlert;
    private int userQueue;
    private SharedPreferences sharedPrefQueue;
    private SharedPreferences.Editor editor;
    private final Context context=this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);
        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        editor = sharedPrefQueue.edit();

        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        alarmSound = MediaPlayer.create(this,R.raw.alarm);
        alarmSound.start();
        //setAlertMessage();
        noticeAlertType=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        noticeAlertType.setTitleText("התראה לפי בקשתך!");
        noticeAlertType.setContentText(setAlertMessage());
        noticeAlertType.setConfirmText("אישור");
        noticeAlertType.setCancelable(false);
        noticeAlertType.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                Intent i=new Intent(context,MainActivity4.class);
                int branchId = sharedPrefQueue.getInt("BRANCH_ID",0);
                i.putExtra("branchId", branchId);

                editor.putBoolean("ALERT_ON",false).apply();
                Log.d("ALERT On Is FALSE","From ALARM");
                sDialog.cancel();
                startActivity(i);
            }
        });
        noticeAlertType.show();

//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private String setAlertMessage()
    {
        String toReturn;
        userQueue=sharedPrefQueue.getInt("THE_LINE",0);
        toReturn = "לקוח מספר ";
        toReturn+=userQueue +", ";
        int serviceType =sharedPrefQueue.getInt("SERVICE_TYPE",0);
        if (serviceType==GeneralConstans.SERVICE_TYPE_CLIENTS)
        {
            int numOfClientsBeforeUser=sharedPrefQueue.getInt("USER_CLIENT_CHOICE",0);
            toReturn+="ישנם כ-";
            toReturn+=numOfClientsBeforeUser;
            toReturn+=" לקוחות הממתינים לפניך!";
        }
        else if(serviceType==GeneralConstans.SERVICE_TYPE_TIME)
        {
            long userTimeInMillis=sharedPrefQueue.getLong("USER_TIME_CHOICE", 0);
            long minFromMillis= TimeUnit.MILLISECONDS.toMinutes(userTimeInMillis);
            toReturn+="בעוד ";
            toReturn+=minFromMillis;
            toReturn+=" דקות התור שלך יגיע!";
        }
        return toReturn;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //alarmSound.release();
    }
}
