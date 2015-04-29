package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.TextView;

import com.mysql.jdbc.TimeUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

/**
 * Created by Zaken on 02/04/2015.
 */
public class QueueDAL extends AsyncTask<String,Object,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    TextView currentQueueDisplay;
    TextView averageTextView;
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";

    public QueueDAL(Activity activity,Context context,int branchId) {
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currenQueueDisplay);
        averageTextView = (TextView) activity.findViewById(R.id.timeTextView);
    }

    @Override
    protected Integer doInBackground(String... params) {
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
                String query2 = "SELECT AverageTime FROM Queue WHERE BusinessId = '" + branchId + "'";

                ResultSet rs = st.executeQuery(query);
                ResultSet rs2 = st2.executeQuery(query2);
                while (rs.next()&& rs2.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    Time t=rs2.getTime("AverageTime");
                    //System.out.println("The Time From DB Id : "+ t );
                    response = currentQueue;
                    publishProgress(currentQueue,t);

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

    protected void onProgressUpdate(Object...progress) {
        System.out.println("The Progress Id : "+ progress[0] );
        System.out.println("The Time OnProgress Id : " + progress[1] );
        Time t = (Time) progress[1];

        currentQueueDisplay.setText(progress[0].toString());
        averageTextView.setText(setAverage(t.getHours(),t.getMinutes(),t.getSeconds(),(Integer)progress[0]));
    }

    private String setAverage(int receivedHours, int receivedMinutes, int receivedSeconds,int queueNum) {
        Long subtract,secondsRR,hours,newHours,newMinutes,newSeconds;

        secondsRR = TimeUnit.HOURS.toSeconds(receivedHours)+
                TimeUnit.MINUTES.toSeconds(receivedMinutes)+
                receivedSeconds;
        secondsRR = secondsRR*queueNum;
        newHours=TimeUnit.SECONDS.toHours(secondsRR);
        secondsRR=secondsRR-(newHours*3600);
        newMinutes=TimeUnit.SECONDS.toMinutes(secondsRR);
        secondsRR=secondsRR-(newMinutes*60);
        newSeconds=secondsRR;
        System.out.println(""+receivedHours+":"+receivedMinutes+":"+receivedSeconds);
        System.out.println(""+queueNum);
        System.out.println(""+newHours+":"+newMinutes+":"+newSeconds);
        return newHours+":"+newMinutes+":"+newSeconds;
    }
}

