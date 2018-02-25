package com.joaopaulodribeiro.storesapp.page;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.joaopaulodribeiro.storesapp.R;
import com.joaopaulodribeiro.storesapp.model.GetStoresResponse;
import com.joaopaulodribeiro.storesapp.model.Store;
import com.joaopaulodribeiro.storesapp.model.StoreAddress;
import com.joaopaulodribeiro.storesapp.page.adapters.AdaptersListeners;
import com.joaopaulodribeiro.storesapp.page.adapters.StoresAdapter;
import com.joaopaulodribeiro.storesapp.retrofit.RetrofitImplementation;
import com.joaopaulodribeiro.storesapp.retrofit.StoresAppService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StoresAppService.GetStores {

    private RecyclerView mStoresRv;
    private List<Store> mStores;
    private RelativeLayout mLoadingLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AdaptersListeners.NotifyStoreClickedListener mListener = new AdaptersListeners.NotifyStoreClickedListener() {
        @Override
        public void onNotifyStoreClicked(int storeListPosition) {
            Intent intent = new Intent(MainActivity.this, StoreDetailsActivity.class);

            intent.putExtra(StoreDetailsActivity.STORE_ID_BUNDLE_KEY, mStores.get(storeListPosition));

            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingLayout = findViewById(R.id.home_loading_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        RetrofitImplementation.getInstance().initRetrofit();

        setStoresRv();

        getStores();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStores();
            }
        });
    }

    private void getStores() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RetrofitImplementation.getInstance().listStores(this);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setStoresRv() {
        mStoresRv = findViewById(R.id.stores_rv);
        mStoresRv.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mStoresRv.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onGetStores(GetStoresResponse response, Error error) {
        mStores = new ArrayList<>();
        if (error != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.error_service_title_string)
                            .setMessage(R.string.error_service_stores_msg_string)
                            .setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (int i = 0; i <= 5; i++) {
                                        mStores.add(new Store("0" + i,
                                                new StoreAddress("complemento 0" + i,
                                                        "bairro 0" + i,
                                                        "numero 0" + i,
                                                        "logradouro 0" + i),
                                                "nome 0" + i,
                                                "telefone 0" + i));
                                    }
                                    final StoresAdapter adapter = new StoresAdapter(mStores, mListener);
                                    mLoadingLayout.setVisibility(View.GONE);
                                    mStoresRv.setAdapter(adapter);
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            });

        } else if (verifyIfResIsNull(response)) {
            mStores = response.lojas;
            final StoresAdapter adapter = new StoresAdapter(mStores, mListener);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingLayout.setVisibility(View.GONE);
                    mStoresRv.setAdapter(adapter);
                }
            });
        }
    }

    public boolean verifyIfResIsNull(GetStoresResponse res) {
        return res != null && res.lojas != null;
    }

}
