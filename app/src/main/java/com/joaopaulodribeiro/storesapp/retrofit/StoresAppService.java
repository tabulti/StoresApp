package com.joaopaulodribeiro.storesapp.retrofit;

import com.joaopaulodribeiro.storesapp.model.GetStoresResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by joao.paulo.d.ribeiro on 24/02/2018.
 */

public interface StoresAppService {
    interface GetStores {
        void onGetStores(GetStoresResponse response, Error error);
    }

    @GET("hvcbf/")
    Call<GetStoresResponse> listStores();
}
