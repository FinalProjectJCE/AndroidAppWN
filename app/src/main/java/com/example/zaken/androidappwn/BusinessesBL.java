package com.example.zaken.androidappwn;

import android.content.Context;

import java.util.ArrayList;

/**
 * This Class Is The Business Logic Layer
 */
public class BusinessesBL
{


    private BusinessesDAL businessesDB;

    public BusinessesBL(Context context)
    {
        businessesDB=new BusinessesDAL(context);
    }

    public void insertToDB(String city, String business, int id, String branch , double latitude,double longitude,int distance) {
           businessesDB.insertToDB(city, business, id, branch, latitude, longitude, distance);
    }

    public ArrayList<String> getCities() {
       return businessesDB.getCities();
    }
    public ArrayList<String> getBusinessByCity(String choosenCity) {
        return businessesDB.getBusinessByCity(choosenCity);
    }
    public ArrayList<String> getBranchByCityAndBusiness(String choosenCity, String chosenBusiness) {
       return businessesDB.getBranchByCityAndBusiness(choosenCity, chosenBusiness);
    }
    public int getBusinessId(String choosenCity, String chosenBusiness, String chosenBranch) {
       return businessesDB.getBusinessId(choosenCity, chosenBusiness, chosenBranch);
    }

    public double getLongitude(int businessId) {
        return businessesDB.getLongitude(businessId);
    }
    public double getLatitude(int businessId) {
        return businessesDB.getLatitude(businessId);
    }
    public int getDistance(int businessId) {
        return businessesDB.getDistance(businessId);
    }
    public String getBusinessName(int businessId)
    {
        return businessesDB.getBusinessName(businessId);
    }

    }
