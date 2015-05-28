package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.sql.Statement;


public class MainActivity4 extends Activity {

    TextView business_name_in_queue, current_line_in_queue, totalQueueDisplay, currentQueueDisplay_in_queue, userQueueDisplay;
    Activity activity;
    Context context;
    TotalQueuesBL tqb;
    DBDAL getLineAsync;
    int branchId, userQueue;
    private SharedPreferences.Editor editor;
    SharedPreferences sharedPrefQueue;
    boolean asyncIsRunning;
    String businessNameFromIntent;
    Intent noticeServiceIntent;
    boolean run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity4);
        business_name_in_queue = (TextView) findViewById(R.id.business_name_in_queue);
        userQueueDisplay = (TextView) findViewById(R.id.userQueueDisplay);

        Intent intent = getIntent();
        businessNameFromIntent = intent.getStringExtra("businessNameFromIntent");
        branchId = intent.getIntExtra("branchId",0);
        business_name_in_queue.setText(businessNameFromIntent);
        tqb = new TotalQueuesBL();
        tqb.showQueue(this, this, branchId);

        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        if (userQueue ==0) {
            Log.d("Initial Of The Shared ", "" + userQueue);
            Log.d("Starting Async ", "");
            getLineAsync = new DBDAL(branchId);
            //getLineAsync.execute();
            getLineAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            asyncIsRunning=true;
        }
            else
            {
                Log.d("The Line OnCreate Is", "" + userQueue);
                Log.d("Not Starting Async", "");
                userQueueDisplay.setText(Integer.toString(sharedPrefQueue.getInt("THE_LINE",0)));
                asyncIsRunning=false;

            }
        editor = sharedPrefQueue.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        //getLineAsync.cancel(true);
        if(run)
        {
            stopService(noticeServiceIntent);
        }
        editor.putInt("THE_LINE",0).apply();
        Context context = getApplicationContext();
        CharSequence text = "התור שלך בוטל";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        Intent i=new Intent(this,Entry.class);
        startActivity(i);
        if(asyncIsRunning)
            getLineAsync.cancel(true);
        finish();

    }

    public void buttonListener(View view) { // Notice Button
        if (userQueue !=0) {
            noticeServiceIntent = new Intent(this, NoticeService.class);
            noticeServiceIntent.putExtra("branchId", branchId);
            noticeServiceIntent.putExtra("userQueue", userQueue);
            startService(noticeServiceIntent);
            Log.d("ButtonPressed", "");

            run = true;
        }
    }

    public void setSharedPrefQueue(int lineNum)
    {
        editor.putInt("THE_LINE",lineNum).apply();
        userQueue=sharedPrefQueue.getInt("THE_LINE",0);
        editor.putString("BUSINESS_NAME",businessNameFromIntent).apply();
        editor.putInt("BRANCH_ID",branchId).apply();
        userQueueDisplay.setText(Integer.toString(sharedPrefQueue.getInt("THE_LINE", 0)));
        Log.d("in async",lineNum+"");
    }

//########################################################################################


    public class DBDAL extends AsyncTask<String,Integer,Integer>
    {
        private int branchId;
        TextView userQueueDisplay,currentQueueDisplay_in_queue,totalQueueDisplay;
        String DB_URL =DatabaseConstants.DB_URL;
        String USER = DatabaseConstants.USER;
        String PASS = DatabaseConstants.PASS;

        public DBDAL(int branchId) {
            this.branchId=branchId;
        }

        @Override
        protected Integer doInBackground(String... sqlQ) {
            int response = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                String result = "\nDatabase connection success\n";
                Statement st = con.createStatement();
                Statement st2 = con.createStatement();
                Statement st3 = con.createStatement();
                String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query2 = "SELECT TotalQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query3 ="UPDATE Queue SET TotalQueue = TotalQueue + 1 WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSet rs2 = st2.executeQuery(query2);
                int rs3 = st3.executeUpdate(query3);
                while (rs.next()&& rs2.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue= rs2.getInt("TotalQueue");
                    response = currentQueue;
                    //System.out.println("\nSQLQ{1}"+sqlQ[1]+"\n");
                    publishProgress(currentQueue,totalQueue);

                }

                Log.d(result, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onProgressUpdate(Integer... progress) {
            setSharedPrefQueue(progress[0] + (progress[1] - progress[0]) + 1);
            //currentQueueDisplay_in_queue.setText(Integer.toString(progress[0]));
        }
    }


}
