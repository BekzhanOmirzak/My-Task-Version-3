package com.example.hometaskversion3.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Utils {


    private String[] permissions = {
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
            CAMERA,
            RECORD_AUDIO
    };


    private static Utils instance;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public void checkPermissionForEverything(Activity activity) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
    }


}


//    private void startForeGround() {
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        String channel_Id = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? createNotifactionChannel(manager) : "";
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_Id);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.icon)
//                .setPriority(PRIORITY_MIN)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .build();
//
//        startForeground(1234, notification);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private String createNotifactionChannel(NotificationManager manager) {
//        String channelId = "my_service_channel";
//        String channelName = "My Foreground Service";
//        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
//        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        manager.createNotificationChannel(channel);
//        return channelId;
//    }
//
