package com.application.m_farek.riwayat_transaksi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.application.m_farek.databinding.ActivityTransactionBinding;
import com.google.android.material.tabs.TabLayout;

public class TransactionActivity extends AppCompatActivity {


    /// inisiasi variabel supaya tidak terjadi error
    private ActivityTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// Transaction Activity merupakan halaman yang terdapat Tab view nya
        /// dimana halaman ini pengguna bisa memilih mau melihat riwayat transaksi "tarik tunai" atau riwayat "transfer"
        /// kemudian user bisa klik salah satu item pada list nya, dan menuju ke halaman detail

        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// bagian ini berfungsi untuk membuat Tab View menjadi 2, yaitu TARIK TUNAI dan TRANSFER
        FragmentManager fm = getSupportFragmentManager();
        FragmentAdapter adapter = new FragmentAdapter(fm, getLifecycle());
        binding.viewPager2.setAdapter(adapter);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tarik Tunai"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Transfer"));


        /// bagian ini berfungsi jika kita klik tarik tunai, maka akan muncul data riwayat tarik tunai dan sebaliknya jika transfer
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}