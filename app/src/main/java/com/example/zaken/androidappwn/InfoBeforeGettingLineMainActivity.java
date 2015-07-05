package com.example.zaken.androidappwn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * This Is The Main Class For The "Info Before Getting Line" Page.
 * The User Get To This Page After He Chose The Business He Want To See.
 * In This Page, The User Can See The Next Queue Number, How Manny People Is In Queue And
 * The Average Time For The Queue To Arrive If The User Get In Queue Now.
 * There Are Also 3 Buttons:
 * 1. Choose A Different Business
 * 2. See The Opening Hours Of The Business.
 * 3. Get A Queue.
 * When The User Press The "Get A Queue" Button, There Is A GPS Distance Check For The
 * User From The Business.
 */

public class InfoBeforeGettingLineMainActivity extends Activity implements LocationListener{

    private TextView businessName;
    private QueueInfoOutOfQueueBL qbl;
    private Activity activity;
    private  Context context;
    private int branchId;
    final long MIN_TIME_FOR_UPDATE=5000; // The Time For The Update Is 5 Seconds
    final long MIN_DIS_FOR_UPDATES=10; // The Distance Will Be 10 Meter
    private LocationManager lm;
    private double longitude;
    private double latitude;
    private SweetAlertDialog pDialog;
    private LocationListener thisLocationListener;
    private BusinessesBL database = new BusinessesBL(this);
    private int distanceFromDB;
    private String businessNameFromIntent;
    private OpeningHoursBL openingHours;

    /**
     * This Method, Initialize The Fields And Starts The Async Task For The Display
     * Of The Data.
     * It Gets The Business ID And Name From The Previous Page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity_before_getting_line);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        businessName = (TextView) findViewById(R.id.businessNameTV);
        Intent intent = getIntent();
        businessNameFromIntent = intent.getStringExtra("name");
        branchId = intent.getIntExtra("branchId", 0);
        latitude = database.getLatitude(branchId);
        longitude=database.getLongitude(branchId);
        distanceFromDB=database.getDistance(branchId);
        businessName.setText(businessNameFromIntent);
        setActivity(this);
        qbl=new QueueInfoOutOfQueueBL(this,this,branchId);
        qbl.showQueue();
        thisLocationListener=this;
        }

    private Context getContext()
    {
        return this.context;
    }
    private void setActivity(Activity activity)
    {
        this.activity=activity;
    }
    private Activity getActivity()
    {
        return this.activity;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
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

    // When The Activity Pauses, Kill The Task.
    @Override
    public void onPause(){
        super.onPause();
        qbl.task.cancel(true);
    }

    // When The Activity Restarts, Start The Task Again.
    public void onRestart(){
        super.onRestart();
        qbl=new QueueInfoOutOfQueueBL(this,this,branchId);
        qbl.showQueue();
    }

    public void testForInLine(View view)
    {
        Intent i=new Intent(this,ChooseBusinessMainActivity.class);
        startActivity(i);
    }

    /**
     * This Method Is Invoked When The User Press The "Get A Queue" Button.
     * It Sets An Alert And Starts The GPS Distance Check.
     */
    public void getQueueButtonClick(final View view)
    {
        lm.requestLocationUpdates(lm.GPS_PROVIDER,MIN_TIME_FOR_UPDATE,MIN_DIS_FOR_UPDATES,this);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("אנא המתן בזמן שמיקומך נקבע");
        pDialog.showCancelButton(true);
        pDialog.setCancelText("עצור");
        pDialog.setCancelable(false);
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                lm.removeUpdates(thisLocationListener);
                pDialog.cancel();
            }
        });
        pDialog.show();

    }


    /**
     * This Method Is Part Of The LocationListener.
     * When The User Location Is Defined, The Distance Check Is Started.
     * If The Distance Is Too Long, A Dialog Will Apears.
     * If The Distance Is OK, The User Will Redirect To The Final Page And A Queue
     * Will Be Calculated.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        lm.removeUpdates(this);

            float distance;
            Location businessLocation = new Location("");
            businessLocation.setLatitude(latitude);
            businessLocation.setLongitude(longitude);
            distance = location.distanceTo(businessLocation);
            if (distance < distanceFromDB || distanceFromDB==0 ) {
                Intent i = new Intent(this, FinalPage.class);
                i.putExtra("businessNameFromIntent",businessNameFromIntent+" From GPS");
                i.putExtra("branchId", branchId );
                pDialog.cancel();
                startActivity(i);
            } else {
                pDialog.cancel();
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("הנך עדיין רחוק/ה מבית העסק")
                        .setContentText("עלייך להיות קרוב/ה לבית העסק בלפחות 100 מטר")
                        .setConfirmText("אישור")
                        .showCancelButton(false)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            }
    }

    public void openingHoursOnClick(View view)
    {
        openingHours=new OpeningHoursBL();
        openingHours.getOpeningHoursBL(this,branchId);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    // If The GPS Is OFF, A Message Will Apeare To The User.
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,
                "על מנת לקבל תור עליך להדליק את ה-GPS",
                Toast.LENGTH_LONG).show();
        pDialog.cancel();
    }
}
