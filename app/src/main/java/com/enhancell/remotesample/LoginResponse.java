package com.enhancell.remotesample;


import android.content.Context;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Asiya Khatib on 15-09-2017.
 */

public class LoginResponse {
    public static final int POST_TASK = 1;
    public static final int GET_TASK = 0;
    // public static final int GET_TASK = 2;

    private static final String TAG = "WebServiceTask";

    // connection timeout, in milliseconds (waiting to connect)
    private static final int CONN_TIMEOUT = 7000 * 60 * 10;

    // socket timeout, in milliseconds (waiting for data)
    private static final int SOCKET_TIMEOUT = 7000 * 60 * 10;


    private int taskType = POST_TASK;
    private Context mContext = null;
    private String processMessage = "Processing...";
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    public LoginResponse(int taskType, Context mContext, String processMessage) {

        this.taskType = taskType;
        this.mContext = mContext;
        this.processMessage = processMessage;
    }

    public void addNameValuePair(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

   /* public String doInBackground(String url) {
        String result = "";
        HttpResponse response = doResponse(url);
        if (response == null) {
            return result;
        } else {
            try {
                result = inputStreamToString(response.getEntity().getContent());
            } catch (IllegalStateException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);

            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
        return result;
    }*/

    // Establish connection and socket (data retrieval) timeouts
    HttpParams getHttpParams() {

        HttpParams htpp = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

        return htpp;
    }

    public String doResponse(String url) {

        // Use our connection and data timeouts as parameters for our
        // DefaultHttpClient
        HttpClient httpclient = new DefaultHttpClient(getHttpParams());
        HttpResponse response = null;
        String responseVal = null;
        try {
            switch (taskType) {

                case POST_TASK:
                    try {
                        HttpPost httppost = new HttpPost(url);
                        Log.v(TAG,"Request object :- "+params);
                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params));
                        response = httpclient.execute(httppost);
                        if (response == null) {
                            responseVal = null;
                        } else {
                            try {
                                responseVal = inputStreamToString(response.getEntity()
                                        .getContent());
                            } catch (IllegalStateException e) {
                                responseVal = null;
                            } catch (IOException e) {
                                responseVal = null;
                            }
                            System.out.println("Result:" + responseVal);
                        }
                    } catch (SocketTimeoutException e) {
                        Log.e(TAG, " SocketTimeoutException exception\n");
                        e.printStackTrace();
                        responseVal = "timeout";
                    } catch (Exception e) {
                        Log.e(TAG, " POST exception\n");
                        e.printStackTrace();
                        responseVal = null;
                    }
                    break;
                case GET_TASK:
                    try {
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        if (response == null) {
                            responseVal = null;
                        } else {
                            try {
                                responseVal = inputStreamToString(response.getEntity()
                                        .getContent());
                            } catch (IllegalStateException e) {
                                responseVal = null;
                            } catch (IOException e) {
                                responseVal = null;
                            }
                            System.out.println("Result:" + responseVal);
                        }
                    } catch (SocketTimeoutException e) {
                        Log.e(TAG, " SocketTimeoutException exception\n");
                        e.printStackTrace();
                        responseVal = "timeout";
                    } catch (Exception e) {
                        responseVal = null;
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return responseVal;
    }

    public String inputStreamToString(InputStream is) {

        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        // Return full string
        return total.toString();
    }
}
