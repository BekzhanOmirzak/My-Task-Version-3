package com.example.hometaskversion3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.hometaskversion3.ui.CameraRecorderActivity;

public class ReceiverToStartAnApp extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Home Task 3 Version ", Toast.LENGTH_LONG).show();

        Intent myIntent = new Intent(context, CameraRecorderActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);

    }


}

