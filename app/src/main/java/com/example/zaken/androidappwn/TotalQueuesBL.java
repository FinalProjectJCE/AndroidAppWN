package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zaken on 02/04/2015.
 */
public class TotalQueuesBL
{
    private SharedPreferences sharedPrefQueue;
    TotalQueuesDAL task;
    private Button alertButton,cancelButton;
    private TextView currentQueueDisplay_in_queue,totalQueueDisplay,totalQueueText;
    private Context context;
    private Activity activity;
    private int branchId;



    public TotalQueuesBL(Activity activity,Context context,int branchId)
    {
        currentQueueDisplay_in_queue=(TextView) activity.findViewById(R.id.currentQueueDisplay_in_queue);
        totalQueueText=(TextView) activity.findViewById(R.id.totalQueueText);
        totalQueueDisplay = (TextView) activity.findViewById(R.id.totalQueueDisplay);
        sharedPrefQueue= context.getSharedPreferences("MyPrefsFile",context.MODE_PRIVATE);
        alertButton=(Button)activity.findViewById(R.id.getNoticeButton);
        cancelButton=(Button)activity.findViewById(R.id.cancelQueueButton);
        this.context=context;
        this.activity=activity;
        this.branchId=branchId;
    }

    public void showQueue()
    {
        task= new TotalQueuesDAL(this,activity,context,branchId);
        task.execute();
    }
    public void setTextViews(Integer...progress)
    {
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

    public void connectionProblemAlert()
    {

        SweetAlertDialog warningDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        warningDialog.setTitleText("החיבור לאינטרנט כשל");
        warningDialog.setContentText("תורך נשמר אך כעת לא תוכל לצפות בנתוני התור!");
        warningDialog.setConfirmText("נסה שנית");
        warningDialog.showCancelButton(true);
        warningDialog.setCancelText("בטל");
        warningDialog.setCancelable(false);
        warningDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) { // Try Again
                task.cancel(true);
                showQueue();
                Log.d("TRY AGAIN CLICKED", "TRY AGAIN CLICKED");
                sDialog.dismissWithAnimation();
            }
        });
        warningDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        warningDialog.show();
    }
}
