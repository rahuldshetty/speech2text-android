package com.rahuldshetty.speech2text;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.rahuldshetty.speech2text.Utils.getMobileIPAddress;
import static com.rahuldshetty.speech2text.Utils.getWifiIPAddress;

public class ClientInternet {

    String wifiip,mobip;
    Context context;
    Activity activity;

    static String PORT = "3999";
    static String postUrl;

    public ClientInternet(Context context,Activity activity){
        this.context = context;
        this.activity =activity;
        wifiip = getWifiIPAddress(context);
        mobip = getMobileIPAddress();
        System.out.println(wifiip + " " + mobip );

        String ipv4Address = "";

        if(!wifiip.equals(""))
            ipv4Address = wifiip;
        else
            ipv4Address = mobip;

         postUrl= "http://"+ipv4Address+":"+PORT+"/request";
    }

    public static String getPostUrl(String ip){
        return "http://"+ip+":"+PORT+"/request";
    }

    public String getPostUrl(){
        return postUrl;
    }

}
