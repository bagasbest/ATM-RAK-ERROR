package com.application.m_farek.transfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.application.m_farek.databinding.ActivityTransferBinding;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahAdapter;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahViewModel;
import com.application.m_farek.transfer.tambah_daftar_baru.TransferNewUserActivity;
import com.application.m_farek.transfer.transfer_terakhir.LatestTransferAdapter;
import com.application.m_farek.transfer.transfer_terakhir.LatestTransferViewModel;

public class TransferActivity extends AppCompatActivity {

    /// inisiasi variabel
    private ActivityTransferBinding binding;
    private NasabahAdapter nasabahAdapter;
    private LatestTransferAdapter latestTransferAdapter;


    /// fungsi yang akan bekerja ketika halaman di muat
    @Override
    protected void onResume() {
        super.onResume();
        // kedua fungsi ini berfungsi untuk mendapatkan list nasabah yang sudah ditambahkan user, dari database kemudian ditampilkan dalam bentuk list
        initRecyclerViewNasabah();
        initViewModelNasabah("all");


        // kedua fungsi ini berfungsi untuk mendapatkan list transfer terakhir, dari database kemudian ditampilkan dalam bentuk list
        initRecyclerViewLatest();
        initViewModelLatest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// halaman ini merupakan halaman awal menu transfer
        /// user dapat menambahkan daftar transfer baru atau menekan salah satu daftar transfer

        /// user dapat mencari dafar transfer yang diinginkan
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()) {
                    /// all itu berarti semua nasabah akan tampil, ini hanya jika kolom pencarian kosong
                    initViewModelNasabah("all");
                } else {
                    initViewModelNasabah(editable.toString().toLowerCase());
                }
                initRecyclerViewNasabah();

            }
        });

        /// kembali ke halamna sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /// menambahkan daftar nasabah baru
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransferActivity.this, TransferNewUserActivity.class));
            }
        });

    }


    /// ini merupakan fungsi untuk membuat list dari data yang diperoleh dari database
    private void initRecyclerViewNasabah() {
        binding.rvListUserTransfer.setLayoutManager(new LinearLayoutManager(this));
        nasabahAdapter = new NasabahAdapter();
        binding.rvListUserTransfer.setAdapter(nasabahAdapter);
    }

    /// ini merupakan fungsi untuk mengambil data nasabah dari database
    private void initViewModelNasabah(String query) {
        NasabahViewModel viewModel = new ViewModelProvider(this).get(NasabahViewModel.class);
        binding.progressBarNasabah.setVisibility(View.VISIBLE);
        if(query.equals("all")) {
            /// all itu berarti semua data nasabah diambil
            viewModel.setListNasabah();
        } else {
            /// sedangkan query itu berarti, nama nasabah sesuai pencarian saja
            viewModel.setListNasabahByName(query);
        }
        /// observasi data pada database dan tampilkan di halaman ini
        viewModel.getListNasabah().observe(this, nasabahList -> {
            if (nasabahList.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                nasabahAdapter.setData(nasabahList);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBarNasabah.setVisibility(View.GONE);
        });
    }

    /// ini merupakan fungsi untuk membuat daftar transfe terakhir, berbentuk list dari data yang diperoleh dari database
    private void initRecyclerViewLatest() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        binding.rvLastTransfer.setLayoutManager(layoutManager);
        latestTransferAdapter = new LatestTransferAdapter();
        binding.rvLastTransfer.setAdapter(latestTransferAdapter);
    }

    /// ini merupakan fungsi untuk mengambil data transfer terakhir dari database
    private void initViewModelLatest() {
        LatestTransferViewModel viewModel = new ViewModelProvider(this).get(LatestTransferViewModel.class);

        binding.progressBarLastTransfer.setVisibility(View.VISIBLE);
        viewModel.setLatestTransfer();
        viewModel.getLastTransfer().observe(this, latestTransfer -> {
            if (latestTransfer.size() > 0) {
                binding.noDataLastTransfer.setVisibility(View.GONE);
                latestTransferAdapter.setData(latestTransfer);
            } else {
                binding.noDataLastTransfer.setVisibility(View.VISIBLE);
            }
            binding.progressBarLastTransfer.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}