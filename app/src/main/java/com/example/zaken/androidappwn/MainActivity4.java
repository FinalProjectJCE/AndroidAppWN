package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


public class MainActivity4 extends Activity {

    TextView business_name_in_queue,current_line_in_queue,currentQueueDisplay_in_queue;
    SendQueryAsync task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity4);
        business_name_in_queue=(TextView) findViewById(R.id.business_name_in_queue);
        current_line_in_queue = (TextView) findViewById(R.id.current_line_in_queue);
        currentQueueDisplay_in_queue = (TextView) findViewById(R.id.currentQueueDisplay_in_queue);
        Intent intent = getIntent();
        String businessNameFromIntent = intent.getStringExtra("businessNameFromIntent");
        business_name_in_queue.setText(businessNameFromIntent);

        String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '111'";
        task = new SendQueryAsync();
        task.execute(query);
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
    public void onPause(){
        super.onPause();
        task.cancel(true);
    }

//#@#@#@#@#@#@#@#@#@#@@@@@@@@@@@@@@@@@#####################@@@@@@@@@@@@@@@@@@###############
    private class SendQueryAsync extends AsyncTask<String,Integer,Integer> {
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
                System.out.println("\nHere1\n");

                String result = "\nDatabase connection success\n";
                Statement st = con.createStatement();
                System.out.println("\nsqlQ[1] : " + sqlQ[0] + "\n");
                while(running) {
                    String query = sqlQ[0];
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    System.out.println("\nsqlQ[1] : " + sqlQ[0] + "\n");

                    while (rs.next()) {
                        System.out.println("\nHere4\n");
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

        //        public void Connect() {
//            Async task = new Async();
//            task.execute();
//            System.out.println("\nHere6\n");
//        }
        protected void onProgressUpdate(Integer... progress) {
            //finalCurrentQueue=progress[0];
            currentQueueDisplay_in_queue.setText(Integer.toString(progress[0]));
            System.out.println("\nSET TEXT\n");
        }
    }
}