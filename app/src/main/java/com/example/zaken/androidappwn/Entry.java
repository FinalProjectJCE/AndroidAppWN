package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Entry extends Activity {
    SharedPreferences sharedPrefQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        int userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        int branchId = sharedPrefQueue.getInt("BRANCH_ID",0);
        String busName = sharedPrefQueue.getString("BUSINESS_NAME","noName");
        Log.d("On Entry, User Queue Is",""+userQueue);
        Log.d("On Entry, Branch Id Is",""+branchId);
        Log.d("On Entry, Busi Name Is",""+busName);
        if(userQueue!=0)
        {
            Intent i=new Intent(this,MainActivity4.class);
            i.putExtra("businessNameFromIntent",busName);
            i.putExtra("branchId", branchId);
            startActivity(i);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void choose_business(View view){
        Intent i = new Intent(this,ChooseBusinessMainActivity.class);
        startActivity(i);
    }


    public void scan(View view){
        Log.e("Lior","Scan");
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                if(isInteger(result.getContents()))
                {
                     Intent i=new Intent(this,MainActivity4.class);
                     i.putExtra("businessNameFromIntent","Need To Fix Name From QR");
                     i.putExtra("branchId",  Integer.parseInt(result.getContents()));
                     startActivity(i);
                }
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
