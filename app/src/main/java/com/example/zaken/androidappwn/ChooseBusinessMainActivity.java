package com.example.zaken.androidappwn;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Intent;
import java.util.ArrayList;
/**
 * This Is The Main Class For The "Choose Business" Page.
 * In This Page, The User Needs To Choose The Business That He Want To See Info About,.
 * First He Needs To Choose The City, Than The Business And Than The Branch.
 */
public class ChooseBusinessMainActivity extends Activity {

    private Spinner cityTypeSpinner,businessTypeSpinner,branchTypeSpinner;
    private int branchId;
    private ArrayList<String> cities,business,branch;
    private BusinessesBL database = new BusinessesBL(this);
    private String chosenCity,chosenBusiness,chosenBranch;

    /**
     * On The Creation Of The Page, The Spinners Are Being Filled.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_business_activity);
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);
        addItemsToCityTypeSpinner();
        addListenerToCityTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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

    // This Method Adds Item To The City Spinner From The Database.
    public void addItemsToCityTypeSpinner()
    {
        cities= database.getCities();
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, cities);
        cityTypeSpinner.setAdapter(adapter);
    }

    /**
     * This Method Is The Listener To The City Spinner.
     * It Passes The User`s City Choice To Fill The Business Spinner.
     */
    public void addListenerToCityTypeSpinner()
    {
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);
        cityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                chosenCity = itemSelectedInSpinner.toString();
                business = database.getBusinessByCity(chosenCity);
                addItemsToBusinessTypeSpinner();
                addListenerToBusinessTypeSpinner();
                        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // This Method Adds Item To The Business Spinner From The Database.
    public void addItemsToBusinessTypeSpinner()
    {
        if(business != null)
        {
            businessTypeSpinner = (Spinner)findViewById(R.id.business_type_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, business);
            businessTypeSpinner.setAdapter(adapter);
        }
    }

    /**
     * This Method Is The Listener To The Business Spinner.
     * After The User Choose A City And . The Business Spinner Is Being Filled Accord To The City.
     * It Passes The User`s Business Choice To Fill The Branch Spinner.
     */
    public void addListenerToBusinessTypeSpinner()
    {
        businessTypeSpinner = (Spinner) findViewById(R.id.business_type_spinner);
        businessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                chosenBusiness = itemSelectedInSpinner.toString();
                branch=database.getBranchByCityAndBusiness(chosenCity,chosenBusiness);
                addItemsToBranchTypeSpinner();
                addListenerToBranchTypeSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // This Method Adds Item To The Branch Spinner From The Database.
    public void addItemsToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner)findViewById(R.id.branch_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, branch);
        branchTypeSpinner.setAdapter(adapter);
    }

    /**
     * This Method Is The Listener To The Branch Spinner.
     * After The User Choose A City And A Business, The Branch Spinner Is Being Filled Accord To The City.
     */
    public void addListenerToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner) findViewById(R.id.branch_type_spinner);
        branchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                chosenBranch =itemSelectedInSpinner.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * This Method Is Invoked When The User Press The Send Button.
     * It Redirect The User To The Business Info Page.
     */
    public void send_business(View view){
        Intent i=new Intent(this,InfoBeforeGettingLineMainActivity.class);
        i.putExtra("name",businessTypeSpinner.getSelectedItem().toString()+" "+
                branchTypeSpinner.getSelectedItem().toString() +" "+
                cityTypeSpinner.getSelectedItem().toString());
        int businessId = database.getBusinessId(chosenCity,chosenBusiness, chosenBranch);
        branchId=businessId;
        i.putExtra("branchId",  branchId);
        Log.d(cityTypeSpinner.getSelectedItem().toString(), "");
        startActivity(i);
    }
}

