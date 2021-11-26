package com.application.m_farek.tarik_tunai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityWithdrawResultBinding;
import com.application.m_farek.homepage.HomeActivity;
import com.application.m_farek.homepage.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class WithdrawResultActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_NOMINAL = "nominal";
    private ActivityWithdrawResultBinding binding;
    private UserModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_USER);
        binding.rekening.setText("" + model.getRekening());

        /// generate random 6 angka untuk kode tarik tunai
        ///generate code: 6 digit
        long smallest = 1000_00L; /// minimum 16 digit
        long biggest = 9999_99L; /// maksimum 16 digit
        /// proses mengenerate nomor rekening
        long code = ThreadLocalRandom.current().nextLong(smallest, biggest + 1);
        binding.code.setText("" + code);


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.cancelWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConformationDialog();
            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawMoney(code);
            }
        });

        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Anda menyalin kode tarik tunai", String.valueOf(code));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(WithdrawResultActivity.this, "Anda menyalin kode tarik tunai", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void WithdrawMoney(long code) {
        //proses tarik tunai
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String transactionId = "" + System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy | HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(new Date());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Map<String, Object> withdraw = new HashMap<>();
        withdraw.put("transactionId", transactionId);
        withdraw.put("date", formattedDate);
        withdraw.put("rekening", model.getRekening());
        withdraw.put("name", model.getName());
        withdraw.put("uid", uid);
        withdraw.put("code", code);
        withdraw.put("nominal", getIntent().getLongExtra(EXTRA_NOMINAL, 0));


        /// membuat catatan transaksi, supaya riwayat penarikan tunai tersimpan
        FirebaseFirestore
                .getInstance()
                .collection("withdraw")
                .document(transactionId)
                .set(withdraw)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            /// update balance user
                            Map<String, Object> balance = new HashMap<>();

                            balance.put("balance", model.getBalance() - getIntent().getLongExtra(EXTRA_NOMINAL,0));
                            balance.put("pengeluaran", model.getPengeluaran() + getIntent().getLongExtra(EXTRA_NOMINAL,0));

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("users")
                                    .document(model.getUid())
                                    .update(balance)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                showSuccessDialog();
                                            } else {
                                                mProgressDialog.dismiss();
                                                showFailureDialog();
                                            }
                                        }
                                    });
                        } else {
                            mProgressDialog.dismiss();
                            showFailureDialog();
                        }
                    }
                });
    }

    private void showConformationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Batal Tarik Tunai")
                .setMessage("Apakah anda yakin ingin membatalkan tarik tunai ini?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    Intent intent = new Intent(WithdrawResultActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
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
                .setMessage("Silahkan ambil uang anda sebesar Rp. " + formatter.format(getIntent().getLongExtra(EXTRA_NOMINAL, 0)))
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(WithdrawResultActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}