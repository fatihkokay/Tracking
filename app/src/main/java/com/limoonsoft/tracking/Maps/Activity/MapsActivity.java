package com.limoonsoft.tracking.Maps.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.polok.routedrawer.model.Routes;
import com.github.polok.routedrawer.model.TravelMode;
import com.github.polok.routedrawer.parser.RouteJsonParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.GsonBuilder;
import com.google.maps.android.SphericalUtil;
import com.limoonsoft.core.RouteLineModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.data.Position;
import com.limoonsoft.service.RestClient;
import com.limoonsoft.tracking.R;
import com.limoonsoft.tracking.Service.Provider.PositionProvider;
import com.limoonsoft.tracking.Service.Route.CustomRouteDrawer;
import com.limoonsoft.tracking.Service.Route.CustomRouteRest;
import com.limoonsoft.tracking.Util.JsonDateDeserializer;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MapsActivity extends FragmentActivity implements PositionProvider.PositionListener {

    private GoogleMap googleMap;
    private CustomRouteRest routeRest;
    private PositionProvider positionProvider;
    private RouteModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.positionProvider = new PositionProvider(MapsActivity.this, this);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);


        final CustomRouteDrawer routeDrawer = new CustomRouteDrawer.RouteDrawerBuilder(googleMap)
                .withColor(Color.BLUE)
                .withWidth(8)
                .withAlpha(0.5f)
                .withMarkerIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .build();


        RestClient restClient = new RestClient(getBaseContext());
        RequestParams params = new RequestParams();
        params.add("DriverId", "1");
        restClient.get("Route/DriverRoute/", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

                Log.e("onStart","Route/DriverRoute/");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Log.e("Route", new String(responseBody));
                    model = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create().fromJson(new String(responseBody), RouteModel.class);
                    if (model == null) {
                        Toast.makeText(getBaseContext(), "Rota tanımınız tanımlanmamış merkez ile irtibata geçiniz.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    /*Öğrenci Lokasyonları*/
                    for (int i=0;i<model.getRouteLineList().size();i++)
                    {
                        RouteLineModel rootLineModel = model.getRouteLineList().get(0);
                        if (rootLineModel.getStudentId() != 0){
                            LatLng location = new LatLng(rootLineModel.getLatitute(), rootLineModel.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(location).title(rootLineModel.getStudent().getNameSurname()));
                        }
                    }

                    /*Başlangıç Noktasına Zoom*/
                    if (model.getRouteLineList().size() > 0) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( model.getRouteLineList().get(0).getLatitute(),  model.getRouteLineList().get(0).getLongitude()),15));
                    }

                    /*Root Draw*/
                    int max = 25 - 1;
                    List<LatLng> waypoints = new ArrayList<>();
                    routeRest = new CustomRouteRest();


                    for (int i = 0; i < model.getRouteLineList().size(); i = i + max) {
                        if (i + (i + max + 1) < model.getRouteLineList().size()) {
                            List<RouteLineModel> routeLineModelList = model.getRouteLineList().subList(i, i + max + 1);
                            Log.e("waypoints 2",routeLineModelList.size()+"");

                            waypoints.clear();
                            for (int x = 0; x < routeLineModelList.size(); x++) {

                                waypoints.add(new LatLng(routeLineModelList.get(x).getLatitute(), routeLineModelList.get(x).getLongitude()));
                            }

                            LatLng start = waypoints.get(0);
                            LatLng stop = waypoints.get(waypoints.size() - 1);

                            waypoints.remove(waypoints.size() - 1);
                            waypoints.remove(0);

                            List<LatLng> temp = new ArrayList<LatLng>();
                            temp.addAll(waypoints);



                            if (temp.size() < 25){
                                Log.e("waypoints 3",temp.size()+"");
                                routeRest.getJsonWaypointsDirections(start, temp, stop, TravelMode.DRIVING)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .map(new Func1<String, Routes>() {
                                            @Override
                                            public Routes call(String s) {
                                                Log.e("json", s);

                                                if (s != null){
                                                    return new RouteJsonParser<Routes>().parse(s, Routes.class);
                                                }

                                                return null;
                                            }
                                        })
                                        .subscribe(new Action1<Routes>() {
                                            @Override
                                            public void call(Routes routes) {
                                                if (routes != null)
                                                    routeDrawer.drawPath(routes);


                                            }
                                        });
                            }
                        } else {
                            List<RouteLineModel> routeLineModelList = model.getRouteLineList().subList(i, model.getRouteLineList().size() - 1);
                            for (int x = 0; x < routeLineModelList.size(); x++) {
                                waypoints.add(new LatLng(routeLineModelList.get(x).getLatitute(), routeLineModelList.get(x).getLongitude()));
                            }

                            if (waypoints.size() > 1) {
                                LatLng start = waypoints.get(0);
                                LatLng stop = waypoints.get(waypoints.size() - 1);

                                if (waypoints.size() > 1) {
                                    waypoints.remove(waypoints.size() - 1);
                                }

                                if (waypoints.size() > 1) {
                                    waypoints.remove(0);
                                }

                                routeRest.getJsonWaypointsDirections(start, waypoints, stop, TravelMode.DRIVING)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .map(new Func1<String, Routes>() {
                                            @Override
                                            public Routes call(String s) {
                                                Log.e("json", s);

                                                return new RouteJsonParser<Routes>().parse(s, Routes.class);
                                            }
                                        })
                                        .subscribe(new Action1<Routes>() {
                                            @Override
                                            public void call(Routes routes) {
                                                if (routes != null)
                                                    routeDrawer.drawPath(routes);


                                            }
                                        });
                            }
                        }
                    }


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Driver RestClient", "Hata oluştu");
            }


            @Override
            public void onFinish() {
                super.onFinish();

            }
        });


    }

    @Override
    public void onPositionUpdate(Position position) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.getLatitude(),position.getLongitude()),20));
        if (model != null){
            for (int i=0;i<model.getRouteLineList().size();i++){
                RouteLineModel line = model.getRouteLineList().get(i);
                if (!line.isStatus()) {
                    double distance = SphericalUtil.computeDistanceBetween(new LatLng(position.getLatitude(),position.getLongitude()),new LatLng(line.getLatitute(),line.getLongitude()));
                    Log.e("computeDistanceBetween",distance+"");


                    if (distance < 3000){
                        Polyline polyline = googleMap.addPolyline(new PolylineOptions().geodesic(true));
                        polyline.setColor(Color.parseColor("#FF2C00"));
                        polyline.setPoints(Arrays.asList(new LatLng(position.getLatitude(),position.getLongitude()),new LatLng(line.getLatitute(),line.getLongitude())));
                        line.setStatus(true);
                    }

                    break;
                }
            }
        }
    }
}