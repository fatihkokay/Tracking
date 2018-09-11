package com.limoonsoft.tracking.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.limoonsoft.api.ResultModel;
import com.limoonsoft.core.DriverModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.data.BaseEntity;
import com.limoonsoft.data.PersistenceManager;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.Main.Activity.DeviceActivity;
import com.limoonsoft.tracking.Main.Activity.MainActivity;
import com.limoonsoft.tracking.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
public class SplashScreenActivity extends AppCompatActivity {

    private TextView textViewLoading;
    private BaseEntity<DriverModel> driverModelBaseEntity;
    private BaseEntity<PersistenceManager.Modal> routeModelBaseEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textViewLoading = (TextView)findViewById(R.id.textViewLoading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Login();
                        }
                    });
                }

            }
        };
        timerThread.start();

    }

    private void Login(){
        try {
            driverModelBaseEntity = new BaseEntity<>(getBaseContext(),DriverModel.class);
            routeModelBaseEntity =new BaseEntity<>(getBaseContext(), RouteModel.class);

            DriverModel driverModel = new DriverModel();
            driverModel.setDevinceId(GetDeviceId());

            RestClient restClient = new RestClient(getBaseContext());
            HttpEntity httpEntity = new StringEntity(new Gson().toJson(driverModel));
            restClient.post("Driver/Login/", httpEntity, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();

                    textViewLoading.setText("Sürücü bilgileri kontrol ediliyor");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200){
                        Log.e("Result",new String(responseBody));

                        ResultModel<DriverModel> model = new Gson().fromJson(new String(responseBody),new TypeToken<ResultModel<DriverModel>>() {}.getType());

                        if (model.getStatus() == 1){
                            DriverModel driverModel = model.getData();
                            if (!driverModelBaseEntity.exists(driverModel.getId())){
                                if (driverModelBaseEntity.create(driverModel)){
                                    for(int i = 0; i< driverModel.getRouteList().size();i++){
                                        routeModelBaseEntity.create(driverModel.getRouteList().get(i));
                                    }

                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    textViewLoading.setText("Sürücü bilgileri kaydedilirken hata oluştu.");
                                }
                            }else{
                                if (driverModelBaseEntity.update(driverModel)){
                                    routeModelBaseEntity.deleteAll(RouteModel.class);

                                    for(int i = 0; i< driverModel.getRouteList().size();i++){
                                        routeModelBaseEntity.create(driverModel.getRouteList().get(i));
                                    }

                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }else{
                            Intent intent = new Intent(getBaseContext(),DeviceActivity.class);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    textViewLoading.setText("Sürücü bilgileri kontrol edilirken hata oluştu.");
                }



                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String GetDeviceId() {
        return android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
