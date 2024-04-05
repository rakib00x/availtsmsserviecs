package com.asianitinc.availtrade.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(20, TimeUnit.SECONDS) // write timeout
                .readTimeout(20, TimeUnit.SECONDS) // read timeout
                .build();

        if (retrofit == null) {
            String BASE_URL = "https://sms.availtrade.com/";

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }
}
