package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Zaken on 02/04/2015.
 */
public class TotalQueuesBL
{
    TotalQueuesDAL task;
    public void showQueue(Activity activity,Context context,int branchId)
    {
        task= new TotalQueuesDAL(activity,context,branchId);
        task.execute();
    }
}