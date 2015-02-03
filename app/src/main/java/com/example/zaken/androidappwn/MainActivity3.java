package com.example.zaken.androidappwn;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        business_name = (TextView) findViewById(R.id.business_name);
        estimate_time = (TextView) findViewById(R.id.estimate_time);
        current_line = (TextView) findViewById(R.id.current_queue);
        currentQueueDisplay = (TextView) findViewById(R.id.currenQueueDisplay);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        business_name.setText(name);
        System.out.print("\nON CREATE!!!!!!!!!!!!! \n");
        String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '111'";

            task = new Async();
            task.execute(query);
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
        task.cancel(true);
    }
    public void onRestart(){
        super.onRestart();
        String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '111'";
        task = new Async();
        task.execute(query);
    }

    public void getQueueButtonClick(View view)
    {
        Intent i=new Intent(this,MainActivity4.class);
        i.putExtra("businessNameFromIntent",business_name.getText());
        startActivity(i);
    }



    //##########################################################################################
    private class Async extends AsyncTask<String,Integer,Integer> {
        //        AsyncTask<Params, Progress, Result>:
//
//        Params: the type of doInBackground()'s and execute() var-args parameters.
//        Progress: the type of publishProgress()'s and onProgressUpdate()'s var-args parameters.
//        Result: the type of doInBackground()'s return value, onPostExecute()'s parameter, onCancelled()'s parameter, and get()'s return value.
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
        String USER = "lewtprebbcrycgkb";
        String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";
        int SI = 0;


        protected Integer doInBackground(String... sqlQ) {
            int response = 0;
            try {
                boolean running = true;
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                String result = "\nDatabase connection success\n";
                Statement st = con.createStatement();
                while(running) {
                    String query = sqlQ[0];
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        int currentQueue = rs.getInt("CurrentQueue");
                        response = currentQueue;
                        publishProgress(currentQueue);
                    }
                    if(isCancelled())
                        running=false;
                }
                Log.d(result, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onProgressUpdate(Integer... progress) {
            currentQueueDisplay.setText(Integer.toString(progress[0]));
        }
    }
}
