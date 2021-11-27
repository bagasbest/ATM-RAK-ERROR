package com.application.m_farek.riwayat_transaksi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    /// ViewPager akan bertugas menampilkan fragment, lalu TabLayout akan menjadi navigasinya.
    ///Setiap page bisa berisi layout yang berbeda-beda, karena akan ditangani oleh Fragment dan kita bisa mengisinya dengan apapun yang diinginkan.
    //
    //Pada tutorial ini, kita akan membuat tiga halaman dan mengisinya dengan teks Page 1, Page 2, dan Page 3.
    //
    //Sebenarnya terserah kamu mau diisi dengan apa.
    /// disini saya hanya menggunakan 2 page saja yaitu Tarik Tunai, dan Transfer

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1) {
            return new TransferFragment();
        } else {
            return new WithdrawFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
