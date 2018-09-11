package com.limoonsoft.tracking.Service.Route;

import android.util.Log;

import com.github.polok.routedrawer.RouteApi;
import com.github.polok.routedrawer.model.TravelMode;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

public class CustomRouteRest implements RouteApi {

    private OkHttpClient client;

    public CustomRouteRest(OkHttpClient client) {
        this.client = client;
    }

    public CustomRouteRest() {
        this.client = new OkHttpClient();
    }

    public Observable<String> getJsonWaypointsDirections(final LatLng start, final List<LatLng> waypoints, final LatLng end, final TravelMode mode) {
        Log.e("waypoints",waypoints.size()+"");


        if (waypoints.size() == 23){
            Func0<String> resultFunc = new Func0<String>() {
                @Override
                public String call() {
                    try {
                        return getJSONDirection(start,waypoints, end, mode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "";
                }
            };

            return Async.start(resultFunc, Schedulers.io());
        }else{
            Func0<String> resultFunc = new Func0<String>() {
                @Override
                public String call() {
                    return "";
                }
            };

            return Async.start(resultFunc, Schedulers.io());
        }
    }

    private String getJSONDirection(LatLng start, List<LatLng> waypoints, LatLng end, TravelMode mode) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/directions/json?";

        /*API KEY*/
        url += "key=AIzaSyA9okOO7kBxOunNpzNZF7wxA9sackylH4Q";


        /*Başlangıç Noktası*/
        url += "&origin=" + start.latitude + "," + start.longitude;

        /*Duruş Noktaları*/
        for (int i = 0; i < waypoints.size(); i++) {
           if (waypoints.get(i) != null){
               if (i == 0) {
                   url += "&waypoints=" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude;
               } else {
                   url += URLEncoder.encode("|" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude,"UTF-8");
               }
           }
        }

        /*Bitiş Noktası*/
        url += "&destination=" + end.latitude + "," + end.longitude;

        /*Sensör*/
        url += "&sensor=false";

        /*Units*/
        url += "&units=metric";

        /*Mode*/
        url += "&mode=" + mode.name().toLowerCase();



        Log.e("Url", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String getJSONDistanceMatrix(LatLng start, List<LatLng> waypoints, LatLng end, TravelMode mode) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?";

        /*API KEY*/
        url += "key=AIzaSyA9okOO7kBxOunNpzNZF7wxA9sackylH4Q";


        /*Başlangıç Noktası*/
        url += "&origins=" + start.latitude + "," + start.longitude;

        /*Duruş Noktaları*/
        if (waypoints != null) {
            for (int i = 0; i < waypoints.size(); i++) {
                if (waypoints.get(i) != null){
                    if (i == 0) {
                        url += "&waypoints=" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude;
                    } else {
                        url += URLEncoder.encode("|" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude,"UTF-8");
                    }
                }
            }
        }

        /*Bitiş Noktası*/
        url += "&destinations=" + end.latitude + "," + end.longitude;


        /*Units*/
        url += "&units=metric";

        /*Language*/
        url += "&language=tr-TR";

        Log.e("Url", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public Observable<String> getJsonDistance(final LatLng start, final List<LatLng> waypoints, final LatLng end, final TravelMode mode) {
        Func0<String> resultFunc = new Func0<String>() {
            @Override
            public String call() {
                try {
                    return getJSONDistanceMatrix(start,waypoints, end, mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
        };

        return Async.start(resultFunc, Schedulers.io());
    }

    @Override
    public Observable<String> getJsonDirections(LatLng start, LatLng end, TravelMode mode) {
        return null;
    }
}
