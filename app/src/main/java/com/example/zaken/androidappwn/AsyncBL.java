package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Zaken on 30/03/2015.
 */
public class AsyncBL
{
    Async task;
    Async task2;
    Async task3;
    static ArrayList<String> keysList;
    public void getCities(Activity activity,Context context)
    {
        task = new Async(1,activity,context,0,"");
        task.execute();
        keysList = new ArrayList<String>();
    }

    public void getBusiness(Activity activity,Context context, int cityId)
    {
        task2 = new Async(2,activity,context,cityId,"");
        task2.execute();
    }

    public void getBranches(Activity activity,Context context, int cityId)
    {
        task3 = new Async(3,activity,context,cityId,"");
        task3.execute();
    }
}
