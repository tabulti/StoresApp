package com.joaopaulodribeiro.storesapp.page.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joaopaulodribeiro.storesapp.R;
import com.joaopaulodribeiro.storesapp.model.Store;

import java.util.List;

/**
 * Created by joao.paulo.d.ribeiro on 24/02/2018.
 */

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> {
    private List<Store> stores;
    private AdaptersListeners.NotifyStoreClickedListener listener;

    public StoresAdapter(List<Store> stores, AdaptersListeners.NotifyStoreClickedListener listener) {

        this.stores = stores;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.storeId.setText("ID: " + stores.get(position).id);
        holder.storeName.setText("Name: " + stores.get(position).nome);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView storeId;
        TextView storeName;

        ViewHolder(View itemView) {
            super(itemView);

            storeId = itemView.findViewById(R.id.store_id);
            storeName = itemView.findViewById(R.id.store_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onNotifyStoreClicked(getAdapterPosition());
        }
    }


}
