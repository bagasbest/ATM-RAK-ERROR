package com.application.m_farek.tarik_tunai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityWithdrawBinding;
import com.application.m_farek.homepage.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity {


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

        NumberFormat formatter = new DecimalFormat("#,###");


        model = getIntent().getParcelableExtra(EXTRA_USER);
        binding.rekening.setText("No.Rekening: " + (model.getRekening()));
        binding.balance.setText("Sumber Dana: Rp. " + formatter.format(model.getBalance()));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


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

    private void showConfirmBalance() {
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