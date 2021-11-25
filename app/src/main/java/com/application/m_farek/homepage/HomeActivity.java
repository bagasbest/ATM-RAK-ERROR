package com.application.m_farek.homepage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityHomeBinding;
import com.application.m_farek.login.LoginActivity;
import com.application.m_farek.riwayat_transaksi.TransactionActivity;
import com.application.m_farek.tarik_tunai.WithdrawActivity;
import com.application.m_farek.transfer.TransferActivity;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private long balance;
    private long pengeluaran;
    private final UserModel model = new UserModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showOnboardingImage();
        getBalance();

        /// user klik tombol logout
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        /// klik sembunyikan nominal
        binding.visibleOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.balance.setVisibility(View.INVISIBLE);
                binding.balanceHidden.setVisibility(View.VISIBLE);
                binding.visibleOn.setVisibility(View.GONE);
                binding.visibleOff.setVisibility(View.VISIBLE);
            }
        });

        /// klik tampilkan nominal
        binding.visibleOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.balance.setVisibility(View.VISIBLE);
                binding.balanceHidden.setVisibility(View.GONE);
                binding.visibleOn.setVisibility(View.VISIBLE);
                binding.visibleOff.setVisibility(View.GONE);
            }
        });


        /// user klik menu tarik tunai
        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!model.isUserBlocked()) {
                    Intent intent = new Intent(HomeActivity.this, WithdrawActivity.class);
                    intent.putExtra(WithdrawActivity.EXTRA_USER, model);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Maaf, rekening anda terblokir karena sudah 3 kali salah menginputkan PIN, silahkan hubungi CS M-FAREK", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /// user klik menu transfer
        binding.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!model.isUserBlocked()) {
                    startActivity(new Intent(HomeActivity.this, TransferActivity.class));
                } else {
                    Toast.makeText(HomeActivity.this, "Maaf, rekening anda terblokir karena sudah 3 kali salah menginputkan PIN, silahkan hubungi CS M-FAREK", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /// user klik menu riwayat transaksi
        binding.cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TransactionActivity.class));
            }
        });
        
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setGaugeBar() {
        double valueBpm = (Double.parseDouble(String.valueOf(pengeluaran)) / Double.parseDouble(String.valueOf(balance))) * 100;

        binding.percentage.setText(String.format("%.0f", valueBpm) + " %");

        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0.0);
        range.setTo(100.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#39A2DB"));
        range2.setFrom(0.0);
        range2.setTo(valueBpm);

        binding.balanceGauge.addRange(range);
        binding.balanceGauge.addRange(range2);
        binding.balanceGauge.setValue(valueBpm);
        binding.balanceGauge.setValueColor(R.color.background);
        binding.balanceGauge.setMinValue(0.0);
        binding.balanceGauge.setMaxValue(100.0);
    }

    private void getBalance() {
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

                        balance = documentSnapshot.getLong("balance");
                        pengeluaran = documentSnapshot.getLong("pengeluaran");

                        binding.balance.setText("Rp. " + formatter.format(balance));
                        binding.pengeluaran.setText("Rp. " + formatter.format(pengeluaran));
                        binding.selisih.setText("Rp. " + formatter.format(100000000 - pengeluaran));

                        model.setBalance(documentSnapshot.getLong("balance"));
                        model.setName("" + documentSnapshot.get("name"));
                        model.setPengeluaran(documentSnapshot.getLong("pengeluaran"));
                        model.setPin("" + documentSnapshot.get("pin"));
                        model.setRekening(documentSnapshot.getLong("rekening"));
                        model.setUid("" + documentSnapshot.get("uid"));
                        model.setUsername("" + documentSnapshot.get("username"));
                        model.setUserBlocked(documentSnapshot.getBoolean("isUserBlocked"));

                        /// set grafik pengeluaran
                        setGaugeBar();
                    }
                });
    }

    /// ketika user klik logout, muncul box dialog yang menyatakan konfirmasi user sebelum logout apakah yakin ? atau tidak
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah anda yakin ingin keluar apliaksi ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // jika yakin logout, sign out dari firebase autentikasi
                    FirebaseAuth.getInstance().signOut();

                    // dan go to login activity
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    /// jika tidak, maka tetap di halaman homepage
                    dialog.dismiss();
                })
                .show();
    }


    /// onboarding merupakan gambar-gambar yang otomatis slide pada halaman utama
    private void showOnboardingImage() {
        final ArrayList<SlideModel> imageList = new ArrayList<>();// Create image list

        imageList.add(new SlideModel(R.drawable.img1, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.img2, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.img3, ScaleTypes.CENTER_CROP));

        binding.imageSlider.setImageList(imageList);

    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}