package com.example.zaken.androidappwn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * This Is The Main Class For The Final Page To Get A Queue.
 * In This Page, There Is A Calculation Of A Queue To The User.
 * The User Can See The Next Queue Number, How Many Clients Are Waiting Before Him
 * And The Average Time Until His Queue His Arrived.
 * There Are Two Buttons:
 * 1. Get An Alert.
 * 2. Exit The Queue.
 */

public class FinalPage extends Activity {

    TextView business_name_in_queue, current_line_in_queue, totalQueueDisplay, currentQueueDisplay_in_queue, userQueueDisplay;
    private TextView alertTypeTV;
    Activity activity;
    Context context;
    QueueInfoInQueueBL queueInfo;
    LineNumberCalculation getLineAsync;
    int branchId, userQueue;
    private SharedPreferences.Editor editor;
    SharedPreferences sharedPrefQueue;
    private boolean asyncIsRunning;
    String businessNameFromIntent;
    Intent clientAlertServiceIntent,timeAlertServiceIntent;
    boolean run;
    private Button alertButton;
    private boolean alertOn;
    private TextView timeCounterTV;
    private SweetAlertDialog chooseAlertType;
    private SweetAlertDialog cancelQueueAlert;
    private SweetAlertDialog cancelAlert;
    private CounterClass timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_activity4);
        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        business_name_in_queue = (TextView) findViewById(R.id.business_name_in_queue);
        userQueueDisplay = (TextView) findViewById(R.id.userQueueDisplay);
        alertButton=(Button)findViewById(R.id.getNoticeButton);
        timeCounterTV =(TextView)findViewById(R.id.timeCounterTV);
        Intent intent = getIntent();
        businessNameFromIntent = intent.getStringExtra("businessNameFromIntent");
        branchId = intent.getIntExtra("branchId",0);
        business_name_in_queue.setText(businessNameFromIntent);
        queueInfo = new QueueInfoInQueueBL(this,this,branchId);
        queueInfo.showQueue();// Starts The BLL.
        userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        alertOn=sharedPrefQueue.getBoolean("ALERT_ON",false);
        totalQueueDisplay = (TextView)findViewById(R.id.totalQueueDisplay);
        alertTypeTV=(TextView)findViewById(R.id.alertTypeTV);

        if (userQueue ==0) { // If The User Has No Queue Number.
            getLineAsync = new LineNumberCalculation(branchId);
            getLineAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            asyncIsRunning=true;
        }
            else // If The User Is Already Has A Line, Dont Calculate A Line To Him.
            {
                String busNameFromShared = sharedPrefQueue.getString("BUSINESS_NAME","");
                business_name_in_queue.setText(busNameFromShared);

                setNewCountDown();
                userQueueDisplay.setText(Integer.toString(sharedPrefQueue.getInt("THE_LINE",0)));
                asyncIsRunning=false;
                boolean alertIsON=sharedPrefQueue.getBoolean("ALERT_ON",false);

                if(alertIsON) {
                    alertButton.setText("בטל התראה");
                    alertButton.setBackground(getResources().getDrawable(R.drawable.red_buttons));
                    setAlertTV();
                }
            }
        editor = sharedPrefQueue.edit();
    }

    // Set A Text View When Alert Is Activated.
    private void setAlertTV()
    {
        int serviceType =sharedPrefQueue.getInt("SERVICE_TYPE",0);
        String toDisplay="התראה לפי ";
        if (serviceType==GeneralConstans.SERVICE_TYPE_TIME)
        {
            toDisplay+="זמן ממוצע מופעלת!";
        }
        else if(serviceType==GeneralConstans.SERVICE_TYPE_CLIENTS)
        {
            toDisplay+="מספר לקוחות מופעלת!";
        }
        alertTypeTV.setText(toDisplay);
        alertTypeTV.setVisibility(View.VISIBLE);
    }

    // Set New Countdown When The App Is Re Open After Destroyed, According To The
    // Time When Destroids Until Time It Recreated.
    private void setNewCountDown()
    {
        Long remainingTime=  ( sharedPrefQueue.getLong("CURRENT_TIME_WHEN_DESTROY_APP",0) +sharedPrefQueue.getLong("COUNTER_TIME_WHEN_DESTROY_APP",0) )-System.currentTimeMillis();
        if(remainingTime<0)
        {
            timeCounterTV.setText("00:00:00");
        }
        else {
            timer = new CounterClass(remainingTime, 1000,this);
            timer.start();
        }
    }

    private Context getContext() {
        return this;
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

    /**
     * When The Activity Is Stops, First, Kill The Tread.
     * Save The Current Counter Time In The Shradprefrences
     * Save The Current Time That The Activity Has Stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        queueInfo.task.cancel(true);

        if(timer!=null)
        {
            Long millis = parseTimeFromTV(timeCounterTV.getText().toString());
            editor.putLong("COUNTER_TIME_WHEN_DESTROY_APP", millis).apply();
            editor.putLong("CURRENT_TIME_WHEN_DESTROY_APP", System.currentTimeMillis()).apply();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        queueInfo.task.cancel(true);
    }

    // On Restart, Restart The Info Task.
    public void onRestart(){
        super.onRestart();
        queueInfo = new QueueInfoInQueueBL(this,this,branchId);
        queueInfo.showQueue();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    // If The User Press The Back Button, Dont Do Nothing.
    @Override
    public void onBackPressed() {
    }

    /**
     * This Method Is Invoked When The User Press The Get Alert Button.
     * If The User Already Have An Alert, Pressing This Button Will Display A
     * Dialog And Cancel The Alert.
     */
    public void buttonListener(View view) {
        boolean alertIsON=sharedPrefQueue.getBoolean("ALERT_ON",false);
        if ( (userQueue !=0) && (!alertIsON) ) { // If Alert Is OFF, Choose An Alert.
            setChooseAlertDialog();
            run = true; // Alert Button Is Pressed And No Service Is Running
        }
        else if (alertIsON) // If The User Already Have An Alert Display A
        // Dialog, Cancel The Alert And Change The Text And Color Of The Button.
        {
            cancelAlert=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            cancelAlert.setTitleText("ביטול התראה");
            cancelAlert.setContentText("האם הנך בטוח שברצונך לבטל את ההתראה?");
            cancelAlert.setCancelText("השאר התראה");
            cancelAlert.setConfirmText("בטל התראה");
            cancelAlert.setCancelable(false);
            cancelAlert.showCancelButton(true);
            cancelAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog)
                {
                    Context context = getApplicationContext();
                    alertTypeTV.setVisibility(View.INVISIBLE);
                    cancelAlert
                            .setTitleText("ההתראה בוטלה!")
                            .setContentText("כעת לא תקבל התראה למכשיר!")
                            .setConfirmText("אישור")
                            .showCancelButton(false)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    int serviceType=sharedPrefQueue.getInt("SERVICE_TYPE",0);
                    if(serviceType == 0) {
                        timeAlertServiceIntent = new Intent(context, TimeAlertService.class);
                        stopService(timeAlertServiceIntent);
                    }
                    else if(serviceType == 1) {
                        clientAlertServiceIntent = new Intent(context, ClientsAlertService.class);

                        stopService(clientAlertServiceIntent);
                    }
                    editor.putBoolean("ALERT_ON",false).apply();
                    alertOn=false;
                    alertButton.setText("קבל התראה");
                    alertButton.setBackgroundColor(Color.parseColor("#05b0ff"));
                    alertButton.setBackground(getResources().getDrawable(R.drawable.blue_buttons));


                    run=false; // Alert Button Is Pressed And Service Is On
                }
                });
            cancelAlert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {

                    cancelAlert.cancel();
                }
            }) ;
            cancelAlert.show();
        }
    }

    /**
     * This Method Is Invoked When The User Press The Cancel Queue Button.
     * Pressing This Button Will Display A Dialog And Cancel The Queue And
     * The Alert If Exist.
     */
    public void cancelQueueButtonClick(View view) {//getLineAsync.cancel(true);
        cancelQueueAlert=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        cancelQueueAlert.setTitleText("יציאה מהתור");
        cancelQueueAlert.setContentText("האם אתה בטוח שברצונך לצאת מהתור");
        cancelQueueAlert.setCancelText("להישאר בתור");
        cancelQueueAlert.setConfirmText("לצאת מהתור");
        cancelQueueAlert.setCancelable(false);
        cancelQueueAlert.showCancelButton(true);
        cancelQueueAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                Context context = getApplicationContext();
                // If Alert Is On, Cancel The Alert.
                boolean alertIsON = sharedPrefQueue.getBoolean("ALERT_ON", false);
                if (timer != (null))
                    timer.cancel();

                if (alertIsON) {
                    int serviceType = sharedPrefQueue.getInt("SERVICE_TYPE", 0);
                    if (serviceType == 0) {
                        timeAlertServiceIntent = new Intent(context, TimeAlertService.class);
                        stopService(timeAlertServiceIntent);
                    } else if (serviceType == 1) {
                        clientAlertServiceIntent = new Intent(context, ClientsAlertService.class);
                        stopService(clientAlertServiceIntent);
                    }
                }
                editor.putBoolean("ALERT_ON", false).apply();
                editor.putInt("THE_LINE", 0).apply();
                editor.putLong("AVERAGE_TIME", 0).apply();

                CharSequence text = "התור שלך בוטל";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent i = new Intent(context, Entry.class);
                startActivity(i);
                if (asyncIsRunning)
                    getLineAsync.cancel(true);
                finish();

            }
        });
        cancelQueueAlert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                cancelQueueAlert.cancel();
            }
        }) ;
        cancelQueueAlert.show();
    }


    /**
     * This Method Set The Dialogs To Choose Between Two The Alert Types.
     * 1. Average Time Before The Queue Is Arrived.
     * 2. How Many Clients Are Waiting Before Him.
     * When The User Choose The Alert Type, He Needs To Choose The How Many Clients
     * Or How Much Time Before His Queue.
     */
    private void setChooseAlertDialog()
    {
        final CharSequence[] items = {
                "התראה לפי זמן ממוצע", "התראה לפי מספר האנשים בתור"
        };
        final SweetAlertDialog successDialog= new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int[] flagToReturn = new int[1];
        builder.setTitle("בחר את סוג ההתראה");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                defineAlert(item);
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(true);
        alert.show();
    }

    /**
     * This Method Set The Dialogs To Choose From .
     * 1. Average Time Before The Queue Is Arrived : 30,15,10 Minutes.
     * 2. How Many Clients Are Waiting Before Him : 20,10,5 Clients.
     */
    private void defineAlert(int item)
    {
        final Context context1=this;
        final CharSequence[] itemsForTimeAlert = {
                "30 דקות", "20 דקות", "10 דקות"
        };
        final CharSequence[] itemsForClientsAlert = {
                "20 לקוחות ממתינים", "10 לקוחות ממתינים", "5 לקוחות ממתינים"
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boolean timeIsOver=(timeCounterTV.getText().toString()).equals("00:00:00");
        if(item==0)
        {
            editor.putInt("SERVICE_TYPE",GeneralConstans.SERVICE_TYPE_TIME).apply();
            builder.setTitle("בחר כמה זמן לפני התור תרצה לקבל התראה :");
            builder.setItems(itemsForTimeAlert, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    alertOn=true;
                    Log.d("Time From The TV ","" + timeCounterTV.getText().toString() );
                    Long millis=parseTimeFromTV(timeCounterTV.getText().toString());
                    editor.putLong("AVERAGE_TIME", millis).apply();
                    editor.putLong("REQUEST_CURRENT_TIME", System.currentTimeMillis()).apply();
                    if(item==0)
                    {
                        editor.putLong("USER_TIME_CHOICE", GeneralConstans.THIRTY_MIN_MILLIS).apply();
                    }
                    else if(item==1)
                    {
                        editor.putLong("USER_TIME_CHOICE",GeneralConstans.TWENTY_MIN_MILLIS).apply();
                    }
                    else if(item==2)
                    {
                        editor.putLong("USER_TIME_CHOICE", GeneralConstans.TEN_MIN_MILLIS).apply();
                    }
                    // If The Current Time Is Lower Than The Choosen Time, The Alert Wont Be Created.
                        if(sharedPrefQueue.getLong("AVERAGE_TIME", 0)<sharedPrefQueue.getLong("USER_TIME_CHOICE",0))
                        {
                            SweetAlertDialog errorDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                            errorDialog.setTitleText("אין אפשרות ליצור התראה");
                            errorDialog.setContentText("הזמן לקבלת התור הינו קצר מדי");
                            errorDialog.show();
                        }
                        else { // Else, Create The Alert And Chnge The Button Color And Text.
                            SweetAlertDialog successDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                            successDialog.setTitleText("ההתראה נוצרה בהצלחה");
                            successDialog.setContentText("ההתראה תתקבל למכשירך לפי הזמן שבחרת");
                            successDialog.show();
                            editor.putBoolean("ALERT_ON",true).apply();
                            alertButton.setText("בטל התראה");
                            alertButton.setBackground(getResources().getDrawable(R.drawable.red_buttons));


                            timeAlertServiceIntent = new Intent(context1, TimeAlertService.class);
                            startService(timeAlertServiceIntent);
                            setAlertTV();
                        }
                }
            });


        }
        else if(item==1)
        {
            editor.putInt("SERVICE_TYPE",GeneralConstans.SERVICE_TYPE_CLIENTS).apply();
            final int currentLineFromParse = parseTotalFromTV(totalQueueDisplay.getText().toString());
            builder.setTitle("בחר מספר לקוחות הממתינים לפניך  לקבלת התראה :");
            builder.setItems(itemsForClientsAlert, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if(item==0)
                    {
                        editor.putInt("USER_CLIENT_CHOICE",20).apply();
                    }
                    else if(item==1)
                    {
                        editor.putInt("USER_CLIENT_CHOICE",10).apply();

                    }
                    else if(item==2)
                    {
                        editor.putInt("USER_CLIENT_CHOICE",5).apply();

                    }
                    int userClientChoice = sharedPrefQueue.getInt("USER_CLIENT_CHOICE",0);
                    // If The Current Wating Clinets Are Smaller Than The User Choice, The Alert Wont Be Created.

                    if(currentLineFromParse<userClientChoice)
                    {
                        SweetAlertDialog errorDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                        errorDialog.setTitleText("אין אפשרות ליצור התראה");
                        errorDialog.setContentText("מספר הלקוחות הממתינים נמוך ממה שבחרת!");
                        errorDialog.show();
                    }
                    else {  // Else, Create The Alert And Change The Button Color And Text.
                        editor.putBoolean("ALERT_ON", true).apply();
                        alertOn = true;
                        alertButton.setText("בטל התראה");
                        alertButton.setBackground(getResources().getDrawable(R.drawable.red_buttons));
                        SweetAlertDialog successDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("ההתראה נוצרה בהצלחה");
                        successDialog.setContentText("ההתראה תתקבל למכשירך כאשר תורך יתקרב");
                        successDialog.show();
                        clientAlertServiceIntent = new Intent(context1, ClientsAlertService.class);
                        clientAlertServiceIntent.putExtra("branchId", branchId);
                        clientAlertServiceIntent.putExtra("userQueue", userQueue);
                        startService(clientAlertServiceIntent);
                        setAlertTV();
                    }
                }
            });

        }
        AlertDialog alert = builder.create();
        alert.setCancelable(true);
        alert.show();
    }

    // Parse The Total Waiting Clients For The Check To The Alert.
    private int parseTotalFromTV(String total) {
        if( isInteger(total))
        {
            return Integer.parseInt(total);
        }
        else return 0;
    }

    // Parse The Average Time From The Text View For The Check To The Alert.
    private Long parseTimeFromTV(String time)
    {
        if(!(time.equals("00:00:00"))) {
            Long millis, secondsRR;
            int hours = Integer.parseInt(time.substring(0, 2));
            int minutes = Integer.parseInt(time.substring(3, 5));
            int seconds = Integer.parseInt(time.substring(6, 7));
            secondsRR = TimeUnit.HOURS.toSeconds(hours) +
                    TimeUnit.MINUTES.toSeconds(minutes) +
                    seconds;
            millis = TimeUnit.SECONDS.toMillis(secondsRR);
            return millis;
        }
        return Long.valueOf(0);
    }

    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    /**
     *  Set The User Queue And Save It To The SharedPrefrences.
     */
    public void setSharedPrefQueue(int lineNum)
    {
        editor.putInt("THE_LINE",lineNum).apply();
        userQueue=sharedPrefQueue.getInt("THE_LINE",0);
        editor.putString("BUSINESS_NAME",businessNameFromIntent).apply();
        editor.putInt("BRANCH_ID",branchId).apply();
        userQueueDisplay.setText(Integer.toString(sharedPrefQueue.getInt("THE_LINE", 0)));
    }

    /**
     *
     * @param receivedHours
     * @param receivedMinutes
     * @param receivedSeconds
     * @param waitingClients
     * @param numOfClerks
     * Sets The Average Time Until The User Queue Will Arrived.
     * Starts The Timer Class.
     */
    public void setAverage(int receivedHours, int receivedMinutes, int receivedSeconds,int waitingClients,int numOfClerks)
    {
        Long millis,secondsRR;
        secondsRR = TimeUnit.HOURS.toSeconds(receivedHours)+
                TimeUnit.MINUTES.toSeconds(receivedMinutes)+
                receivedSeconds;
        secondsRR = (secondsRR*waitingClients)/numOfClerks;
        millis=TimeUnit.SECONDS.toMillis(secondsRR);
        timer = new CounterClass(millis, 1000,this);
        timer.start();
    }

