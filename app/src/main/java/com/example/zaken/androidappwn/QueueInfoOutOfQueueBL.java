package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zaken on 02/04/2015.
 * This Class Is The Business Logic Layer For
 * The Queue Data While The User Is Not In Queue.
 */
public class QueueInfoOutOfQueueBL
{
    public QueueInfoOutOfQueueDAL task;
    private TextView currentQueueDisplay;
    private TextView averageTextView;
    private TextView waitingClientsTV;
    private Context context;
    private Activity activity;
    private int branchId;

    public QueueInfoOutOfQueueBL(Activity activity, Context context, int branchId)
    {
        this.context=context;
        this.activity=activity;
        this.branchId=branchId;
    }

    /**
     * Sets The Text Views In The Final Page.
     */
    public void showQueue()
    {
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currentQueueDisplayTV);
        averageTextView = (TextView) activity.findViewById(R.id.timeTextView);
        waitingClientsTV = (TextView) activity.findViewById(R.id.waitingClientsDisplayTV);
        task= new QueueInfoOutOfQueueDAL(this,activity,context,branchId);
        task.execute();
    }

    /**
     * Sets The Data in The Text Views For The Display.
     * From The Data That Receive From The Database
     */
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

    /**
     * Sets The Average Time For Until The Queue Is Arrived((Average Time For Queue * Waiting Clients )/The Number Of Clerks)
     */
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

    /**
     * If There Is A Connection Problem, This Method Show A Dialog To The User.
     */
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
