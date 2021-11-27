package com.application.m_farek.transfer.tambah_daftar_baru;

import android.os.Parcel;
import android.os.Parcelable;

public class NasabahModel implements Parcelable {

    /// transaction model berisi atribut atribut penampung data dari firebase database collection nasabah


    private String name;
    private String rekening;
    private String bank;
    private String uid;

    public NasabahModel(){}

    protected NasabahModel(Parcel in) {
        name = in.readString();
        rekening = in.readString();
        bank = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(rekening);
        dest.writeString(bank);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NasabahModel> CREATOR = new Creator<NasabahModel>() {
        @Override
        public NasabahModel createFromParcel(Parcel in) {
            return new NasabahModel(in);
        }

        @Override
        public NasabahModel[] newArray(int size) {
            return new NasabahModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
