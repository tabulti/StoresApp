package com.joaopaulodribeiro.storesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joao.paulo.d.ribeiro on 24/02/2018.
 */

public class StoreAddress implements Parcelable {
    public String complemento;
    public String bairro;
    public String numero;
    public String logradouro;

    public StoreAddress(String complemento, String bairro, String numero, String logradouro) {
        this.complemento = complemento;
        this.bairro = bairro;
        this.numero = numero;
        this.logradouro = logradouro;
    }

    private StoreAddress(Parcel in) {
        complemento = in.readString();
        bairro = in.readString();
        numero = in.readString();
        logradouro = in.readString();
    }

    public static final Creator<StoreAddress> CREATOR = new Creator<StoreAddress>() {
        @Override
        public StoreAddress createFromParcel(Parcel in) {
            return new StoreAddress(in);
        }

        @Override
        public StoreAddress[] newArray(int size) {
            return new StoreAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(complemento);
        dest.writeString(bairro);
        dest.writeString(numero);
        dest.writeString(logradouro);
    }
}
