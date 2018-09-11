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

import com.github.polok.routedrawer.model.TravelMode;
import com.github.polok.routedrawer.parser.RouteJsonParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.limoonsoft.core.MovementModel;
import com.limoonsoft.core.RouteLineModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteMovementModel;
import com.limoonsoft.core.RouteStudentModel;
import com.limoonsoft.core.SmsModel;
import com.limoonsoft.data.DistanceModel;
import com.limoonsoft.data.Position;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.R;
import com.limoonsoft.tracking.Service.Provider.PositionProvider;
import com.limoonsoft.tracking.Service.Route.CustomRouteRest;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class CallNotificationAdapter extends BaseAdapter implements PositionProvider.PositionListener {

    private final LayoutInflater inflater;
    private final Context context;
    private final PositionProvider positionProvider;
    private RouteModel routeModel;
    private boolean SchoolNotification = false;

    public CallNotificationAdapter(Context context, RouteModel routeModel) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.routeModel = routeModel;
        this.positionProvider = new PositionProvider(context, this);
    }

    @Override
    public int getCount() {
        return getStudentList().size();
    }

    @Override
    public RouteStudentModel getItem(int position) {
        return getStudentList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final RouteStudentModel model = getItem(position);

            convertView = inflater.inflate(R.layout.layout_call_notification_item, null);
            ViewHolder holder = new ViewHolder(convertView);
            holder.textViewStudent.setText(getItem(position).getStudent().getName()+" "+getItem(position).getStudent().getSurname());


            if (routeModel.getRouteType() == 0){
                holder.buttonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String phone = model.getStudent().getPhone();
                            phone = phone.replace("(","");
                            phone = phone.replace(")","");
                            phone = phone.replace(" ","");
                            phone = phone.substring(1);

                            SmsModel smsModel = new SmsModel();
                            smsModel.setPhone(phone);
                            smsModel.setMessage(karakterCevir(model.getStudent().getNameSurname()+" isimli ögrenciniz servise bindi."));

                            sendSMS(smsModel);

                            Toast.makeText(context,"Öğrencinin velisine sms gönderildi",Toast.LENGTH_LONG).show();

                            MovementModel model = new MovementModel();
                            model.setRouteId(routeModel.getId());
                            model.setStudentId(getItem(position).getStudentId());
                            model.setInCar(true);

                            sendMovementInCar(model);


                            getStudentList().get(position).setStatus(true);
                            notifyDataSetChanged();
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context,"Bilinmeyen bir hata oluştu",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.buttonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String phone = model.getStudent().getPhone();
                            phone = phone.replace("(","");
                            phone = phone.replace(")","");
                            phone = phone.replace(" ","");
                            phone = phone.substring(1);

                            SmsModel smsModel = new SmsModel();
                            smsModel.setPhone(phone);
                            smsModel.setMessage(karakterCevir(model.getStudent().getNameSurname()+" isimli öğrenciniz servise binmedi."));

                            sendSMS(smsModel);

                            Toast.makeText(context,"Öğrencinin velisine sms gönderildi",Toast.LENGTH_LONG).show();

                            MovementModel model = new MovementModel();
                            model.setRouteId(routeModel.getId());
                            model.setStudentId(getItem(position).getStudentId());
                            model.setInCar(false);

                            sendMovementInCar(model);

                            getStudentList().get(position).setStatus(true);
                            notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context,"Bilinmeyen bir hata oluştu",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                holder.buttonYes.setText("İndi");
                holder.buttonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String phone = model.getStudent().getPhone();
                            phone = phone.replace("(","");
                            phone = phone.replace(")","");
                            phone = phone.replace(" ","");
                            phone = phone.substring(1);

                            SmsModel smsModel = new SmsModel();
                            smsModel.setPhone(phone);
                            smsModel.setMessage(karakterCevir(model.getStudent().getNameSurname()+" isimli ögrenciniz servisden indi."));

                            sendSMS(smsModel);

                            Toast.makeText(context,"Öğrencinin velisine sms gönderildi",Toast.LENGTH_LONG).show();

                            MovementModel model = new MovementModel();
                            model.setRouteId(routeModel.getId());
                            model.setStudentId(getItem(position).getStudentId());
                            model.setOutCar(true);

                            sendMovementOutCar(model);


                            getStudentList().get(position).setStatus(true);
                            notifyDataSetChanged();
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context,"Bilinmeyen bir hata oluştu",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.buttonNo.setVisibility(View.GONE);
            }



            holder.textViewDistance.setText(model.getDistance());
            holder.textViewCallStatus.setText(model.isCallStatus() ? "ARANDI" :"ARANMADI");
            if (model.isCallStatus()){
                if (model.getCallDate() != null){
                    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    holder.textViewDate.setText(simpleDateFormat.format(model.getCallDate()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return convertView;
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

    @Override
    public void onPositionUpdate(Position position) {
        try {
            Log.e("Location Accuracy",position.getAccuracy()+"");
            Log.e("Location Speed",position.getSpeed()+"");
            Log.e("Location",position.getLatitude()+","+position.getLongitude()+"");

            if (position.getAccuracy() < 15){
                if (getCount() == 0){
                    if (routeModel.getRouteType() == 0){
                        getSchoolNotification(this,position);
                    }
                }else{
                    for (int i=0;i<routeModel.getRouteStudentList().size();i++) {
                        RouteStudentModel routeStudentModel  = routeModel.getRouteStudentList().get(i);
                        if (!routeStudentModel.isStatus()){
                            if (!routeStudentModel.isCallStatus()){
                                RouteLineModel routeLineModel;
                                if (routeModel.getRouteType() == 0){
                                    routeLineModel = getRouteLineStudent(1,routeStudentModel.getStudentId());
                                }else{
                                    routeLineModel = getRouteLineStudent(2,routeStudentModel.getStudentId());
                                }
                                if (routeLineModel != null){

                                    List<LatLng> waypoints = new ArrayList<>();
                                    if (i > 0)
                                        waypoints = GetWaypoints(i);

                                    getStudentDistanceAndNotification(this,position,routeLineModel,routeStudentModel,waypoints);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  List<RouteStudentModel> getStudentList(){
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

                if (RouteType == 0) {
                    if (movement.getInCarDate() != null) {
                        return true;
                    }
                }

                if (RouteType == 1) {
                    if (movement.getInCarDate() != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private List<LatLng> GetWaypoints(int index) {
        List<LatLng> waypoints = new ArrayList<>();

        for (int i=0;i<routeModel.getRouteStudentList().size();i++) {
            if (i < index){
                RouteStudentModel routeStudentModel  = routeModel.getRouteStudentList().get(i);
                if (!routeStudentModel.isStatus()){
                    RouteLineModel routeLineModel;

                    if (routeModel.getRouteType() == 0){
                        routeLineModel = getRouteLineStudent(1,routeStudentModel.getStudentId());
                    }else{
                        routeLineModel = getRouteLineStudent(2,routeStudentModel.getStudentId());
                    }

                    if (routeLineModel != null){
                        waypoints.add(new LatLng(routeLineModel.getLatitute(), routeLineModel.getLongitude()));
                    }
                }
            }
        }

        return waypoints;
    }

    private void  getSchoolNotification(final CallNotificationAdapter callNotificationAdapter, final Position position){
       try {
           if (!SchoolNotification){
               RouteLineModel routeLineModel = getRouteLineSchool();


               LatLng start = new LatLng(position.getLatitude(), position.getLongitude());
               LatLng school = new LatLng(routeLineModel.getLatitute(),routeLineModel.getLongitude());

               CustomRouteRest customRouteRest = new CustomRouteRest();
               customRouteRest.getJsonDistance(start, null, school, TravelMode.DRIVING)
                       .observeOn(AndroidSchedulers.mainThread())
                       .map(new Func1<String, DistanceModel>() {
                           @Override
                           public DistanceModel call(String s) {
                               try {
                                   Log.e("json", s);

                                   if (s != null) {
                                       return new RouteJsonParser<DistanceModel>().parse(s, DistanceModel.class);
                                   }

                                   return null;
                               } catch (Exception e) {
                                   e.printStackTrace();
                                   return null;
                               }
                           }
                       })
                       .subscribe(new Action1<DistanceModel>() {
                           @Override
                           public void call(DistanceModel distanceModel) {
                               if (distanceModel.getStatus().equals("OK")) {
                                   int distance = Integer.parseInt(distanceModel.getRows().get(0).getElements().get(0).getDistance().getValue());
                                   if (distance < 500){
                                       SchoolNotification = true;
                                       for (int i=0;i<routeModel.getRouteStudentList().size();i++) {
                                           RouteStudentModel routeStudentModel = routeModel.getRouteStudentList().get(i);

                                           String phone = routeStudentModel.getStudent().getPhone();
                                           phone = phone.replace("(", "");
                                           phone = phone.replace(")", "");
                                           phone = phone.replace(" ", "");
                                           phone = phone.substring(1);


                                           SmsModel smsModel = new SmsModel();
                                           smsModel.setMessage(karakterCevir(routeStudentModel.getStudent().getNameSurname()+" isimli öğrenciniz okula giriş yapmıştır."));
                                           smsModel.setPhone(phone);
                                           sendSMS(smsModel);
                                       }

                                       RouteMovementChange(routeModel.getId(),1);
                                   }
                               }
                           }
                       });
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void RouteMovementChange(int RouteId, int Status) {

        RouteMovementModel model = new RouteMovementModel();
        model.setRouteId(RouteId);
        model.setStatus(Status);


        RestClient restClient = new RestClient(context);
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

    private void getStudentDistanceAndNotification(final CallNotificationAdapter callNotificationAdapter, final Position position, final RouteLineModel routeLineModel, final RouteStudentModel routeStudentModel,final List<LatLng> waypoints) {
        LatLng start = new LatLng(position.getLatitude(), position.getLongitude());
        LatLng end = new LatLng(routeLineModel.getLatitute(), routeLineModel.getLongitude());

        CustomRouteRest customRouteRest = new CustomRouteRest();
        customRouteRest.getJsonDistance(start, waypoints, end, TravelMode.DRIVING)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, DistanceModel>() {
                    @Override
                    public DistanceModel call(String s) {
                        try {
                            Log.e("json", s);

                            if (s != null) {
                                return new RouteJsonParser<DistanceModel>().parse(s, DistanceModel.class);
                            }

                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .subscribe(new Action1<DistanceModel>() {
                    @Override
                    public void call(DistanceModel distanceModel) {
                        try {
                            if (distanceModel.getStatus().equals("OK")) {
                                routeStudentModel.setDistance(distanceModel.getRows().get(0).getElements().get(0).getDistance().getText() + " /" + distanceModel.getRows().get(0).getElements().get(0).getDuration().getText());
                                callNotificationAdapter.notifyDataSetChanged();
                                if (!routeLineModel.isStatus()) {
                                    int distance = Integer.parseInt(distanceModel.getRows().get(0).getElements().get(0).getDistance().getValue());
                                    if (distance <= Integer.parseInt(routeStudentModel.getDistance())) {
                                        routeStudentModel.setCallStatus(true);
                                        callNotificationAdapter.notifyDataSetChanged();

                                        String phone = routeStudentModel.getStudent().getPhone();
                                        phone = phone.replace("(", "");
                                        phone = phone.replace(")", "");
                                        phone = phone.replace(" ", "");
                                        phone = phone.substring(1);

                                        Log.e("Phone", phone);

                                        if (routeModel.getRouteType() == 0){
                                            InCar(routeStudentModel,phone);
                                        }else{
                                            OutCar(routeStudentModel,phone);
                                        }
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void OutCar(RouteStudentModel routeStudentModel, String phone) {
        SmsModel smsModel = new SmsModel();
        smsModel.setMessage(karakterCevir(routeStudentModel.getStudent().getNameSurname()+" isimli öğrenciniz adresinize yaklaşmaktadır."));
        smsModel.setPhone(phone);
        sendSMS(smsModel);

        MovementModel model = new MovementModel();
        model.setRouteId(routeModel.getId());
        model.setStudentId(routeStudentModel.getStudentId());
        model.setOutCarNotification(true);


        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/OutCarNotification", httpEntity, new AsyncHttpResponseHandler() {
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

    private void InCar(RouteStudentModel routeStudentModel,String phone) {
        SmsModel smsModel = new SmsModel();
        smsModel.setMessage(karakterCevir(routeStudentModel.getStudent().getNameSurname()+" isimli öğrenciniz için servisiniz yaklaşmaktadır."));
        smsModel.setPhone(phone);
        sendAudio(smsModel);
        sendSMS(smsModel);

        MovementModel model = new MovementModel();
        model.setRouteId(routeModel.getId());
        model.setStudentId(routeStudentModel.getStudentId());
        model.setInCarNotification(true);


        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/InCarNotification", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public RouteLineModel getRouteLineStudent(int Type,int StudentId)
    {
        for (int i=0;i<routeModel.getRouteLineList().size();i++) {
            RouteLineModel routeLineModel = routeModel.getRouteLineList().get(i);
            if (routeLineModel.getLineType() == Type){
                if (routeLineModel.getStudentId() == StudentId)
                    return routeLineModel;
            }
        }

        return  null;
    }

    public  RouteLineModel getRouteLineSchool(){
        for (int i=0;i<routeModel.getRouteLineList().size();i++) {
            RouteLineModel routeLineModel = routeModel.getRouteLineList().get(i);
            if (routeLineModel.getLineType() == 3){
                    return routeLineModel;
            }
        }

        return  null;
    }

    public static class ViewHolder {
        TextView textViewStudent;
        TextView textViewCallStatus;
        TextView textViewDistance;
        TextView textViewDate;
        ImageView imageViewStudent;
        Button buttonYes;
        Button buttonNo;

        public ViewHolder(View view) {
            textViewStudent = (TextView) view.findViewById(R.id.textViewStudent);
            textViewCallStatus = (TextView) view.findViewById(R.id.textViewCallStatus);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
            textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
            imageViewStudent = (ImageView) view.findViewById(R.id.imageViewStudent);
            buttonYes = (Button)view.findViewById(R.id.buttonYes);
            buttonNo = (Button)view.findViewById(R.id.buttonNo);
        }
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

    public void sendAudio(SmsModel smsModel){
        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(smsModel));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Sms/SendAudio", httpEntity, new AsyncHttpResponseHandler() {
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

    public void sendMovementInCarNotification(MovementModel model){
        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            model.setInCarNotification(true);
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/InCarNotification", httpEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public void sendMovementInCar(MovementModel model){
        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/InCar", httpEntity, new AsyncHttpResponseHandler() {
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

    public void sendMovementOutCar(MovementModel model){
        RestClient restClient = new RestClient(context);
        HttpEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(new Gson().toJson(model));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restClient.post("Movement/OutCar", httpEntity, new AsyncHttpResponseHandler() {
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
}
