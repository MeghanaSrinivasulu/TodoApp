package com.example.user1.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by User 1 on 20-06-2015.
 */
public class MyReceiver extends BroadcastReceiver {
    private NotificationManager mManager;
    private String[] inProgressTasks;
    Set<String> inProgress = new HashSet<String>();

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences("InProgressTasks",Context.MODE_PRIVATE);
        inProgress = preferences.getStringSet("taskDescriptions",new HashSet<String>());
        inProgressTasks = new String[inProgress.size()];
        if(inProgress != null){
            int count = 0;
            Iterator<String> iterator = inProgress.iterator();

            while(iterator.hasNext()){
                inProgressTasks[count] = iterator.next();
                Toast.makeText(context,inProgressTasks[count],Toast.LENGTH_SHORT).show();
                count++;
            }
            //Toast.makeText(context,"count:"+count,Toast.LENGTH_SHORT).show();
        }


        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context,MainActivity.class);
        Notification notification = new Notification.Builder(context).setContentText("Test message!").setSmallIcon(R.drawable.ic_check).setWhen(System.currentTimeMillis()).build();
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( context,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Toast.makeText(context,"test",Toast.LENGTH_SHORT).show();
        mManager.notify(0, notification);
    }
}
