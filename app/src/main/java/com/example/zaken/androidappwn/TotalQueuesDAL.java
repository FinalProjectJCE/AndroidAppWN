package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Created by Zaken on 02/04/2015.
 */
public class TotalQueuesDAL extends AsyncTask<String,Integer,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    private SharedPreferences sharedPrefQueue;
    private Button alertButton,cancelButton;

    TextView currentQueueDisplay_in_queue,totalQueueDisplay,totalQueueText;
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";

    public TotalQueuesDAL(Activity activity,Context context,int branchId) {
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        currentQueueDisplay_in_queue=(TextView) activity.findViewById(R.id.currentQueueDisplay_in_queue);
        totalQueueText=(TextView) activity.findViewById(R.id.totalQueueText);
        totalQueueDisplay = (TextView) activity.findViewById(R.id.totalQueueDisplay);
        sharedPrefQueue= context.getSharedPreferences("MyPrefsFile",context.MODE_PRIVATE);
        alertButton=(Button)activity.findViewById(R.id.getNoticeButton);
        cancelButton=(Button)activity.findViewById(R.id.cancelQueueButton);

    }

    @Override
    protected Integer doInBackground(String... sqlQ) {
        int response = 0;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            while(running) {
                String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query2 = "SELECT TotalQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSet rs2 = st2.executeQuery(query2);
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()&& rs2.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue= rs2.getInt("TotalQueue");
                    response = currentQueue;
                    //System.out.println("\nSQLQ{1}"+sqlQ[1]+"\n");
                    publishProgress(currentQueue,totalQueue);

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
        currentQueueDisplay_in_queue.setText(Integer.toString(progress[0]));

        int tq=sharedPrefQueue.getInt("TOTAL_QUEUE",-1);
        int waitingClients;
        if(tq!=(-1))
        {
            waitingClients=tq-progress[0]+1; // 1 Is Becozwe Updating The Total Queue Is Happed After The User Is Getting The Line
            if(waitingClients>0)
                totalQueueDisplay.setText(Integer.toString(waitingClients));
            else if(waitingClients==0) {
                setQueueArrivedTV();

            }
            else if(waitingClients<0) {
                setPassedQueue();

            }

        }
        //int waitingClients = progress[1]-progress[0]-1; // Minus The User On The Waiting Clients
    }

    private void setPassedQueue()
    {
        totalQueueDisplay.setVisibility(View.INVISIBLE);

        totalQueueText.setText("התור שלך עבר");
        totalQueueText.setTextColor(Color.parseColor("#000099"));
        totalQueueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        totalQueueText.setGravity(Gravity.CENTER);
        alertButton.setClickable(false);
        cancelButton.setText("יציאה");
    }

    private void setQueueArrivedTV()
    {
        totalQueueDisplay.setVisibility(View.INVISIBLE);

        totalQueueText.setText("התור שלך הגיע");
        totalQueueText.setTextColor(Color.parseColor("#000099"));
        totalQueueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        totalQueueText.setGravity(Gravity.CENTER);
        alertButton.setClickable(false);
        cancelButton.setText("יציאה");
    }
}
