package com.example.zaken.androidappwn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Zaken on 09/05/2015.
 */
public class DB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dataBaseFile.db";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + DatabaseConstants.TABLE_NAME + "(" +
                DatabaseConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseConstants.MAIN_ID + " INTEGER," +
                DatabaseConstants.CITIES + " TEXT_TYPE," +
                DatabaseConstants.BUSINESS + " TEXT_TYPE," +
                DatabaseConstants.LATITUDE + " DOUBLE," +
                DatabaseConstants.LONGITUDE + " DOUBLE," +
                DatabaseConstants.DISTANCE + " INTEGER," +
                DatabaseConstants.BRANCH +" TEXT_TYPE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_NAME);
    }

//

    public void insertToDB(String city, String business, int id, String branch , double latitude,double longitude,int distance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.MAIN_ID, id);
        values.put(DatabaseConstants.CITIES, city);
        values.put(DatabaseConstants.BUSINESS, business);
        values.put(DatabaseConstants.BRANCH, branch);
        values.put(DatabaseConstants.LATITUDE,latitude);
        values.put(DatabaseConstants.LONGITUDE,longitude);
        values.put(DatabaseConstants.DISTANCE,distance);
        db.insertOrThrow(DatabaseConstants.TABLE_NAME, null, values);
        db.close();
    }


    public ArrayList<String> getCities() {
        ArrayList<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.CITIES + " FROM " + DatabaseConstants.TABLE_NAME  , null);
        while(c.moveToNext())
        cities.add(c.getString(0));
        return cities;
    }

    public ArrayList<String> getBusinessByCity(String choosenCity) {
        ArrayList<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.BUSINESS + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.CITIES + "= '" + choosenCity + "'", null);
        while(c.moveToNext())
            cities.add(c.getString(0));
        return cities;
    }

    public ArrayList<String> getBranchByCityAndBusiness(String choosenCity, String chosenBusiness) {
        ArrayList<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.BRANCH + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.CITIES + "= '" + choosenCity + "' AND "+DatabaseConstants.BUSINESS + "= '" + chosenBusiness + "'", null);
        while(c.moveToNext())
            cities.add(c.getString(0));
        return cities;

    }

    public int getBusinessId(String choosenCity, String chosenBusiness, String chosenBranch) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.MAIN_ID + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.CITIES + "= '" + choosenCity + "' AND "+DatabaseConstants.BUSINESS + "= '" + chosenBusiness + "' AND "+DatabaseConstants.BRANCH + "= '" + chosenBranch + "' ", null);
        c.moveToNext();
        return c.getInt(0);
    }

    public double[] getCoordinatesAndDistance(int businessId) {
        double[] toReturn=new double[3];
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.LATITUDE+", " +
                DatabaseConstants.LONGITUDE+", " +DatabaseConstants.DISTANCE +" FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.MAIN_ID + "= '" + businessId + "'", null);
        c.moveToNext();
        toReturn[0]=c.getDouble(0);
        Log.d("c.getDouble(0);", c.getDouble(0) + "");
        toReturn[1]=c.getDouble(1);
        Log.d("c.getDouble(1);", c.getDouble(1) + "");
        toReturn[2]=c.getDouble(2);
        Log.d("c.getDouble(2);", c.getDouble(2) + "");
        return toReturn;
    }

    public double getLongitude(int businessId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.LONGITUDE + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.MAIN_ID + "= '" + businessId + "'", null);
        c.moveToNext();
        return c.getDouble(0);
    }

    public double getLatitude(int businessId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.LATITUDE + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.MAIN_ID + "= '" + businessId + "'", null);
        c.moveToNext();
        return c.getDouble(0);
    }

    public int getDistance(int businessId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.DISTANCE + " FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.MAIN_ID + "= '" + businessId + "'", null);
        c.moveToNext();
        return c.getInt(0);
    }

    public String getBusinessName(int businessId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT " + DatabaseConstants.BUSINESS+", " +
                DatabaseConstants.BRANCH+", " +DatabaseConstants.CITIES +" FROM " + DatabaseConstants.TABLE_NAME  + " WHERE " + DatabaseConstants.MAIN_ID + "= '" + businessId + "'", null);
        c.moveToNext();
        return c.getString(0) + " " + c.getString(1) + " " +
                c.getString(2) ;
    }

}