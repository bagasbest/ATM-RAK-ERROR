package com.application.m_farek.riwayat_transaksi.data;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityTransactionDetailBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TransactionDetailActivity extends AppCompatActivity {

    //// inisiasi variabel supaya tidak ada error ketika halaman dimuat
    public static final String EXTRA_TRANSACTION = "transaction";
    public static final String EXTRA_OPTION = "option";
    private ActivityTransactionDetailBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// bagian ini berfungsi untuk meload data dari halaman adapter sebelumnya
        TransactionModel model = getIntent().getParcelableExtra(EXTRA_TRANSACTION);
        String option = getIntent().getStringExtra(EXTRA_OPTION);

        /// ini digunakan untuk membuat money currency, supaya ada titik, contoh 100000 -> Rp.100.000
        NumberFormat formatter = new DecimalFormat("#,###");


        /// jika kita membuka halaman tarik tunai, maka akan muncul detail transaksi khusus tarik tunai
        if(option.equals("withdraw")) {
            binding.title.setText("Detail Tarik Tunai");
            binding.name.setText(model.getName());
            binding.transactionId.setText(model.getTransactionId());
            binding.rekening.setText(model.getRekening());
            binding.nominal.setText("Rp." + formatter.format(model.getNominal()));
            binding.date.setText(model.getDate());
        } else {
            /// jika kita membuka transfer, maka akan muncul detail transaksi khusus transfer
            binding.title.setText("Detail Transfer");
            binding.num1.setVisibility(View.VISIBLE);
            binding.num2.setVisibility(View.VISIBLE);
            binding.rekeningTujuan.setVisibility(View.VISIBLE);
            binding.bank.setVisibility(View.VISIBLE);
            binding.name.setText(model.getName());
            binding.transactionId.setText(model.getTransactionId());
            binding.rekening.setText(model.getUserRekening());
            binding.nominal.setText("Rp." + formatter.format(model.getNominal()));
            binding.date.setText(model.getDate());
            binding.rekeningTujuan.setText(model.getRekening());
            binding.bank.setText(model.getBank());
        }


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}