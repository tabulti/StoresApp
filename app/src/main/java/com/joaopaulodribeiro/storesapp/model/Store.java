package com.joaopaulodribeiro.storesapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by joao.paulo.d.ribeiro on 24/02/2018.
 */

public class Store implements Parcelable{

    public String id;
    public StoreAddress endereco;
    public String nome;
    public String telefone;

    public Store (String id, StoreAddress endereco, String nome, String telefone) {
        this.id = id;
        this.endereco = endereco;
        this. nome = nome;
        this.telefone = telefone;
    }

    private Store(Parcel in) {
        id = in.readString();
        endereco = in.readParcelable(StoreAddress.class.getClassLoader());
        nome = in.readString();
        telefone = in.readString();
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(endereco, flags);
        dest.writeString(nome);
        dest.writeString(telefone);
    }
}
