package com.application.m_farek.transfer.tambah_daftar_baru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityTransferNewUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class TransferNewUserActivity extends AppCompatActivity {

    private ActivityTransferNewUserBinding binding;
    private String bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferNewUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // filter pilihan bank tujuan
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.bank.setAdapter(adapter);
        binding.bank.setOnItemClickListener((adapterView, view, i, l) -> {
            bank = binding.bank.getText().toString();
        });


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

    }

    private void formValidation() {
        String name = binding.name.getText().toString().trim();
        String rekening = binding.name.getText().toString().trim();

        if(rekening.isEmpty()) {
            Toast.makeText(TransferNewUserActivity.this, "Nomor Rekening tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.isEmpty()) {
            Toast.makeText(TransferNewUserActivity.this, "Nama nasabah tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (bank == null) {
            Toast.makeText(TransferNewUserActivity.this, "Bank tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        String uid = String.valueOf(System.currentTimeMillis());

        Map<String, Object> transferNewUser = new HashMap<>();
        transferNewUser.put("rekening", rekening);
        transferNewUser.put("name", name);
        transferNewUser.put("bank", bank);
        transferNewUser.put("uid", uid);

        FirebaseFirestore
                .getInstance()
                .collection("nasabah")
                .document(uid)
                .set(transferNewUser)
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

    /// munculkan dialog ketika gagal menambahkan daftar baru
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal menambahkan daftar nasabah")
                .setMessage("Tampaknya terdapat gangguan pada koneksi internet anda, silahkan coba beberapa saat lagi")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// munculkan dialog ketika berhasil menambahkan daftar baru
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil menambahkan daftar nasabah")
                .setMessage("Silahkan lanjutkan proses transfer")
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