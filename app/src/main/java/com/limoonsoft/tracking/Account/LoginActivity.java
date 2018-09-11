package com.limoonsoft.tracking.Account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.limoonsoft.api.ResultModel;
import com.limoonsoft.core.DriverModel;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView textViewUsername;
    private EditText textViewPassword;
    private View loginProgress;
    private View loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        textViewUsername = (AutoCompleteTextView) findViewById(R.id.textViewUsername);
        textViewPassword = (EditText) findViewById(R.id.textViewPassword);
        textViewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if (textViewUsername.getText().length() == 0){
                        Toast.makeText(getBaseContext(),"Kullanıcı adı boş geçilemez!",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (textViewPassword.getText().length() == 0){
                        Toast.makeText(getBaseContext(),"Şifre boş geçilemez!",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    Login(new DriverModel(textViewUsername.getText().toString(),textViewPassword.getText().toString()));
                    return true;
                }
                return false;
            }
        });

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textViewUsername.getText().length() == 0){
                    Toast.makeText(getBaseContext(),"Kullanıcı adı boş geçilemez!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textViewPassword.getText().length() == 0){
                    Toast.makeText(getBaseContext(),"Şifre boş geçilemez!",Toast.LENGTH_SHORT).show();
                    return;
                }


                Login(new DriverModel(textViewUsername.getText().toString(),textViewPassword.getText().toString()));
            }
        });

        loginForm = findViewById(R.id.loginForm);
        loginProgress = findViewById(R.id.loginProgress);
    }

    private void Login(DriverModel model){
        try {
            RestClient restClient = new RestClient(getBaseContext());
            HttpEntity httpEntity = new StringEntity(new Gson().toJson(model));
            restClient.post("Driver/Login/", httpEntity, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    loginProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200){
                        Log.e("Result",new String(responseBody));

                        ResultModel<DriverModel> model = new Gson().fromJson(new String(responseBody),new TypeToken<ResultModel<DriverModel>>() {}.getType());

                        if (model.getStatus() == 1){
                            DriverModel driverModel = model.getData();
                            SaveDriver(driverModel);

                            finish();
                        }else{
                            Toast.makeText(getBaseContext(),model.getErrorMessage(),Toast.LENGTH_LONG).show();
                        }
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

    protected void SaveDriver(DriverModel memberModel) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Driver", new Gson().toJson(memberModel));

        editor.commit();
    }

    protected boolean isDriver() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getString("Driver", "0").equals("0")) {
            return false;
        }

        return true;
    }

    protected DriverModel GetDriver() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return new GsonBuilder().create().fromJson(preferences.getString("Driver", ""), new TypeToken<DriverModel>() {}.getType());
    }
}

