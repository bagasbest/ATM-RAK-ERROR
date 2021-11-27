package com.application.m_farek.transfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityTransferConfirmationBinding;
import com.application.m_farek.homepage.HomeActivity;
import com.application.m_farek.homepage.UserModel;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransferConfirmationActivity extends AppCompatActivity {

    /// inisiasi variabel supaya tidak terjadi error
    public static final String EXTRA_NASABAH = "nasabah";
    private ActivityTransferConfirmationBinding binding;
    private NasabahModel model;
    private UserModel userModel;
    private long moneyTransfer;
    private int chance = 3;
    private String date;
    private String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// halaman ini merupakan halaman konfirmasi transfer, dimana user memasukkan nominal transfer dan memasukkan pin


        /// inisiasi nama tujuan, rekening tujuan, dan bank tujuan
        model = getIntent().getParcelableExtra(EXTRA_NASABAH);
        binding.name.setText(model.getName());
        binding.bank.setText(model.getBank());
        binding.rekeningTujuan.setText(model.getRekening());


        /// inisiasi nama pengirim, rekening pengirim
        getUserData();


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /// klik tombol transfer dan masukkan pin
        binding.transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPin();
            }
        });
    }

    /// input pin, maksimal 3 kali
    private void inputPin() {
        /// inisiasi layout yang tampil
        Dialog dialog;
        Button btnSubmit;
        EditText etPin;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_pin);
        dialog.setCanceledOnTouchOutside(false);
        btnSubmit = dialog.findViewById(R.id.submit);
        etPin = dialog.findViewById(R.id.pin);

        /// user memasukkan pin, kemudian klik submit, setelah itu dilakukan validasi
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPin = etPin.getText().toString().trim();
                String nominal = binding.nominal.getText().toString().trim();

                /// validasi pin oleh sistem, harus terisi, maksimal salah 3 kali
                /// di cek juga apakah user sudah memasukkan saldo atau saldo mencukupi atau tidak
                if (inputPin.isEmpty()) {
                    Toast.makeText(TransferConfirmationActivity.this, "PIN tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!inputPin.equals(userModel.getPin()) && chance > 0) {
                    chance--;
                    Toast.makeText(TransferConfirmationActivity.this, "PIN Salah!, Kesempatan " + chance + " kali lagi", Toast.LENGTH_SHORT).show();
                    return;
                } else if (chance == 0) {
                    showBlockedUser();
                    return;
                } else if (nominal.isEmpty()) {
                    Toast.makeText(TransferConfirmationActivity.this, "Mohon isi nominal terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Long.parseLong(nominal) > userModel.getBalance()) {
                    Toast.makeText(TransferConfirmationActivity.this, "Maaf, saldo anda tidak mencukupi", Toast.LENGTH_SHORT).show();
                    return;
                }


                /// jika validasi berhasil dilewatim, selanjutnya proses transfer
                ProgressDialog mProgressDialog = new ProgressDialog(TransferConfirmationActivity.this);
                mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                /// menginisiasi id, nominal transfer, tanggal dan waktu
                transactionId = "" + System.currentTimeMillis();
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy | HH:mm:ss", Locale.getDefault());
                date = df.format(new Date());
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                moneyTransfer = Long.parseLong(nominal);


                /// masukkan data - data transaksi kedalam hashmap supaya lebih efisien ketika ingin disimpan ke database
                Map<String, Object> transfer = new HashMap<>();
                transfer.put("transactionId", transactionId);
                transfer.put("date", date);
                transfer.put("rekening", model.getRekening());
                transfer.put("bank", model.getBank());
                transfer.put("name", model.getName());
                transfer.put("userName", userModel.getName());
                transfer.put("userRekening", userModel.getRekening());
                transfer.put("nominal", moneyTransfer);
                transfer.put("uid", uid);


                /// membuat catatan transaksi, supaya riwayat penarikan tunai tersimpan
                /// simpan transfer kedalam database
                FirebaseFirestore
                        .getInstance()
                        .collection("transfer")
                        .document(transactionId)
                        .set(transfer)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    /// update jumlah tabungan setelah dipotong transfer
                                    Map<String, Object> balance = new HashMap<>();
                                    balance.put("balance", userModel.getBalance() - Long.parseLong(nominal));
                                    balance.put("pengeluaran", userModel.getPengeluaran() + Long.parseLong(nominal));

                                    /// simpan update an kedalam database
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("users")
                                            .document(userModel.getUid())
                                            .update(balance)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        /// jika sukses, maka akn muncul dialog sukses transfer
                                                        mProgressDialog.dismiss();
                                                        dialog.dismiss();
                                                        showSuccessDialog();
                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        dialog.dismiss();
                                                        showFailureDialog();
                                                    }
                                                }
                                            });
                                } else {
                                    mProgressDialog.dismiss();
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


    /// fungsi untuk memblokir user yang lebih dari 3 kali salah
    private void showBlockedUser() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.getUid())
                .update("isUserBlocked", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            /// menampilkan alert dialog rekening anda terblokir
                            new AlertDialog.Builder(TransferConfirmationActivity.this)
                                    .setTitle("Rekening Anda Terblokir")
                                    .setMessage("Maaf, rekening anda terblokir karena sudah 3 kali salah menginputkan PIN, silahkan hubungi CS M-FAREK")
                                    .setIcon(R.drawable.ic_baseline_warning_24)
                                    .setPositiveButton("OKE", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        dialogInterface.dismiss();
                                        startActivity(intent);
                                        finish();
                                    })
                                    .show();
                        }
                    }
                });
    }

    /// mendapatkan data diri user, untuk ditampilkan pada halaman ini
    private void getUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        NumberFormat formatter = new DecimalFormat("#,###");


                        /// ambil data user yang diperlukan, dan masukkan kedalam model, supaya efisien ketika ingin digunakan
                        userModel = new UserModel();
                        userModel.setBalance(documentSnapshot.getLong("balance"));
                        userModel.setPengeluaran(documentSnapshot.getLong("pengeluaran"));
                        userModel.setName("" + documentSnapshot.get("name"));
                        userModel.setPin("" + documentSnapshot.get("pin"));
                        userModel.setRekening(documentSnapshot.getLong("rekening"));
                        userModel.setUid("" + documentSnapshot.get("uid"));


                        /// set nomor rekening user dan total tabungan user
                        binding.rekening.setText("No.Rekening: " + userModel.getRekening());
                        binding.balance.setText("Total Tabungan: Rp." + formatter.format(userModel.getBalance()));

                    }
                });
    }



    /// munculkan dialog ketika gagal transfer
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan transfer")
                .setMessage("Tampaknya terdapat gangguan pada koneksi internet anda, silahkan coba beberapa saat lagi")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// munculkan dialog ketika sukses transfer
    private void showSuccessDialog() {
        NumberFormat formatter = new DecimalFormat("#,###");
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan transfer")
                .setMessage("Anda melakukan transfer uang sebesar Rp. " + formatter.format(moneyTransfer))
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    /// jika transfer berhasil, maka ke halaman transfer result
                    Intent intent = new Intent(TransferConfirmationActivity.this, TransferResultActivity.class);
                    intent.putExtra(TransferResultActivity.EXTRA_USER, userModel);
                    intent.putExtra(TransferResultActivity.EXTRA_NOMINAL, binding.nominal.getText().toString().trim());
                    intent.putExtra(TransferResultActivity.EXTRA_NASABAH, model);
                    intent.putExtra(TransferResultActivity.TRANSACTION_ID, transactionId);
                    intent.putExtra(TransferResultActivity.DATE, date);
                    startActivity(intent);

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