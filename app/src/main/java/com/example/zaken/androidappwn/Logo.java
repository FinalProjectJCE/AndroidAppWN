package com.example.zaken.androidappwn;

import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;



import static android.os.SystemClock.sleep;


public class Logo extends Activity {

    Handler mHandler;
    Runnable mNextActivityCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        mHandler = new Handler();
        mNextActivityCallback = new Runnable() {
            @Override
            public void run() {
                // Intent to jump to the next activity
                Intent i= new Intent(Logo.this,MainActivity.class);
                startActivity(i);
                finish(); // so the splash activity goes away
            }
        };
        mHandler.postDelayed(mNextActivityCallback, 5000L);
    }

//    public void onRestart(){
//        super.onRestart();
//        sleep(10000);
//        Intent i=new Intent(this,MainActivity.class);
//        startActivity(i);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logo, menu);
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
        if (isFinishing()) {
            mHandler.removeCallbacks(mNextActivityCallback);
        }
    }
}

