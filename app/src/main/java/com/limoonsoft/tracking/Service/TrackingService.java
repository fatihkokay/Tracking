package com.limoonsoft.tracking.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.limoonsoft.api.ResultModel;
import com.limoonsoft.core.DriverModel;
import com.limoonsoft.core.MovementModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteMovementModel;
import com.limoonsoft.core.RouteStudentModel;
import com.limoonsoft.core.SmsModel;
import com.limoonsoft.data.BaseEntity;
import com.limoonsoft.data.PersistenceManager;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.Main.Activity.DeviceActivity;
import com.limoonsoft.tracking.Main.Activity.MainActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrackingService extends Service {

    private BaseEntity<DriverModel> driverModelBaseEntity;
    private BaseEntity<RouteModel> routeModelBaseEntity;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Refresh();
        GetTracking();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void Refresh(){
        Thread refreshThread = new Thread() {
            @Override
            public void run() {
                super.run();

                while (true){
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DriverModel driverModel = new DriverModel();
                                driverModel.setDevinceId(GetDeviceId());

                                RestClient restClient = new RestClient(getBaseContext());
                                HttpEntity httpEntity = new StringEntity(new Gson().toJson(driverModel));
                                restClient.post("Driver/Login/",httpEntity,new

                                        AsyncHttpResponseHandler() {
                                            @Override
                                            public void onStart () {
                                                super.onStart();
                                            }

                                            @Override
                                            public void onSuccess ( int statusCode, Header[] headers,byte[] responseBody){
                                                if (statusCode == 200) {
                                                    Log.e("Result", new String(responseBody));

                                                    ResultModel<DriverModel> model = new Gson().fromJson(new String(responseBody), new TypeToken<ResultModel<DriverModel>>() {
                                                    }.getType());

                                                    if (model.getStatus() == 1) {
                                                        DriverModel driverModel = model.getData();
                                                        if (SaveDriver(driverModel)) {
                                                            Log.e("Refresh","Veriler başarılı bir şekilde güncelleştirildi");
                                                        }else{
                                                            Log.e("Refresh","Veriler güncellenirken bir hata oluştu");
                                                        }
                                                    }else{
                                                        Log.e("Refresh","Veriler güncellenirken bir hata oluştu");
                                                    }
                                                }else{
                                                    Log.e("Refresh","Veriler güncellenirken bir hata oluştu");
                                                }
                                            }

                                            @Override
                                            public void onFailure ( int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                                                Log.e("Refresh","Veriler güncellenirken bir hata oluştu");
                                            }


                                            @Override
                                            public void onFinish () {
                                                super.onFinish();
                                            }

                                            @Override
                                            public boolean getUseSynchronousMode() {
                                                return false;
                                            }
                                        });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    handler.post(runnable);


                    try {
                        sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        refreshThread.start();
    }

    private void GetTracking() {
        try {
            Thread routeThread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            DriverModel driverModel = GetDriver();
                            if (driverModel != null){
                                for (int i = 0;i<driverModel.getRouteList().size();i++){
                                    RouteModel routeModel = driverModel.getRouteList().get(i);


                                    Log.e("Start Time",routeModel.getStartTime());
                                    if (routeModel.getSchoolExitTime() != null){
                                        Log.e("SchoolExitTime",routeModel.getSchoolExitTime());
                                    }
                                    Log.e("Current Time",getCurrentTime());
                                    if (routeModel.getRouteType() == 1){
                                        if (routeModel.getSchoolExitTime() != null){
                                            if (routeModel.getSchoolExitTime().equals(getCurrentTime())){
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                SharedPreferences.Editor editor = preferences.edit();

                                                if (routeModel.getRouteMovement() == null){
                                                    Intent intent = new Intent(TrackingService.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("StartType",2);
                                                    intent.putExtra("RouteId",routeModel.getId());
                                                    intent.putExtra("RouteStatus",-1);
                                                    TrackingService.this.startActivity(intent);

                                                    RouteMovementModel model = new RouteMovementModel();
                                                    model.setRouteId(routeModel.getId());
                                                    model.setStatus(-1);
                                                    model.setSchoolExitStatus(0);

                                                    routeModel.setRouteMovement(model);
                                                    SaveDriver(driverModel);
                                                }else{
                                                    if (routeModel.getRouteMovement().getSchoolExitStatus() ==-1){
                                                        Intent intent = new Intent(TrackingService.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.putExtra("StartType",2);
                                                        intent.putExtra("RouteId",routeModel.getId());
                                                        intent.putExtra("RouteStatus",-1);
                                                        TrackingService.this.startActivity(intent);

                                                        routeModel.getRouteMovement().setSchoolExitStatus(0);
                                                        SaveDriver(driverModel);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (routeModel.getStartTime().equals(getCurrentTime())){
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = preferences.edit();

                                        if (routeModel.getRouteMovement() == null){
                                            Intent intent = new Intent(TrackingService.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("StartType",1);
                                            intent.putExtra("RouteId",routeModel.getId());
                                            intent.putExtra("RouteStatus",-1);
                                            TrackingService.this.startActivity(intent);

                                            RouteMovementModel model = new RouteMovementModel();
                                            model.setRouteId(routeModel.getId());
                                            model.setStatus(0);
                                            model.setSchoolExitStatus(-1);

                                            routeModel.setRouteMovement(model);
                                            SaveDriver(driverModel);
                                        }else{
                                            Log.e("Status",routeModel.getRouteMovement().getStatus()+"");
                                            if (routeModel.getRouteMovement().getStatus() == -1){
                                                Intent intent = new Intent(TrackingService.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("StartType",1);
                                                intent.putExtra("RouteId",routeModel.getId());
                                                intent.putExtra("RouteStatus",0);
                                                TrackingService.this.startActivity(intent);

                                                routeModel.getRouteMovement().setStatus(0);

                                                SaveDriver(driverModel);

                                            }
                                        }
                                    }
                                }
                            }

                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            routeThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String GetDeviceId() {
        return android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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

    public DriverModel GetDriver() {
        driverModelBaseEntity = new BaseEntity<>(getBaseContext(),DriverModel.class);
        routeModelBaseEntity = new BaseEntity<>(getBaseContext(),RouteModel.class);

        if (driverModelBaseEntity.readAll() == null)
            return null;

        if (driverModelBaseEntity.readAll().size() == 0) {
            return null;
        }

        DriverModel driverModel = driverModelBaseEntity.readAll().get(0);
        driverModel.setRouteList(routeModelBaseEntity.readAll());

        return driverModel;
    }

    public boolean SaveDriver(DriverModel driverModel) {
        driverModelBaseEntity = new BaseEntity<>(getBaseContext(),DriverModel.class);
        routeModelBaseEntity = new BaseEntity<>(getBaseContext(),RouteModel.class);

        if (driverModelBaseEntity.exists(driverModel.getId())){
            if (driverModelBaseEntity.update(driverModel)){
                routeModelBaseEntity.deleteAll(RouteModel.class);

                for(int i = 0; i< driverModel.getRouteList().size();i++){
                    routeModelBaseEntity.create(driverModel.getRouteList().get(i));
                }
            }

            return  true;
        }else{
            if (driverModelBaseEntity.create(driverModel)){
                for(int i = 0; i< driverModel.getRouteList().size();i++){
                    routeModelBaseEntity.create(driverModel.getRouteList().get(i));
                }
            }

            return  true;
        }
    }


}
