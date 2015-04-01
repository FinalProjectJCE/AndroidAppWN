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
    static String tryMe="In AsyncBL Start";
    Async task;
    Async task2;
    static ArrayList<String> keysList;
    public void getCities(Activity activity,Context context)
    {
        tryMe="Inside Get Cities";
         task = new Async(1,activity,context,"","");
        System.out.println("\nBefore execute "+tryMe+"\n");
        task.execute();
        System.out.println("\nAfter execute "+tryMe+"\n");

        keysList = new ArrayList<String>();

        if(task.getStatus() == AsyncTask.Status.FINISHED)
        {
            System.out.println("\nFini\n");
        }

        System.out.println("\nPPLPLPLPLPLP\n");
        for(int i=0;i<keysList.size();i++) {
            System.out.println("\nPPLPLPLPLPLP"+i+"\n");
            System.out.println(keysList.get(i));
        }
        System.out.println("\nPPLPLPLPLPLP\n");
    }
    public void BB(String s)
    {
        System.out.println("\n"+s+"\n");
    }

    public void getBusiness(Activity activity,Context context,String city)
    {
        task2 = new Async(2,activity,context,city,"");
        task2.execute();
    }
}
