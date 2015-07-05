package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
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
 * This Class Is The Business Logic Layer For
 * The Queue Data While The User Is In Queue.
 */
public class QueueInfoInQueueBL
{
    private SharedPreferences sharedPrefQueue;
    QueueInfoInQueueDAL task;
    private Button alertButton,cancelButton;
    private TextView currentQueueDisplay_in_queue,totalQueueDisplay,totalQueueText;
    private Context context;
    private Activity activity;
    private int branchId;


    /**
     * Sets The Text Views In The Final Page.
     */
    public QueueInfoInQueueBL(Activity activity, Context context, int branchId)
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

    // Starts The Task.
    public void showQueue()
    {
        task= new QueueInfoInQueueDAL(this,activity,context,branchId);
        task.execute();
    }

    /**
     * Sets The Data in The Text Views.
     */
    public void setTextViews(Integer...progress)
    {
        currentQueueDisplay_in_queue.setText(Integer.toString(progress[0]));
        int totalClientsInQueue=sharedPrefQueue.getInt("TOTAL_QUEUE",-1);
        int waitingClients;
        if(totalClientsInQueue!=(-1))
        {
            waitingClients=totalClientsInQueue-progress[0]+1; // 1 Is Becouse Updating The Total Queue Is Happend After The User Is Getting The Line
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

    /**
     * If The User Queue Is Passed Show Him A Message.
     */
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

    /**
     * When The User Queue Is Arrived, It Will Disapear On The Page.
     */
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

    /**
     * If There Is A Connection Problem, This Method Show A Dialog To The User.
     */
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
            public void onClick(SweetAlertDialog sDialog) { // Try Again.
                task.cancel(true);
                showQueue();
                sDialog.dismissWithAnimation();
            }
        });
        warningDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) { // Cancel.
                sDialog.dismissWithAnimation();
            }
        });
        warningDialog.show();
    }
}
