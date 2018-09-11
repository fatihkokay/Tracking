package com.limoonsoft.service;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;


/**
 * Created by Fatih on 03.03.2018.
 */

public class RestClient {
    public Context context;
    private AsyncHttpClient client;

    public RestClient(Context context) {
        this.context = context;
        this.client = new AsyncHttpClient();
        this.client.setTimeout(200 * 1000);
        this.client.setResponseTimeout(200 * 1000);
        this.client.setConnectTimeout(200 * 1000);

    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        String url = "http://94.103.47.11" + "/" + relativeUrl;
        Log.e("url", url);
        return url;

    }
}
