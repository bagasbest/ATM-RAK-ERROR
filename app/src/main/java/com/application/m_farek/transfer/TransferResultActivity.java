package com.application.m_farek.transfer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.application.m_farek.databinding.ActivityTransferResultBinding;
import com.application.m_farek.homepage.HomeActivity;
import com.application.m_farek.homepage.UserModel;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TransferResultActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_NOMINAL = "nominal";
    public static final String EXTRA_NASABAH = "nasabah";
    public static final String TRANSACTION_ID = "trId";
    public static final String DATE = "date";
    private ActivityTransferResultBinding binding;
    private UserModel userModel;
    private NasabahModel nasabahModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userModel = getIntent().getParcelableExtra(EXTRA_USER);
        nasabahModel = getIntent().getParcelableExtra(EXTRA_NASABAH);

        binding.transactionId.setText(getIntent().getStringExtra(TRANSACTION_ID));
        binding.date.setText(getIntent().getStringExtra(DATE));

        binding.userName.setText(userModel.getName());
        binding.userRekening.setText("" +userModel.getRekening());

        binding.name.setText(nasabahModel.getName());
        binding.rekening.setText(nasabahModel.getRekening());

        NumberFormat formatter = new DecimalFormat("#,###");
        binding.nominal.setText("Rp" + formatter.format(Long.parseLong(getIntent().getStringExtra(EXTRA_NOMINAL))));
        binding.total.setText("Rp" + formatter.format(Long.parseLong(getIntent().getStringExtra(EXTRA_NOMINAL))));


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransferResultActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}