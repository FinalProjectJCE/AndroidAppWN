package com.example.zaken.androidappwn;
/**
 * This Is The Main Class For The Logo Page.
 * It Displays The Logo Off The Application For 3 Seconds.
 * The Insertion To The Database Starts Here.
 */
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;


public class Logo extends Activity {

    Handler mHandler;
    Runnable mNextActivityCallback;
    InsertToLocalDBBL abl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        abl=new InsertToLocalDBBL();
        abl.getCities(this,this);
        mHandler = new Handler();
        mNextActivityCallback = new Runnable() {
            @Override
            public void run() {

                Intent i= new Intent(Logo.this,Entry.class);
                startActivity(i);
                finish();
            }
        };
        mHandler.postDelayed(mNextActivityCallback, 3000L);
    }

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

