package com.application.m_farek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.application.m_farek.databinding.ActivityMainBinding;
import com.application.m_farek.login.LoginActivity;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// menampilkan logo pada halaman splash screen
        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.logo);


        //// fungsi untuk memberikan delay pada halaman splash screen selama 4 detik sebelum masuk ke menu utama / login
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }, 4000);

    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}