package com.application.m_farek.tarik_tunai;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.application.m_farek.databinding.ActivityWithdrawBinding;
import com.application.m_farek.homepage.UserModel;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class WithdrawActivity extends AppCompatActivity {


    /// inisiasi variabel
    public static final String EXTRA_USER = "users";
    private ActivityWithdrawBinding binding;
    private long nominal;
    private UserModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// ini merupakan metode untuk mengubah nilai tabungan kita dari 100000000 -> 100.000.000
        NumberFormat formatter = new DecimalFormat("#,###");


        /// meload rekening user dan sumber dana user
        model = getIntent().getParcelableExtra(EXTRA_USER);
        binding.rekening.setText("No.Rekening: " + (model.getRekening()));
        binding.balance.setText("Sumber Dana: Rp. " + formatter.format(model.getBalance()));


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /// Fungsi fungsi di bawah ini merupakan tombol dari 100.000 hingga 1.000.000
        /// dimana user dapat menekan salah satu tombol untuk menarik uang
        /// setiap tombol memiliki angka berbeda

        binding.num1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 100000;
                showConfirmBalance();
            }
        });
        binding.num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 200000;
                showConfirmBalance();
            }
        });
        binding.num3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 300000;
                showConfirmBalance();
            }
        });
        binding.num4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 400000;
                showConfirmBalance();

            }
        });
        binding.num5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 500000;
                showConfirmBalance();
            }
        });
        binding.num6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 600000;
                showConfirmBalance();

            }
        });
        binding.num7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 700000;
                showConfirmBalance();

            }
        });
        binding.num8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 800000;
                showConfirmBalance();

            }
        });
        binding.num9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 900000;
                showConfirmBalance();

            }
        });
        binding.num10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 1000000;
                showConfirmBalance();

            }
        });

    }

    /// fungsi ini lebih ke pengecekan, apakah user memiliki dana yang cukup atau tidak untuk menarik uang
    private void showConfirmBalance() {
        /// jika nominal tidak cukup
        if(nominal <= model.getBalance()) {
            Intent intent = new Intent(WithdrawActivity.this, WithdrawConfirmationActivity.class);
            intent.putExtra(WithdrawConfirmationActivity.EXTRA_USER, model);
            intent.putExtra(WithdrawConfirmationActivity.EXTRA_NOMINAL, nominal);
            startActivity(intent);
        } else {
            Toast.makeText(WithdrawActivity.this, "Mohon maaf, saldo anda tidak mencukupi", Toast.LENGTH_SHORT).show();
        }
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}