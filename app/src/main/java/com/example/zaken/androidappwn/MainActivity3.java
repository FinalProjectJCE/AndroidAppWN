package com.example.zaken.androidappwn;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import android.app.Activity;
import android.os.AsyncTask;


public class MainActivity3 extends Activity {

    TextView business_name, estimate_time, current_line,currentQueueDisplay;
    Async task;
    QueueBL qbl;
    Activity activity;
    Context context;
    int branchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        business_name = (TextView) findViewById(R.id.business_name);
        estimate_time = (TextView) findViewById(R.id.estimate_time);
        current_line = (TextView) findViewById(R.id.current_queue);
        //currentQueueDisplay = (TextView) findViewById(R.id.currenQueueDisplay);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        branchId = intent.getIntExtra("branchId",0);
        business_name.setText(name);
        System.out.print("\nON CREATE!!!!!!!!!!!!! \n");
        setActivity(this);
        qbl=new QueueBL();
        qbl.showQueue(getActivity(),getContext(),branchId);
    }
    private Context getContext()
    {
        return this.context;
    }
    private void setActivity(Activity activity)
    {
        this.activity=activity;
    }
    private Activity getActivity()
    {
        return this.activity;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
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
    public void onPause(){
        super.onPause();
        qbl.task.cancel(true);
//        task.cancel(true);
    }
    public void onRestart(){
        super.onRestart();
        qbl.showQueue(getActivity(),getContext(),branchId);
    }

    public void getQueueButtonClick(View view)
    {
        //UPDATE Queue SET CurrentQueue = CurrentQueue + 1 WHERE BusinessId = '111'"
        Intent i=new Intent(this,MainActivity4.class);
        i.putExtra("businessNameFromIntent",business_name.getText());
        startActivity(i);
    }

}
