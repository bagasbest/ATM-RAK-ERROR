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

//    private void formValidation() {
//        String rekening = binding.noRek.getText().toString().trim();
//        String nominal = binding.nominal.getText().toString().trim();
//        String pin = binding.pin.getText().toString().trim();
//
//        /// validasi form inputan page transfer
//        if(rekening.isEmpty()) {
//            Toast.makeText(TransferActivity.this, "Nomor Rekening Tujuan tidak boleh kosong", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (bank == null) {
//            Toast.makeText(TransferActivity.this, "Bank tujuan harus dipilih", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if (nominal.isEmpty()) {
//            Toast.makeText(TransferActivity.this, "Nominal Transfer tidak boleh kosong", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (pin.isEmpty()) {
//            Toast.makeText(TransferActivity.this, "PIN Anda tidak boleh kosong", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (!pin.equals(userPIN)) {
//            Toast.makeText(TransferActivity.this, "PIN Salah!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        /// proses tarik tunai
//        binding.progressBar.setVisibility(View.VISIBLE);
//        transferMoney = Long.parseLong(nominal);
//        String transactionId = "INV-" + System.currentTimeMillis();
//
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
//        String formattedDate = df.format(new Date());
//
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//        Map<String, Object> transfer = new HashMap<>();
//        transfer.put("transactionId", transactionId);
//        transfer.put("date", formattedDate);
//        transfer.put("rekening", rekening);
//        transfer.put("userName", userName);
//        transfer.put("userRekening", userRekening);
//        transfer.put("bank", bank);
//        transfer.put("nominal", transferMoney);
//        transfer.put("uid", uid);
//
//
//        /// membuat catatan transaksi, supaya riwayat penarikan tunai tersimpan
//        FirebaseFirestore
//                .getInstance()
//                .collection("transfer")
//                .document(transactionId)
//                .set(transfer)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            binding.progressBar.setVisibility(View.GONE);
//                            showSuccessDialog();
//                        } else {
//                            binding.progressBar.setVisibility(View.GONE);
//                            showFailureDialog();
//                        }
//                    }
//                });
//
//    }
//
//
//    /// munculkan dialog ketika gagal transfer
//    private void showFailureDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Gagal melakukan transfer")
//                .setMessage("Tampaknya terdapat gangguan pada koneksi internet anda, silahkan coba beberapa saat lagi")
//                .setIcon(R.drawable.ic_baseline_clear_24)
//                .setPositiveButton("OKE", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//                })
//                .show();
//    }
//
//    /// munculkan dialog ketika sukses transfer
//    private void showSuccessDialog() {
//        NumberFormat formatter = new DecimalFormat("#,###");
//        new AlertDialog.Builder(this)
//                .setTitle("Berhasil melakukan transfer")
//                .setMessage("Anda melakukan transfer uang sebesar Rp. " + formatter.format(transferMoney))
//                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
//                .setPositiveButton("OKE", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//                    onBackPressed();
//                })
//                .show();
//    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}