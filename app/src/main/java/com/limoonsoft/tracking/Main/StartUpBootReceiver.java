package com.limoonsoft.tracking.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.limoonsoft.tracking.Main.Activity.MainActivity;
import com.limoonsoft.tracking.Service.TrackingService;

public class StartUpBootReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("StartUpBootReceiver","Geldi");
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(
                    Intent.ACTION_BOOT_COMPLETED)) {

                Intent main = new Intent(context, MainActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(main);
            }
        }


    }
}