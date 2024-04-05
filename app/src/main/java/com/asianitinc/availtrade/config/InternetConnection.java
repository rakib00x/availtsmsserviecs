package com.asianitinc.availtrade.config;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnection
{
    private Context context;

    public InternetConnection(Context context)
    {
        this.context = context;
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null)
                return networkInfo.getState() == NetworkInfo.State.CONNECTED;

        }
        return false;
    }
}
