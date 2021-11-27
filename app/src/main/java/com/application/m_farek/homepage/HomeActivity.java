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

    /// inisiasi variable supaya aplikasi tidak error ketika dijalankan
    private ActivityHomeBinding binding;
    private long balance;
    private long pengeluaran;
    private final UserModel model = new UserModel();


    /// fungsi yang akan bekerja ketika halaman sedang di muat
    @Override
    protected void onResume() {
        super.onResume();
        /// fungsi untuk memperoleh berapa tabungan user saat ini
        getBalance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// fungsi untuk menampilkan slide-show image pada halaman utama
        showOnboardingImage();

        /// user klik tombol logout
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        /// klik sembunyikan nominal, hide tabungan
        binding.visibleOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.balance.setVisibility(View.INVISIBLE);
                binding.balanceHidden.setVisibility(View.VISIBLE);
                binding.visibleOn.setVisibility(View.GONE);
                binding.visibleOff.setVisibility(View.VISIBLE);
            }
        });

        /// klik tampilkan nominal, show tabungan
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
        /// akan di cek dahulu, apakah user sedang terblokir atau tidak,
        /// jika user terblokir, maka tidak bisa masuk ke halaman tarik tunai
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
        /// akan di cek dahulu, apakah user sedang terblokir atau tidak,
        /// jika user terblokir, maka tidak bisa masuk ke halaman transfer
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

        /// user klik menu riwayat transaksi,
        /// masuk ke halaman riwayat transaksi
        binding.cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TransactionActivity.class));
            }
        });
        
    }

    /// fungsi untuk menampilkan grafik pada halaman utama yang memunculkan pemasukan, pengeluaran dan selisih
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setGaugeBar() {

        /// bagian ini untuk menampilkan persentase 1 - 100% pengeluaran
        double percentageValue;
        if(model.getBalance() > model.getPengeluaran()) {
            percentageValue = (Double.parseDouble(String.valueOf(pengeluaran)) / Double.parseDouble(String.valueOf(balance))) * 100;
        } else {
            percentageValue = 100.0;
        }
        binding.percentage.setText(String.format("%.0f", percentageValue) + " %");


        /// bagian ini untuk memberi warna pada grafik
        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0.0);
        range.setTo(100.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#39A2DB"));
        range2.setFrom(0.0);
        range2.setTo(percentageValue);


        /// bagian ini untuk memberi batasan persentase pada grafik
        /// dimana batasannya adalah 0 - 100% pengeluaran
        binding.balanceGauge.addRange(range);
        binding.balanceGauge.addRange(range2);
        binding.balanceGauge.setValue(percentageValue);
        binding.balanceGauge.setValueColor(R.color.background);
        binding.balanceGauge.setMinValue(0.0);
        binding.balanceGauge.setMaxValue(100.0);
    }


    ///  fungsi untuk mendapatkan total tabungan dari database user
    private void getBalance() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /// mula mula sistem akan mendapatkan data user yang sedang login
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

                        /// dimana data ini memuat: tabungan, pengeluaran, nama pengguna, email, pin, rekening, dsb;
                        balance = documentSnapshot.getLong("balance");
                        pengeluaran = documentSnapshot.getLong("pengeluaran");
                        binding.balance.setText("Rp. " + formatter.format(balance));
                        binding.pengeluaran.setText("Rp. " + formatter.format(pengeluaran));
                        binding.selisih.setText("Rp. " + formatter.format(100000000 - pengeluaran));


                        /// kemudian masukkan data - data ini kedalam model, supaya data dapat digunakan secara efisien
                        model.setBalance(documentSnapshot.getLong("balance"));
                        model.setName("" + documentSnapshot.get("name"));
                        model.setPengeluaran(documentSnapshot.getLong("pengeluaran"));
                        model.setPin("" + documentSnapshot.get("pin"));
                        model.setRekening(documentSnapshot.getLong("rekening"));
                        model.setUid("" + documentSnapshot.get("uid"));
                        model.setUsername("" + documentSnapshot.get("username"));
                        model.setUserBlocked(documentSnapshot.getBoolean("isUserBlocked"));

                        /// setelah mendapatkan data dan memasukkannya kedalam model, selanjutnya merupakan inisiasi fungsi untuk mendapatkan persentase dan grafik pengeluaran
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