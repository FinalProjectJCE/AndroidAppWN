package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This Is The Main Class For The "Entry" Page.
 * In This Page, The User Needs To Choose From Two Options:
 * 1. Choose The Business That He Want To Take Queue Or See Information About.
 * 2. Scan The QR-Code From The Service Screen
 */
public class Entry extends Activity {
    SharedPreferences sharedPrefQueue;
    private BusinessesDAL database = new BusinessesDAL(this);

    /**
     * On The Creation Of The Page, There Is A Check To See If The User Is InQueue
     * If He Does, He Will Redirect To The Final Queue Page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        int userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        int branchId = sharedPrefQueue.getInt("BRANCH_ID",0);
        String busName = sharedPrefQueue.getString("BUSINESS_NAME","noName");

        if(userQueue!=0)
        {
            Intent i=new Intent(this,FinalPage.class);
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

    /**
     * This Method Is Invoked When The User Press The Choose Business Button.
     * It Redirect The User To Choose A Business From The List.
     */
    public void choose_business(View view){
        Intent i = new Intent(this,ChooseBusinessMainActivity.class);
        startActivity(i);
    }

    /**
     * This Method Is Invoked When The User Press The Scan Qr-Code Button.
     * It Starts The Scan Init. The Init Opens The Camera And Wait For The QR-Code To Be Scanned.
     */
    public void scan(View view){
        new IntentIntegrator(this).initiateScan();
    }

    /**
     * This Method Is Waiting To The Result From The QR-Code.
     * The Method Checks If The Result Is Valid, And Passes The Business Unique ID
     * To The Final Page To Calculate A Queue.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "הסריקה בוטלה", Toast.LENGTH_LONG).show();
            } else {

                if(isInteger(result.getContents()))
                {
                     String businessNameFromDB;
                     int branchIdFromScan = Integer.parseInt(result.getContents());
                     Intent i=new Intent(this,FinalPage.class);
                     businessNameFromDB=database.getBusinessName(branchIdFromScan);
                     i.putExtra("branchId",  branchIdFromScan);
                     i.putExtra("businessNameFromIntent",businessNameFromDB);
                     startActivity(i);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * This Method Checks If The Result From The Scan Is An Integer.
     */
    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
