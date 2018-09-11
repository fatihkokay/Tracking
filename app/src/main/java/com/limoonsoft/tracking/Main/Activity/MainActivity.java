package com.limoonsoft.tracking.Main.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.limoonsoft.core.DriverModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteMovementModel;
import com.limoonsoft.data.BaseEntity;
import com.limoonsoft.data.PersistenceManager;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.Main.Adapter.BoardingPointAdapter;
import com.limoonsoft.tracking.Main.Adapter.CallNotificationAdapter;
import com.limoonsoft.tracking.Main.Adapter.LandingPointAdapter;
import com.limoonsoft.tracking.Main.Adapter.PollingAdapter;
import com.limoonsoft.tracking.R;
import com.limoonsoft.tracking.Service.TrackingService;
import com.limoonsoft.tracking.Util.JsonDateDeserializer;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private LinearLayout linearLayoutMenuButton1;
    private LinearLayout linearLayoutMenuButton2;
    private LinearLayout linearLayoutMenuButton3;
    private LinearLayout linearLayoutMenuButton4;
    private ListView listView;
    private View viewHeader;
    private View loginProgress;
    private TextView textViewPlate;
    private TextView textViewDriver;
    private TextView textViewDate;
    private TextView textViewTime;
    private boolean Status = false;
    private BaseEntity<DriverModel> driverModelBaseEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LockScreen();
        LoadUI();
        LoadData();
        LoadDateTime();
        RequestPermissions();
        StartService();


        int StartType = getIntent().getIntExtra("StartType",-1);
        if (StartType != -1){
            switch (StartType){
                case 1:
                    RouteMovementChange(getIntent().getIntExtra("RouteId",0),0);

                    LoadCallNotification(getIntent().getIntExtra("RouteId",0),getIntent().getIntExtra("RouteStatus",0));
                    break;
                case 2 :
                    RouteMovementSchoolExitChange(getIntent().getIntExtra("RouteId",0),0);

                    LoadPolling(getIntent().getIntExtra("RouteId",0),getIntent().getIntExtra("RouteStatus",0));
                    break;
            }
        }
    }

    private void RouteMovementChange(int RouteId, int Status) {

        RouteMovementModel model = new RouteMovementModel();
        model.setRouteId(RouteId);
        model.setStatus(Status);


        RestClient restClient = new RestClient(getBaseContext());
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Route/RouteMovementChange", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    private void RouteMovementSchoolExitChange(int RouteId, int Status) {

        RouteMovementModel model = new RouteMovementModel();
        model.setRouteId(RouteId);
        model.setSchoolExitStatus(Status);


        RestClient restClient = new RestClient(getBaseContext());
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Route/RouteMovementSchoolExitChange", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    private void StartService() {
        if (!isServiceRunning(TrackingService.class)){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TrackingService.class);
            startService(intent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void LockScreen() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "STS");
            if ((wakeLock != null) && (wakeLock.isHeld() == false)) {
                wakeLock.acquire();
            }
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    private void LoadDateTime() {
        Thread timerThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                textViewDate.setText(getCurrentDate());
                                textViewTime.setText(getCurrentTime());
                            }
                        });
                    }
                }

            }
        };
        timerThread.start();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd.MM.yyyy");
        return   mdformat.format(calendar.getTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        return   mdformat.format(calendar.getTime());
    }

    private void LoadData() {
        DriverModel driverModel = GetDriver();
        if(driverModel != null){
            textViewDriver.setText(driverModel.getNameSurname());
            textViewPlate.setText("42 ELV 22");
        }
    }

    protected DriverModel GetDriver() {
        driverModelBaseEntity = new BaseEntity<>(getBaseContext(),DriverModel.class);
        if (driverModelBaseEntity.readAll() == null)
            return null;

        if (driverModelBaseEntity.readAll().size() == 0)
            return null;

        return driverModelBaseEntity.readAll().get(0);
    }

    private void LoadUI() {
        textViewDate =(TextView)findViewById(R.id.textViewDate);
        textViewTime =(TextView)findViewById(R.id.textViewTime);

        textViewPlate =(TextView)findViewById(R.id.textViewPlate);
        textViewDriver =(TextView)findViewById(R.id.textViewDriver);
        loginProgress = findViewById(R.id.loginProgress);

        listView = (ListView)findViewById(R.id.listView);

        linearLayoutMenuButton1 = (LinearLayout)findViewById(R.id.linearLayoutMenuButton1);
        linearLayoutMenuButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTracking(1);
            }
        });

        linearLayoutMenuButton2 = (LinearLayout)findViewById(R.id.linearLayoutMenuButton2);
        linearLayoutMenuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTracking(0);
            }
        });

        linearLayoutMenuButton3 = (LinearLayout)findViewById(R.id.linearLayoutMenuButton3);
        linearLayoutMenuButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ListViewClear();

                    RestClient restClient = new RestClient(getBaseContext());
                    RequestParams params = new RequestParams();
                    params.add("DriverId",GetDriver().getId()+"");
                    params.add("RouteType","0");
                    restClient.get("Route/DriverRouteStudent/", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();

                            loginProgress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200){
                                RouteModel model = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create().fromJson(new String(responseBody),RouteModel.class);
                                if (model == null){
                                    Toast.makeText(getBaseContext(),"Rota tanımınız tanımlanmamış merkez ile irtibata geçiniz.",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                BoardingPointAdapter boardingPointAdapter = new BoardingPointAdapter(MainActivity.this,model,loginProgress);
                                listView.addHeaderView(getListViewHeader("BİNME NOKTASI BELİRLEME",model.getName()));
                                listView.setAdapter(boardingPointAdapter);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e("Driver RestClient","Hata oluştu");
                        }



                        @Override
                        public void onFinish() {
                            super.onFinish();

                            loginProgress.setVisibility(View.GONE);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        linearLayoutMenuButton4 = (LinearLayout)findViewById(R.id.linearLayoutMenuButton4);
        linearLayoutMenuButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ListViewClear();

                    RestClient restClient = new RestClient(getBaseContext());
                    RequestParams params = new RequestParams();
                    params.add("DriverId",GetDriver().getId()+"");
                    params.add("RouteType","1");
                    restClient.get("Route/DriverRouteStudent/", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();

                            loginProgress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200){
                                RouteModel model = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create().fromJson(new String(responseBody),RouteModel.class);
                                if (model == null){
                                    Toast.makeText(getBaseContext(),"Rota tanımınız tanımlanmamış merkez ile irtibata geçiniz.",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                LandingPointAdapter adapter = new LandingPointAdapter(getBaseContext(),model);
                                listView.addHeaderView(getListViewHeader("İNME NOKTASI BELİRLEME",model.getName()));
                                listView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e("Driver RestClient","Hata oluştu");
                        }



                        @Override
                        public void onFinish() {
                            super.onFinish();

                            loginProgress.setVisibility(View.GONE);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


        });
    }

    private void GetTracking(final int Type) {
        try {
            DriverModel driverModel = GetDriver();

            for (int i = 0;i<driverModel.getRouteList().size();i++){
                RouteModel routeModel = driverModel.getRouteList().get(i);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                if (Type == 0){
                    if (routeModel.getRouteMovement() != null){
                        if (routeModel.getRouteMovement().getStatus() == 0){
                            LoadCallNotification(routeModel.getId(),0);
                        }
                    }
                }

                if (Type == 1){
                    if (routeModel.getRouteMovement() != null){
                        if (routeModel.getRouteMovement().getSchoolExitStatus() == 0){
                            LoadPolling(routeModel.getId(),0);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void LoadPolling(int RouteId,int Status) {
        try {
            ListViewClear();

            RestClient restClient = new RestClient(getBaseContext());
            RequestParams params = new RequestParams();

            params.add("DriverId",GetDriver().getId()+"");
            params.add("RouteId",RouteId+"");

            String url = "";
            if (Status == -1){
                url = "Route/DriverRouteStudent/";
            }
            if (Status == 0){
                url = "Route/DriverRouteStudentInMovement/";
            }


            restClient.get(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();

                    loginProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200){
                        RouteModel model = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create().fromJson(new String(responseBody),RouteModel.class);
                        if (model == null){
                            Toast.makeText(getBaseContext(),"Rota tanımınız tanımlanmamış merkez ile irtibata geçiniz.",Toast.LENGTH_LONG).show();
                            return;
                        }

                        PollingAdapter adapter = new PollingAdapter(getBaseContext(),model);
                        listView.addHeaderView(getListViewHeader("OKULDAN ÇIKIŞ YOKLAMA",model.getName()));
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("Driver RestClient","Hata oluştu");
                }



                @Override
                public void onFinish() {
                    super.onFinish();

                    loginProgress.setVisibility(View.GONE);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void LoadCallNotification(int RouteId,int Status) {
        try {
            ListViewClear();

            RestClient restClient = new RestClient(getBaseContext());
            RequestParams params = new RequestParams();
            params.add("DriverId",GetDriver().getId()+"");
            params.add("RouteId",RouteId+"");

            String url = "";
            if (Status == -1){
                url = "Route/DriverRouteStudent/";
            }
            if (Status == 0){
                url = "Route/DriverRouteStudentInMovement/";
            }

            restClient.get(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();

                    loginProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200){

                        Log.e("JSON",new String (responseBody));

                        RouteModel model = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create().fromJson(new String(responseBody),RouteModel.class);
                        if (model == null){
                            Toast.makeText(getBaseContext(),"Rota tanımınız tanımlanmamış merkez ile irtibata geçiniz.",Toast.LENGTH_LONG).show();
                            return;
                        }

                        CallNotificationAdapter adapter = new CallNotificationAdapter(getBaseContext(),model);
                        listView.addHeaderView(getListViewHeader("ARANA BİLGİLENDİRME",model.getName()));
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getBaseContext(),"Rota Bilgilisi Yüklenirken Hata Oluştu.Lütfen Tekrar Deneyiniz.",Toast.LENGTH_SHORT).show();
                }



                @Override
                public void onFinish() {
                    super.onFinish();

                    loginProgress.setVisibility(View.GONE);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ListViewClear() {
        listView.setAdapter(null);
        if (listView.getHeaderViewsCount() > 0){
            listView.removeHeaderView(viewHeader);
        }
    }

    private View getListViewHeader(String title,String subTitle){
        try {
            viewHeader = (View)getLayoutInflater().inflate(R.layout.layout_main_listview_header, listView, false);
            if (viewHeader == null)
                return null;

            TextView textViewTitle =(TextView)viewHeader.findViewById(R.id.textViewTitle);
            TextView textViewSubTitle =(TextView)viewHeader.findViewById(R.id.textViewSubTitle);
            textViewTitle.setText(title);
            textViewSubTitle.setText(subTitle);

            return  viewHeader;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    private void RequestPermissions() {

        Set<String> missingPermissions = new HashSet<>();
        if (!hasPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            missingPermissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }

        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!missingPermissions.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onBackPressed() {

    }
}
