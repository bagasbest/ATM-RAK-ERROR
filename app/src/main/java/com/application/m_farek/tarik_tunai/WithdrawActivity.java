package com.application.m_farek.tarik_tunai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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


    private ActivityWithdrawBinding binding;
    private FirebaseUser user;
    private String pin;
    private String name;
    private String rekening;
    private long nominal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        /// ambil pin nasabah, ini digunakan sebagai validator ketika nasabah memasukkan pin, jika pin sama, maka nasabah dapat menarik uang
        getPin();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.num1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 500000;
                inputPin();
            }
        });

        binding.num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 1000000;
                inputPin();
            }
        });

        binding.num3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 1500000;
                inputPin();

            }
        });

        binding.num4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nominal = 2000000;
                inputPin();

            }
        });

        binding.num5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getNominal = binding.nominal.getText().toString().trim();
                nominal = Long.parseLong(getNominal);
                inputPin();

            }
        });

    }

    private void getPin() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        pin = "" + documentSnapshot.get("pin");
                        name= "" + documentSnapshot.get("name");
                        rekening = "" + documentSnapshot.get("rekening");
                    }
                });
    }

    private void inputPin() {
        Dialog dialog;
        Button btnSubmit;
        EditText etPin;
        ProgressBar pb;

        dialog = new Dialog(this);

        dialog.setContentView(R.layout.popup_pin);
        dialog.setCanceledOnTouchOutside(false);


        btnSubmit = dialog.findViewById(R.id.submit);
        etPin = dialog.findViewById(R.id.pin);
        pb = dialog.findViewById(R.id.progress_bar);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPin = etPin.getText().toString().trim();

                /// validasi pin oleh sistem
                if(inputPin.isEmpty()) {
                    Toast.makeText(WithdrawActivity.this, "PIN tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!inputPin.equals(pin)) {
                    Toast.makeText(WithdrawActivity.this, "PIN Salah!", Toast.LENGTH_SHORT).show();
                    return;
                }

                /// proses tarik tunai
                pb.setVisibility(View.VISIBLE);
                String transactionId = "INV-" + System.currentTimeMillis();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
                String formattedDate = df.format(new Date());

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                Map<String, Object> withdraw = new HashMap<>();
                withdraw.put("transactionId", transactionId);
                withdraw.put("date", formattedDate);
                withdraw.put("rekening", rekening);
                withdraw.put("name", name);
                withdraw.put("uid", uid);
                withdraw.put("nominal", nominal);


                /// membuat catatan transaksi, supaya riwayat penarikan tunai tersimpan
                FirebaseFirestore
                        .getInstance()
                        .collection("withdraw")
                        .document(transactionId)
                        .set(withdraw)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    pb.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    showSuccessDialog();
                                } else {
                                    pb.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    showFailureDialog();
                                }
                            }
                        });
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    /// munculkan dialog ketika gagal tarik tunai
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan tarik tunai")
                .setMessage("Tampaknya terdapat gangguan pada koneksi internet anda, silahkan coba beberapa saat lagi")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// munculkan dialog ketika sukses tarik tunai
    private void showSuccessDialog() {
        NumberFormat formatter = new DecimalFormat("#,###");
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan tarik tunai")
                .setMessage("Silahkan ambil uang anda sebesar Rp. " + formatter.format(nominal))
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}