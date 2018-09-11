package com.limoonsoft.tracking.Main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limoonsoft.api.ResultModel;
import com.limoonsoft.core.RouteLineModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteStudentModel;
import com.limoonsoft.data.Position;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.R;
import com.limoonsoft.tracking.Service.Provider.PositionProvider;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class LandingPointAdapter extends BaseAdapter implements PositionProvider.PositionListener {

    private final LayoutInflater inflater;
    private final Context context;
    private RouteModel routeModel;
    private final PositionProvider positionProvider;
    private RestClient restClient;
    private Position location;

    public LandingPointAdapter(Context context, RouteModel routeModel) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.routeModel = routeModel;
        this.restClient = new RestClient(context);
        this.positionProvider = new PositionProvider(context, this);
    }

    @Override
    public int getCount() {
        return routeModel.getRouteStudentList().size();
    }

    @Override
    public RouteStudentModel getItem(int position) {
        return routeModel.getRouteStudentList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.layout_landing_point_item, null);
        ViewHolder holder = new ViewHolder(convertView);
        holder.textViewStudent.setText(getItem(position).getStudent().getName() + " " + getItem(position).getStudent().getSurname());
        holder.buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location.getAccuracy() < 15) {
                    if (location != null) {
                        RouteLineModel model = new RouteLineModel();
                        model.setRouteId(routeModel.getId());
                        model.setLineType(2);
                        model.setStudentId(getItem(position).getStudentId());
                        model.setLatitute(location.getLatitude());
                        model.setLongitude(location.getLongitude());

                        HttpEntity httpEntity = null;
                        try {
                            httpEntity = new StringEntity(new Gson().toJson(model));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        restClient.post("Route/Line", httpEntity, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200) {
                                    String resultJson = new String(responseBody);
                                    ResultModel model = new GsonBuilder().create().fromJson(resultJson, ResultModel.class);
                                    if (model.getStatus() == 1) {
                                        routeModel.getRouteStudentList().remove(position);
                                        notifyDataSetChanged();

                                        Toast.makeText(context, "İniş Noktası Belirlendi.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "İniş Noktası Belirlenirken Hata Oluştu.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, "İniş Noktası Belirlenirken Hata Oluştu.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "İniş Noktası Belirlenirken Hata Oluştu.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Konumunuz alınamadı tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "Konumunuz alınamadı tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                }

            }
        });


        return convertView;
    }

    @Override
    public void onPositionUpdate(Position position) {
        this.location = position;
    }

    public static class ViewHolder {
        TextView textViewStudent;
        ImageView imageViewStudent;
        Button buttonLocation;

        public ViewHolder(View view) {
            textViewStudent = (TextView) view.findViewById(R.id.textViewStudent);
            imageViewStudent = (ImageView) view.findViewById(R.id.imageViewStudent);
            buttonLocation = (Button)view.findViewById(R.id.buttonLocation);
        }
    }
}