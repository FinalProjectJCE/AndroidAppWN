package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zaken on 02/04/2015.
 */
public class QueueBL
{
    public QueueDAL task;
    private TextView currentQueueDisplay;
    private TextView averageTextView;
    private TextView waitingClientsTV;
    private Context context;
    private Activity activity;
    private int branchId;

    public QueueBL(Activity activity,Context context,int branchId)
    {
        this.context=context;
        this.activity=activity;
        this.branchId=branchId;

    }
    public void showQueue()
    {
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currentQueueDisplayTV);
        averageTextView = (TextView) activity.findViewById(R.id.timeTextView);
        waitingClientsTV = (TextView) activity.findViewById(R.id.waitingClientsDisplayTV);
        task= new QueueDAL(this,activity,context,branchId);
        task.execute();
    }

    public void setTextViews(Object...progress)
    {
        Time t = (Time) progress[1];
        int totalClients=(int)progress[4];
        int currentLine=(int)progress[0];
        currentQueueDisplay.setText(progress[0].toString());
        int clients=(int)(progress[2]);
        if(clients!=0)
            clients+=1;

        int waitingClients;
        if(totalClients==0 || totalClients<0)
            waitingClients=0;
        else
            waitingClients= totalClients-currentLine+1;

        waitingClientsTV.setText(""+waitingClients);
        averageTextView.setText(setAverage(t.getHours(),t.getMinutes(),t.getSeconds(),(Integer)progress[2],(Integer)progress[3]));
    }

    public String setAverage(int receivedHours, int receivedMinutes, int receivedSeconds,int queueNum,int numOfClerks) {
        Long secondsRR,newHours,newMinutes,newSeconds;
        String toReturn;
        secondsRR = TimeUnit.HOURS.toSeconds(receivedHours)+
                TimeUnit.MINUTES.toSeconds(receivedMinutes)+
                receivedSeconds;

        secondsRR = (secondsRR*queueNum)/numOfClerks;

        newHours=TimeUnit.SECONDS.toHours(secondsRR);
        secondsRR=secondsRR-(newHours*3600);
        newMinutes=TimeUnit.SECONDS.toMinutes(secondsRR);
        secondsRR=secondsRR-(newMinutes*60);
        newSeconds=secondsRR;

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

    public void connectionProblemAlert()
    {

        SweetAlertDialog warningDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        warningDialog.setTitleText("החיבור לאינטרנט כשל");
        warningDialog.setContentText("כעת לא תוכל לצפות בנתוני התור!");
        warningDialog.setConfirmText("נסה שנית");
        warningDialog.showCancelButton(true);
        warningDialog.setCancelText("בטל");
        warningDialog.setCancelable(false);
        warningDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) { // Try Again
                task.cancel(true);
                showQueue();
                sDialog.dismissWithAnimation();
            }
        });
        warningDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                Intent i = new Intent(context, Entry.class);
                context.startActivity(i);
            }
        });
        warningDialog.show();
    }


}
