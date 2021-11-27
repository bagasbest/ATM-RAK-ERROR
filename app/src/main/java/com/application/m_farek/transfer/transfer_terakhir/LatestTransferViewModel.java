package com.application.m_farek.transfer.transfer_terakhir;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.m_farek.transfer.tambah_daftar_baru.NasabahModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class LatestTransferViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS LATEST TRANSFER VIEW MODEL MENGAMBIL DATA DARI COLLECTION "transfer", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA Activity Transfer, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA transfer terakhir

    private final MutableLiveData<ArrayList<NasabahModel>> listNasabah = new MutableLiveData<>();
    final ArrayList<NasabahModel> nasabahModelArrayList = new ArrayList<>();

    private static final String TAG = LatestTransferViewModel.class.getSimpleName();

    public void setLatestTransfer() {
        nasabahModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("transfer")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                NasabahModel model = new NasabahModel();

                                model.setName("" + document.get("name"));
                                model.setBank("" + document.get("bank"));
                                model.setRekening("" + document.get("rekening"));
                                model.setUid("" + document.get("uid"));


                                nasabahModelArrayList.add(model);
                            }
                            listNasabah.postValue(nasabahModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<NasabahModel>> getLastTransfer() {
        return listNasabah;
    }


}
