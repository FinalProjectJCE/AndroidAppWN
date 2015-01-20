package com.example.zaken.androidappwn;

import android.app.Activity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Intent;


public class MainActivity2 extends Activity {

    private Spinner cityTypeSpinner;
    private Spinner businessTypeSpinner;
    private Spinner branchTypeSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_activity2);
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);
        addItemsToCityTypeSpinner();
        addListenerToCityTypeSpinner();
        addItemsToBusinessTypeSpinner();
        addListenerToBusinessTypeSpinner();
        addItemsToBranchTypeSpinner();
        addListenerToBranchTypeSpinner();
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

        ArrayAdapter<CharSequence> cityTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.city_types,android.R.layout.simple_spinner_item);
        cityTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityTypeSpinner.setAdapter(cityTypeSpinnerAdapter);
    }
    public void addListenerToCityTypeSpinner()
    {
        cityTypeSpinner = (Spinner) findViewById(R.id.city_type_spinner);
        cityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                //TODO add somethimg here
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addItemsToBusinessTypeSpinner()
    {
        businessTypeSpinner = (Spinner) findViewById(R.id.business_type_spinner);
        ArrayAdapter<CharSequence> businessTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.business_types,android.R.layout.simple_spinner_item);
        businessTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(businessTypeSpinnerAdapter);
    }

    public void addListenerToBusinessTypeSpinner()
    {
        businessTypeSpinner = (Spinner) findViewById(R.id.business_type_spinner);
        businessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                //TODO add somethimg here
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addItemsToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner) findViewById(R.id.branch_type_spinner);
        ArrayAdapter<CharSequence> branchTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.branch_types,android.R.layout.simple_spinner_item);
        branchTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchTypeSpinner.setAdapter(branchTypeSpinnerAdapter);
    }

    public void addListenerToBranchTypeSpinner()
    {
        branchTypeSpinner = (Spinner) findViewById(R.id.branch_type_spinner);
        branchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
            {
                String itemSelectedInSpinner = parent.getItemAtPosition(position).toString();
                //TODO add somethimg here
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void send_business(View view){
        Intent i=new Intent(this,MainActivity3.class);
        i.putExtra("name",cityTypeSpinner.getSelectedItem().toString() );
       // i.putExtra("time", user.getUserFullName());
        Log.d(cityTypeSpinner.getSelectedItem().toString(), "");
        startActivity(i);
    }


}
