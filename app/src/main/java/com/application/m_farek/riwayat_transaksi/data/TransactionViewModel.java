package com.application.m_farek.riwayat_transaksi.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TransactionViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS TRANSACTION VIEW MODEL MENGAMBIL DATA DARI COLLECTION "withdraw dan transfer", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA Fragment Withdraw atau Fragment Transfer, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA transaksi

    private final MutableLiveData<ArrayList<TransactionModel>> listTransaction = new MutableLiveData<>();
    final ArrayList<TransactionModel> transactionModelArrayList = new ArrayList<>();

    private static final String TAG = TransactionViewModel.class.getSimpleName();

    public void setWithdrawList(String uid) {
        transactionModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("withdraw")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                TransactionModel model = new TransactionModel();

                                model.setName("" + document.get("name"));
                                model.setDate("" + document.get("date"));
                                model.setNominal( document.getLong("nominal"));
                                model.setRekening("" + document.get("rekening"));
                                model.setTransactionId("" + document.get("transactionId"));
                                model.setUid("" + document.get("uid"));


                                transactionModelArrayList.add(model);
                            }
                            listTransaction.postValue(transactionModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void setTransferList(String uid) {
        transactionModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("transfer")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                TransactionModel model = new TransactionModel();

                                model.setDate("" + document.get("date"));
                                model.setNominal( document.getLong("nominal"));
                                model.setRekening("" + document.get("rekening"));
                                model.setTransactionId("" + document.get("transactionId"));
                                model.setName("" + document.get("userName"));
                                model.setUserRekening("" + document.get("userRekening"));
                                model.setUid("" + document.get("uid"));
                                model.setBank("" + document.get("bank"));


                                transactionModelArrayList.add(model);
                            }
                            listTransaction.postValue(transactionModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<TransactionModel>> getListTransaction() {
        return listTransaction;
    }

}
