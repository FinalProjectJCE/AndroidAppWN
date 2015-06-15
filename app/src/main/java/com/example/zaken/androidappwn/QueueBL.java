package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zaken on 02/04/2015.
 */
public class QueueBL
{
    public QueueDAL task;
    private TextView currentQueueDisplay;
    private TextView averageTextView;
    private TextView waitingClients;
    public void showQueue(Activity activity,Context context,int branchId)
    {
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currentQueueDisplayTV);
        averageTextView = (TextView) activity.findViewById(R.id.timeTextView);
        waitingClients = (TextView) activity.findViewById(R.id.waitingClientsDisplayTV);
        task= new QueueDAL(this,activity,context,branchId);
        task.execute();
    }

    public void setTextViews(Object...progress)
    {
        Time t = (Time) progress[1];
        currentQueueDisplay.setText(progress[0].toString()+" From BL");
        int clients=(int)(progress[2]);
        if(clients!=0)
            clients+=1;

        waitingClients.setText(""+clients);
        averageTextView.setText(setAverage(t.getHours(),t.getMinutes(),t.getSeconds(),(Integer)progress[2],(Integer)progress[3]));


    }

    public String setAverage(int receivedHours, int receivedMinutes, int receivedSeconds,int queueNum,int numOfClerks) {
        Long subtract,secondsRR,hours,newHours,newMinutes,newSeconds;
        String toReturn;
//        Log.d("receivedHours"," "+receivedHours);
//        Log.d("receivedMinutes"," "+receivedMinutes);
//        Log.d("receivedSeconds"," "+receivedSeconds);

        secondsRR = TimeUnit.HOURS.toSeconds(receivedHours)+
                TimeUnit.MINUTES.toSeconds(receivedMinutes)+
                receivedSeconds;
//        Log.d("(seconds*queueNum)/Cler", "(" + secondsRR+"*"+queueNum+")/"+numOfClerks);

        secondsRR = (secondsRR*queueNum)/numOfClerks;

        newHours=TimeUnit.SECONDS.toHours(secondsRR);
        secondsRR=secondsRR-(newHours*3600);
        newMinutes=TimeUnit.SECONDS.toMinutes(secondsRR);
        secondsRR=secondsRR-(newMinutes*60);
        newSeconds=secondsRR;
//        System.out.println(""+receivedHours+":"+receivedMinutes+":"+receivedSeconds);
//        System.out.println(""+queueNum);
//        System.out.println(""+newHours+":"+newMinutes+":"+newSeconds);
        if(newHours<10)
            toReturn="0"+newHours;
        else
            toReturn=""+newHours;
        if(newMinutes<10)
            toReturn+=":0"+newMinutes;
        else
            toReturn+=":"+newMinutes;
        if(newSeconds<10)
            toReturn+=":0"+newSeconds;
        else
            toReturn+=":"+newSeconds;

        return toReturn;
    }


}
