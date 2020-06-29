package com.example.server_client_calls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {
//    private static String BASE_URL= "https://hujipostpc2019.pythonanywhere.com";
    private static String BASE_URL= "http://10.0.0.9:5678";//todo debug
    private static ServerHolder instance = null;

    synchronized static ServerHolder getInstance(){
        if (instance == null) {
            instance = new ServerHolder();
        }
        return instance;
    }

    public final UserService server;

    private ServerHolder(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.server = retrofit.create(UserService.class);
    }
}
