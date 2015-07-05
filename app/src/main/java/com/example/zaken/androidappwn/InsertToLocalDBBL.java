package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Zaken on 30/03/2015.
 * This Is The Business Logic Layer For The Insertion The Data To The Database.
 */
public class InsertToLocalDBBL
{
    InsertToLocalDBDAL task;
    public void getCities(Activity activity,Context context)
    {
        task = new InsertToLocalDBDAL(activity,context);
        task.execute();
    }
}
