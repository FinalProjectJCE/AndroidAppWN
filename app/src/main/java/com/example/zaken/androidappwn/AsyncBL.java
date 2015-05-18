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
    public void getCities(Activity activity,Context context)
    {
        task = new Async(activity,context);
        task.execute();
    }
}
