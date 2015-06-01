package com.example.zaken.androidappwn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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


public class InfoBeforeGettingLineMainActivity extends Activity implements LocationListener{

    private TextView businessName;
    private QueueBL qbl;
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
    private DB database = new DB(this);
    private int distanceFromDB;
    private String businessNameFromIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity_before_getting_line);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        businessName = (TextView) findViewById(R.id.businessNameTV);
        Intent intent = getIntent();
        businessNameFromIntent = intent.getStringExtra("name");
        branchId = intent.getIntExtra("branchId",0);
        latitude = database.getLatitude(branchId);
        Log.d("latitude",""+latitude);
        longitude=database.getLongitude(branchId);
        Log.d("longi",""+longitude);
        distanceFromDB=database.getDistance(branchId);
        Log.d("distance",""+distanceFromDB);
        businessName.setText(businessNameFromIntent);
        setActivity(this);
        qbl=new QueueBL();
        qbl.showQueue(getActivity(),getContext(),branchId);
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
    @Override
    public void onPause(){
        super.onPause();
        qbl.task.cancel(true);
//        task.cancel(true);
    }
    public void onRestart(){
        super.onRestart();
        qbl.showQueue(getActivity(),getContext(),branchId);
    }

    public void testForInLine(View view)
    {
        Intent i=new Intent(this,MainActivity4.class);
        i.putExtra("businessNameFromIntent",businessName.getText());
        i.putExtra("branchId",  branchId);
        startActivity(i);
    }

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

        //UPDATE Queue SET CurrentQueue = CurrentQueue + 1 WHERE BusinessId = '111'"
//        Intent i=new Intent(this,MainActivity4.class);
//        i.putExtra("businessNameFromIntent",businessName.getText());
//        i.putExtra("branchId",  branchId);
//        startActivity(i);

    }



    @Override
    public void onLocationChanged(Location location)
    {

            Log.d("ONLOCATION", "Hererer");
            float distance;
            Location businessLocation = new Location("");
            businessLocation.setLatitude(latitude);
            businessLocation.setLongitude(longitude);
            distance = location.distanceTo(businessLocation);
            String notice = "The Distance Between Is \n" + distance;
            Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
            if (distance < distanceFromDB) {
                Intent i = new Intent(this, MainActivity4.class);
                i.putExtra("businessNameFromIntent",businessNameFromIntent+" From GPS");
                i.putExtra("branchId", branchId );
                pDialog.cancel();
                lm.removeUpdates(this);
                startActivity(i);
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("הנך עדיין רחוק/ה מבית העסק")
                        .setContentText("עלייך להיות קרוב/ה לבית העסק בלפחות 100 מטר")
                        .setCancelText("בדוק שנית")
                        .setConfirmText("בטל")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                pDialog.cancel();

                            }
                        })
                        .show();
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,
                "על מנת לקבל תור עליך להדליק את ה-GPS",
                Toast.LENGTH_LONG).show();
        pDialog.cancel();
    }
}
