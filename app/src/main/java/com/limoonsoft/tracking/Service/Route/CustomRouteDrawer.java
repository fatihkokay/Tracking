package com.limoonsoft.tracking.Service.Route;

import android.graphics.Color;

import com.github.polok.routedrawer.DrawerApi;
import com.github.polok.routedrawer.model.Legs;
import com.github.polok.routedrawer.model.Route;
import com.github.polok.routedrawer.model.Routes;
import com.github.polok.routedrawer.model.Step;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class CustomRouteDrawer implements DrawerApi {

    private static final float DEFAULT_MARKER_ALPHA = 1;
    private static final int DEFAULT_PATH_WIDTH = 5;
    private static final int DEFAULT_PATH_COLOR = Color.RED;

    private float alpha;
    private int pathWidth;
    private int pathColor;

    private BitmapDescriptor bitmapDescriptor;

    private GoogleMap googleMap;

    private CustomRouteDrawer(RouteDrawerBuilder builder) {
        this.googleMap = builder.googleMap;

        this.alpha = builder.alpha;
        this.pathWidth = builder.pathWidth;
        this.pathColor = builder.pathColor;
        this.bitmapDescriptor = builder.bitmapDescriptor;
    }

    @Override
    public void drawPath(Routes routes) {
        PolylineOptions lineOptions = null;

        for (Route route : routes.routes) {
            for (Legs legs : route.legs) {
                lineOptions = new PolylineOptions();

                for (Step step : legs.steps) {
                    lineOptions.add(new LatLng(step.startLocation.lat, step.startLocation.lng));
                    lineOptions.add(new LatLng(step.endLocation.lat, step.endLocation.lng));

                    /*googleMap.addMarker(
                            new MarkerOptions()
                                    .alpha(alpha)
                                    .position(new LatLng(step.startLocation.lat, step.startLocation.lng))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    );

                    googleMap.addMarker(
                            new MarkerOptions()
                                    .alpha(alpha)
                                    .position(new LatLng(step.endLocation.lat, step.endLocation.lng))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    );*/

                    lineOptions.width(pathWidth);
                    lineOptions.color(pathColor);

                    googleMap.addPolyline(lineOptions);

                }
            }
        }
    }

    public static class RouteDrawerBuilder {
        private BitmapDescriptor bitmapDescriptor;

        private int pathWidth;
        private int pathColor;
        private float alpha;

        private final GoogleMap googleMap;

        public RouteDrawerBuilder(GoogleMap googleMap) {
            this.googleMap = googleMap;

            this.pathWidth = DEFAULT_PATH_WIDTH;
            this.pathColor = DEFAULT_PATH_COLOR;
            this.alpha = DEFAULT_MARKER_ALPHA;
            this.bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }

        public RouteDrawerBuilder withColor(int pathColor) {
            this.pathColor = pathColor;
            return this;
        }

        public RouteDrawerBuilder withWidth(int pathWidth) {
            this.pathWidth = pathWidth;
            return this;
        }

        public RouteDrawerBuilder withMarkerIcon(BitmapDescriptor bitmapDescriptor) {
            this.bitmapDescriptor = bitmapDescriptor;
            return this;
        }

        public RouteDrawerBuilder withAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public CustomRouteDrawer build() {
            return new CustomRouteDrawer(this);
        }

    }

}