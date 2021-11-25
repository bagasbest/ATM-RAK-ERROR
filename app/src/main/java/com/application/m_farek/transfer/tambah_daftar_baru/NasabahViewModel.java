package com.application.m_farek.transfer.tambah_daftar_baru;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NasabahViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS NASABAH VIEW MODEL MENGAMBIL DATA DARI COLLECTION "nasabah", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA Activity Transfer, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA nasabah

    private final MutableLiveData<ArrayList<NasabahModel>> listNasabah = new MutableLiveData<>();
    final ArrayList<NasabahModel> nasabahModelArrayList = new ArrayList<>();

    private static final String TAG = NasabahViewModel.class.getSimpleName();

    public void setListNasabah() {
        nasabahModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("nasabah")
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


    public LiveData<ArrayList<NasabahModel>> getListNasabah() {
        return listNasabah;
    }


}
