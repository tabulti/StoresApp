package com.joaopaulodribeiro.storesapp.retrofit;

import android.support.annotation.NonNull;

import com.joaopaulodribeiro.storesapp.model.GetStoresResponse;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joao.paulo.d.ribeiro on 24/02/2018.
 */

public class RetrofitImplementation {
    private StoresAppService service;
    private static RetrofitImplementation instance;
    private static final String RETROFIT_BASE_URL = "https://api.myjson.com/bins/";

    public static RetrofitImplementation getInstance() {
        if (instance == null) {
            instance = new RetrofitImplementation();
        }
        return instance;
    }

    public void initRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RETROFIT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(client)
                .build();

        service = retrofit.create(StoresAppService.class);
    }

    public void listStores(final StoresAppService.GetStores handler) {
        Call<GetStoresResponse> res = service.listStores();

        res.enqueue(new Callback<GetStoresResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetStoresResponse> call, @NonNull Response<GetStoresResponse> response) {
                if (response.errorBody() == null && response.body() != null){
                    handler.onGetStores(response.body(), null);
                } else {
                    String err = "Code: " + response.raw().code() + "\n" +
                            "Message: " + response.raw().message();
                    handler.onGetStores(null, new Error(err));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetStoresResponse> call, @NonNull Throwable t) {
                handler.onGetStores(null, new Error(t.toString()));
            }
        });
    }
}
