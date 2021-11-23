package com.application.m_farek.riwayat_transaksi.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionModel implements Parcelable {

    private String transactionId;
    private String date;
    private String rekening;
    private String name;
    private long nominal;
    private String userName;
    private String userRekening;
    private String bank;
    private String uid;


    protected TransactionModel(Parcel in) {
        transactionId = in.readString();
        date = in.readString();
        rekening = in.readString();
        name = in.readString();
        nominal = in.readLong();
        userName = in.readString();
        userRekening = in.readString();
        bank = in.readString();
        uid = in.readString();
    }

    public TransactionModel() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(date);
        dest.writeString(rekening);
        dest.writeString(name);
        dest.writeLong(nominal);
        dest.writeString(userName);
        dest.writeString(userRekening);
        dest.writeString(bank);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionModel> CREATOR = new Creator<TransactionModel>() {
        @Override
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNominal() {
        return nominal;
    }

    public void setNominal(long nominal) {
        this.nominal = nominal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRekening() {
        return userRekening;
    }

    public void setUserRekening(String userRekening) {
        this.userRekening = userRekening;
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
