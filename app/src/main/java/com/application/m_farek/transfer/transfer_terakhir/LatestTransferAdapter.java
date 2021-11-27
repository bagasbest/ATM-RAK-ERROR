package com.application.m_farek.transfer.transfer_terakhir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.application.m_farek.R;
import com.application.m_farek.transfer.TransferConfirmationActivity;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahAdapter;
import com.application.m_farek.transfer.tambah_daftar_baru.NasabahModel;

import java.util.ArrayList;

public class LatestTransferAdapter extends RecyclerView.Adapter<LatestTransferAdapter.ViewHolder> {


    ///  Apakah Adaptor itu? Adaptor adalah objek dari kelas yang mengimplementasikan Adapter interface.
    /// Ini bertindak sebagai penghubung antara kumpulan data dan tampilan adaptor, objek dari kelas yang memperluas kelas AdapterView abstrak.
    /// Data set mampu menyajikan data apa saja secara terstruktur.

    /// data dari transfer fragment, maupun withdraw fragment akan di hubungkan ke adapter melalui fungsi setData pada baris 35 dibawah
    /// kemudian adapter ini akan membuat list vertikal yang bisa di scroll data transaksinya


    private final ArrayList<NasabahModel> listNasabah = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<NasabahModel> items) {
        listNasabah.clear();
        listNasabah.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LatestTransferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last_transfer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestTransferAdapter.ViewHolder holder, int position) {
        holder.bind(listNasabah.get(position));
    }

    @Override
    public int getItemCount() {
        return listNasabah.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, bank;
        ConstraintLayout cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            bank = itemView.findViewById(R.id.bank);
            cv = itemView.findViewById(R.id.cv);
        }

        public void bind(NasabahModel model) {
            name.setText(model.getName());
            bank.setText(model.getBank());

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), TransferConfirmationActivity.class);
                    intent.putExtra(TransferConfirmationActivity.EXTRA_NASABAH, model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
