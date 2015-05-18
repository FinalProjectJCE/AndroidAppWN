package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


public class MainActivity4 extends Activity {

    TextView business_name_in_queue, current_line_in_queue, totalQueueDisplay, currentQueueDisplay_in_queue, userQueueDisplay;
    Activity activity;
    Context context;
    TotalQueuesBL tqb;
    int branchId;
    public static int userQueueNum ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity4);
        business_name_in_queue = (TextView) findViewById(R.id.business_name_in_queue);
        current_line_in_queue = (TextView) findViewById(R.id.current_line_in_queue);
        totalQueueDisplay = (TextView) findViewById(R.id.totalQueueDisplay);
        currentQueueDisplay_in_queue = (TextView) findViewById(R.id.currentQueueDisplay_in_queue);
        userQueueDisplay = (TextView) findViewById(R.id.userQueueDisplay);

        Intent intent = getIntent();
        String businessNameFromIntent = intent.getStringExtra("businessNameFromIntent");
        branchId = intent.getIntExtra("branchId",0);
        userQueueNum=0;
        business_name_in_queue.setText(businessNameFromIntent);

        setActivity(this);
        tqb = new TotalQueuesBL();
        tqb.showQueue(getActivity(), getContext(), branchId);
        userQueueDisplay.setText(Integer.toString(userQueueNum));
    }

    private Context getContext() {
        return this.context;
    }

    private void setActivity(Activity activity) {
        this.activity = activity;
    }

    private Activity getActivity() {
        return this.activity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity4, menu);
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
    public void onPause() {
        super.onPause();
        tqb.task.cancel(true);
    }

    public void cancelQueueButtonClick(View view) {
        userQueueNum = 0;

        Context context = getApplicationContext();
        CharSequence text = "התור שלך בוטל";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        finish();
    }
}
