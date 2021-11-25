package com.application.m_farek.transfer.tambah_daftar_baru;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.application.m_farek.R;
import com.application.m_farek.riwayat_transaksi.data.TransactionModel;

import java.util.ArrayList;

public class NasabahAdapter extends RecyclerView.Adapter<NasabahAdapter.ViewHolder> {

    private final ArrayList<NasabahModel> listNasabah = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<NasabahModel> items) {
        listNasabah.clear();
        listNasabah.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_transfer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listNasabah.get(position));
    }

    @Override
    public int getItemCount() {
        return listNasabah.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView bank;
        private final ConstraintLayout cv;

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

                }
            });
        }
    }
}
