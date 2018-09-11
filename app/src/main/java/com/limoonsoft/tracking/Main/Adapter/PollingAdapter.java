package com.limoonsoft.tracking.Main.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.limoonsoft.core.MovementModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteStudentModel;
import com.limoonsoft.core.SmsModel;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PollingAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private RouteModel routeModel;
    public PollingAdapter(Context context, RouteModel routeModel) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.routeModel = routeModel;
    }

    @Override
    public int getCount() {
        return getStudentList().size();
    }

    @Override
    public RouteStudentModel getItem(int position) {
        return  getStudentList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.layout_polling_item, null);
        ViewHolder holder = new ViewHolder(convertView);
        holder.textViewStudent.setText(getItem(position).getStudent().getName()+" "+getItem(position).getStudent().getSurname());

        holder.buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteStudentModel routeStudentModel = routeModel.getRouteStudentList().get(position);


                String phone = routeStudentModel.getStudent().getPhone();
                phone = phone.replace("(","");
                phone = phone.replace(")","");
                phone = phone.replace(" ","");
                phone = phone.substring(1);

                InCarSchool(routeStudentModel,phone,false);
                InCarSchoolNotification(routeStudentModel,true);

                routeModel.getRouteStudentList().get(position).setStatus(true);
                notifyDataSetChanged();
            }
        });

        holder.buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteStudentModel routeStudentModel = routeModel.getRouteStudentList().get(position);


                String phone = routeStudentModel.getStudent().getPhone();
                phone = phone.replace("(","");
                phone = phone.replace(")","");
                phone = phone.replace(" ","");
                phone = phone.substring(1);

                InCarSchool(routeStudentModel,phone,true);
                InCarSchoolNotification(routeStudentModel,true);


                routeModel.getRouteStudentList().get(position).setStatus(true);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    public static class ViewHolder {
        TextView textViewStudent;
        ImageView imageViewStudent;
        Button buttonYes;
        Button buttonNo;

        public ViewHolder(View view) {
            textViewStudent = (TextView) view.findViewById(R.id.textViewStudent);
            imageViewStudent = (ImageView) view.findViewById(R.id.imageViewStudent);
            buttonYes = (Button)view.findViewById(R.id.buttonYes);
            buttonNo = (Button)view.findViewById(R.id.buttonNo);
        }
    }

    private void InCarSchool(RouteStudentModel routeStudentModel, String phone,boolean InCarSchool) {
        SmsModel smsModel = new SmsModel();
        smsModel.setMessage(karakterCevir(routeStudentModel.getStudent().getNameSurname()+" isimli öğrenciniz okuldan servise bindi."));
        smsModel.setPhone(phone);
        sendSMS(smsModel);

        MovementModel model = new MovementModel();
        model.setRouteId(routeModel.getId());
        model.setStudentId(routeStudentModel.getStudentId());
        model.setInCarSchool(InCarSchool);


        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/InCarSchool", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 0){

                }else{

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    private void InCarSchoolNotification(RouteStudentModel routeStudentModel,boolean InCarSchoolNotification) {
        MovementModel model = new MovementModel();
        model.setRouteId(routeModel.getId());
        model.setStudentId(routeStudentModel.getStudentId());
        model.setInCarSchoolNotification(InCarSchoolNotification);


        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/InCarSchoolNotification", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public static String karakterCevir(String kelime)
    {
        String mesaj = kelime;
        char[] oldValue = new char[] { 'ö', 'Ö', 'ü', 'Ü', 'ç', 'Ç', 'İ', 'ı', 'Ğ', 'ğ', 'Ş', 'ş' };
        char[] newValue = new char[] { 'o', 'O', 'u', 'U', 'c', 'C', 'I', 'i', 'G', 'g', 'S', 's' };
        for (int sayac = 0; sayac < oldValue.length; sayac++)
        {
            mesaj = mesaj.replace(oldValue[sayac], newValue[sayac]);
        }
        return mesaj;
    }

    public void sendSMS(SmsModel smsModel){
        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(smsModel));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Sms/Send", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("Result",new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Result",new String(responseBody));
            }
        });
    }

    private List<RouteStudentModel> getStudentList(){
        List<RouteStudentModel> tempList = new ArrayList<>();

        for (int i = 0; i < routeModel.getRouteStudentList().size(); i++) {
            if (routeModel.getMovementList().size() == 0)
            {
                if (!routeModel.getRouteStudentList().get(i).isStatus())
                    tempList.add(routeModel.getRouteStudentList().get(i));
            }else{
                if (!getMovementStatus(routeModel.getRouteType(),routeModel.getRouteStudentList().get(i).getStudentId())){
                    if (!routeModel.getRouteStudentList().get(i).isStatus())
                        tempList.add(routeModel.getRouteStudentList().get(i));
                }
            }
        }

        return  tempList;
    }

    private boolean getMovementStatus(int RouteType,int StudentId) {
        for (int i = 0; i < routeModel.getMovementList().size(); i++) {
            if (routeModel.getMovementList().get(i).getStudentId() == StudentId) {
                MovementModel movement = routeModel.getMovementList().get(i);

                if (RouteType == 1) {
                    if (movement.getInCarSchoolDate() != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
