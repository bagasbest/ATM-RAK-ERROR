package com.application.m_farek.transfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.application.m_farek.databinding.ActivityTransferBinding;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahAdapter;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahViewModel;
import com.application.m_farek.transfer.tambah_daftar_baru.TransferNewUserActivity;
public class TransferActivity extends AppCompatActivity {

    private ActivityTransferBinding binding;
    private NasabahAdapter nasabahAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerViewNasabah();
        initViewModelNasabah();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransferActivity.this, TransferNewUserActivity.class));
            }
        });

    }



    private void initRecyclerViewNasabah() {
        binding.rvListUserTransfer.setLayoutManager(new LinearLayoutManager(this));
        nasabahAdapter = new NasabahAdapter();
        binding.rvListUserTransfer.setAdapter(nasabahAdapter);
    }

    private void initViewModelNasabah() {
        NasabahViewModel viewModel = new ViewModelProvider(this).get(NasabahViewModel.class);

        binding.progressBarNasabah.setVisibility(View.VISIBLE);
        viewModel.setListNasabah();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}