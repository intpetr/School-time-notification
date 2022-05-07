package com.suli.rk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.Duration;
import java.time.LocalDateTime;

public class OrakHatterfolyamat extends Service {
    NotificationManager mNotificationManager;

    period[ ] lessons = {new period(8,0, "az"), new period(9,0,"a"),
            new period(9,55,"a"), new period(10,50,"a"), new period(11,45,"az"), new period(12,55,"a"),
    new period(13,50,"a"), new period(14,40,"a"), new period(15,30,"a")};
    int[] daysend = {};

    int[] endTimesHour = {20,20,20,20,20};
    int[] endTimesMinute = {0,0,0,0,0};

    Context context = this;

    BroadcastReceiver myreceiver;

    public class screenreceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {

            if(isInSchool()){
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){



                    Intent intenty=new Intent(getApplicationContext(),MainActivity.class);
                    String CHANNEL_ID="MYCHANNEL";
                    NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
                    PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intenty,0);
                    Notification notification= null;
                    try {
                        notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                                //.setContentText(getcurrent()) //more text actual content
                                .setContentTitle(getcurrent()) //middle (big) text
                                .setContentIntent(pendingIntent)
                                .addAction(android.R.drawable.sym_action_chat,"Beállítások",pendingIntent)
                                .setChannelId(CHANNEL_ID)
                                .setOngoing(true)
                                .setSound(null, null)
                                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationManager.notify(1,notification);


                }
            }
            else{

                Intent intenty=new Intent(getApplicationContext(),MainActivity.class);
                String CHANNEL_ID="MYCHANNEL";
                NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);

                NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.cancel(1);
                System.out.println("JAEEEEEEEEEEEEEEEEEEEEEEEEEE");
            }


        }

        // constructor
        public screenreceiver(){

        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        myreceiver = new screenreceiver();
        registerReceiver(myreceiver, filter);
        loadEndTimes();
        System.out.println("The background service started.");




    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        String level = "3";

        Intent intenty=new Intent(getApplicationContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intenty,0);
        Notification notification= null;
        try {
            notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    //.setContentText(getcurrent()) //more text actual content
                    .setContentTitle("Az órák háttérfolyamat le van állítva.") //middle (big) text
                    .setContentText("Törölheted ezt az értesítést.")
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.sym_action_chat,"Beállítások",pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setOngoing(false)
                    .setSound(null, null)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
        unregisterReceiver(myreceiver);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }


    private boolean isInSchool(){
        LocalDateTime now = LocalDateTime.now();
        if(now.getDayOfWeek().getValue() <= 5 || now.getHour() > endTimesHour[now.getDayOfWeek().getValue()] || now.getHour()<8){
            return false;
        }
        else{
            return true;
        }
    }

    public String getcurrent() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        for(int i=0; i< lessons.length; i++){
            if(lessons[i].getstartldt().isAfter(now)){
                Duration difference = Duration.between(now,lessons[i].getstartldt());
                int periodNumber = i +1;
                return difference.toMinutes() +" Perc van hátra a " +periodNumber+". óráig.";
                //szamold ki amit akarsz mutatni mert megvan ami kell oran vagy
            }
            else if (lessons[i].getendingldt().isAfter(now)){
                Duration difference = Duration.between(now,lessons[i].getendingldt());
                int periodNumber = i +1;
                return difference.toMinutes() + " Perc van hátra "+periodNumber+". órából.";
                //szamold ki amit akarsz mutatni mert megvan ami kell szünet van
            }
        }
        return "Vége az óráidnak";
    }


    public void loadEndTimes(){
        SharedPreferences sharedPreferences = getSharedPreferences("OrakPrefs", Context.MODE_PRIVATE);
        for(int i=1; i<6; i++){
            try{

                int hour = sharedPreferences.getInt(i+"h",-1);
                int minute = sharedPreferences.getInt(i+"m", -1);
                if(hour != -1 && minute != -1){
                    endTimesHour[i-1] = hour;
                    endTimesMinute[i-1] = minute;
                    System.out.println("LOADED"+hour+" "+minute+" for day "+i);
                }
                else{
                    System.out.println("Didn't load date because hour is "+hour+" and minute is "+minute);
                }

            }
            catch(Exception e){
                System.out.println(e);
            }


        }
    }


}
