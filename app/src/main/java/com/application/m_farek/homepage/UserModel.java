package com.application.m_farek.homepage;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    /// user model berisi atribut atribut penampung data dari firebase database dari collection users

    private String name;
    private String pin;
    private String uid;
    private String username;
    private long balance;
    private long pengeluaran;
    private long rekening;
    private boolean isUserBlocked;

   public UserModel() {}

    protected UserModel(Parcel in) {
        name = in.readString();
        pin = in.readString();
        uid = in.readString();
        username = in.readString();
        balance = in.readLong();
        pengeluaran = in.readLong();
        rekening = in.readLong();
        isUserBlocked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pin);
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeLong(balance);
        dest.writeLong(pengeluaran);
        dest.writeLong(rekening);
        dest.writeByte((byte) (isUserBlocked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getPengeluaran() {
        return pengeluaran;
    }

    public void setPengeluaran(long pengeluaran) {
        this.pengeluaran = pengeluaran;
    }

    public long getRekening() {
        return rekening;
    }

    public void setRekening(long rekening) {
        this.rekening = rekening;
    }

    public boolean isUserBlocked() {
        return isUserBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        isUserBlocked = userBlocked;
    }
}