//########################################################################################

    /**
     * This Class Is The Data Access Layer To Calculate A User Queue Number.
     * This Invokes When The User Scan The QR-Code Or Get A Queue By GPS Distance Check.
     */
    public class LineNumberCalculation extends AsyncTask<String,Object,Integer>
    {
        private int branchId;
        String DB_URL =DatabaseConstants.DB_URL;
        String USER = DatabaseConstants.USER;
        String PASS = DatabaseConstants.PASS;

        public LineNumberCalculation(int branchId) {
            this.branchId=branchId;
        }

        // doInBackground Starts The Connection To The DB And Sends The Data In The Process

        @Override
        protected Integer doInBackground(String... sqlQ) {
            int response = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                String result = "\nDatabase connection success\n";
                Statement st = con.createStatement();
                Statement st3 = con.createStatement();
                String query = "SELECT CurrentQueue,TotalQueue,AverageTime,NumberOfClerks FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query3 ="UPDATE Queue SET TotalQueue = TotalQueue + 1 WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                 st3.executeUpdate(query3);
                while (rs.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue= rs.getInt("TotalQueue");
                    int numOfClerks = rs.getInt("NumberOfClerks");
                    Time time=rs.getTime("AverageTime");
                    response = currentQueue;
                    publishProgress(currentQueue,totalQueue,time,numOfClerks);

                }

                Log.d(result, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onProgressUpdate(Object... progress) {
            Time t = (Time) progress[2];
            int numOfClerks=(Integer)progress[3];
            int currQueue=(Integer) progress[0];
            int totalQueue=(Integer) progress[1];
            editor.putInt("TOTAL_QUEUE", totalQueue).apply();
            int numOfPeopleForAverage=totalQueue-currQueue;// The Line Of The User Update The Total
            if (numOfPeopleForAverage<1)
                numOfPeopleForAverage=0;
            setSharedPrefQueue(currQueue + (totalQueue - currQueue) + 1);
            setAverage(t.getHours(),t.getMinutes(),t.getSeconds(),numOfPeopleForAverage,numOfClerks);
        }
    }
}
