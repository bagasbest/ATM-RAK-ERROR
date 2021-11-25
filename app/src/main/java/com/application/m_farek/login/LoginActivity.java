package com.application.m_farek.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityLoginBinding;
import com.application.m_farek.homepage.HomeActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// jalankan fungsi auto login jika user sebelumnya pernah login agar masuk ke homepage tanpa perlu login
        autoLogin();


        /// menampilkan logo pada halaman splash screen
        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.logo);

        /// klik tombol login
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

        /// klik tulisan "Buka rekening"
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /// fungsi auto-login, jika sudah login sebelumnya maka langsung ke homepage tanpa perlu login lagi
    private void autoLogin() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }


    /// sebelum login, sistem perlu mendeteksi tiap kolom email dan password, dan memastikan kolom tersebut terisi
    private void formValidation() {
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }  else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Kata Sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        /// tampilkan loading bar ketika klik login
        binding.progressBar.setVisibility(View.VISIBLE);

        /// mula mula cek dulu username yang dimasukkan user, apakah sudah terdaftar di dalam database collection users atau belum,
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.getResult().size() == 0) {
                            /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                            return;
                        }

                        /// jika terdaftar maka ambil email di database, kemudian lakukan autentikasi menggunakan email & password dari user
                        for(QueryDocumentSnapshot snapshot : task.getResult()) {
                            String email = "" + snapshot.get("email");

                            /// fungsi untuk mengecek, apakah email yang di inputkan ketika login sudah terdaftar di database atau belum
                            FirebaseAuth
                                    .getInstance()
                                    .signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                /// jika terdapat di database dan email serta password sama, maka masuk ke homepage
                                                binding.progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            } else {
                                                /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                                                binding.progressBar.setVisibility(View.GONE);
                                                showFailureDialog();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    /// munculkan dialog ketika gagal login
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan login")
                .setMessage("Silahkan login kembali dengan informasi yang benar")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
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