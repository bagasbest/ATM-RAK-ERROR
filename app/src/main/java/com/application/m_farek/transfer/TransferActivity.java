package com.application.m_farek.transfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityTransferBinding;
import com.application.m_farek.tarik_tunai.WithdrawActivity;
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

public class TransferActivity extends AppCompatActivity {

    private ActivityTransferBinding binding;
    private FirebaseUser user;
    private String userPIN;
    private String userRekening;
    private String userName;
    private long transferMoney;
    private String bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();

        /// ambil pin nasabah, ini digunakan sebagai validator ketika nasabah memasukkan pin, jika pin sama, maka nasabah dapat melakukan transfer
        getPin();


        // filter pilihan bank tujuan
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.bankEt.setAdapter(adapter);
        binding.bankEt.setOnItemClickListener((adapterView, view, i, l) -> {
            bank = binding.bankEt.getText().toString();
        });


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

    }

    private void formValidation() {
        String rekening = binding.noRek.getText().toString().trim();
        String nominal = binding.nominal.getText().toString().trim();
        String pin = binding.pin.getText().toString().trim();

        /// validasi form inputan page transfer
        if(rekening.isEmpty()) {
            Toast.makeText(TransferActivity.this, "Nomor Rekening Tujuan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (bank == null) {
            Toast.makeText(TransferActivity.this, "Bank tujuan harus dipilih", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (nominal.isEmpty()) {
            Toast.makeText(TransferActivity.this, "Nominal Transfer tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (pin.isEmpty()) {
            Toast.makeText(TransferActivity.this, "PIN Anda tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (!pin.equals(userPIN)) {
            Toast.makeText(TransferActivity.this, "PIN Salah!", Toast.LENGTH_SHORT).show();
            return;
        }

        /// proses tarik tunai
        binding.progressBar.setVisibility(View.VISIBLE);
        transferMoney = Long.parseLong(nominal);
        String transactionId = "INV-" + System.currentTimeMillis();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(new Date());


        Map<String, Object> transfer = new HashMap<>();
        transfer.put("transactionId", transactionId);
        transfer.put("date", formattedDate);
        transfer.put("rekening", rekening);
        transfer.put("userName", userName);
        transfer.put("userRekening", userRekening);
        transfer.put("bank", bank);
        transfer.put("nominal", transferMoney);


        /// membuat catatan transaksi, supaya riwayat penarikan tunai tersimpan
        FirebaseFirestore
                .getInstance()
                .collection("transfer")
                .document(transactionId)
                .set(transfer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
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
                        userPIN = "" + documentSnapshot.get("pin");
                        userName = "" + documentSnapshot.get("name");
                        userRekening = "" + documentSnapshot.get("rekening");
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
                .setMessage("Anda melakukan transfer uang sebesar Rp. " + formatter.format(transferMoney))
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