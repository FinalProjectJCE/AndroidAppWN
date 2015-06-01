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


public class ChooseBusinessMainActivity extends Activity {

    private Spinner cityTypeSpinner;
    private Spinner businessTypeSpinner;
    private Spinner branchTypeSpinner;
    private int branchId;
    private ArrayList<String> cities,business,branch;
    private DB database = new DB(this);
    private String chosenCity,chosenBusiness,chosenBranch;

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
    public void addItemsToCityTypeSpinner()
    {
        cities= database.getCities();
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, cities);
        cityTypeSpinner.setAdapter(adapter);
    }
    public void addListenerToCityTypeSpinner()
    {
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);
        cityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                System.out.println("The Item Is : "+itemSelectedInSpinner );
                chosenCity = itemSelectedInSpinner.toString();
                Log.d("Item Selcted Is ", chosenCity);
                business = database.getBusinessByCity(chosenCity);
                addItemsToBusinessTypeSpinner();
                addListenerToBusinessTypeSpinner();
                        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addItemsToBusinessTypeSpinner()
    {
        if(business != null)
        {
            businessTypeSpinner = (Spinner)findViewById(R.id.business_type_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, business);
            businessTypeSpinner.setAdapter(adapter);
        }
    }

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

    public void addItemsToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner)findViewById(R.id.branch_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items, branch);
        branchTypeSpinner.setAdapter(adapter);
    }

    public void addListenerToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner) findViewById(R.id.branch_type_spinner);
        branchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                chosenBranch =itemSelectedInSpinner.toString();
                Log.e("The Branch Is",""+chosenBranch);


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void send_business(View view){
        Intent i=new Intent(this,InfoBeforeGettingLineMainActivity.class);
        i.putExtra("name",businessTypeSpinner.getSelectedItem().toString()+" "+
                branchTypeSpinner.getSelectedItem().toString() +" "+
                cityTypeSpinner.getSelectedItem().toString());
        int businessId = database.getBusinessId(chosenCity,chosenBusiness, chosenBranch);
        branchId=businessId;
        Log.e("The Business Id Is+",""+branchId);
        i.putExtra("branchId",  branchId);
        Log.d(cityTypeSpinner.getSelectedItem().toString(), "");
        startActivity(i);
    }
}

