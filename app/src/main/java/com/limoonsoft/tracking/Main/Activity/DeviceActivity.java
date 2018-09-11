package com.limoonsoft.tracking.Main.Activity;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.limoonsoft.tracking.R;

public class DeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        TextView textViewDevinceId =(TextView)findViewById(R.id.textViewDevinceId);
        textViewDevinceId.setText(GetDeviceId());
    }

    public String GetDeviceId() {
        return android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
