package com.application.m_farek.riwayat_transaksi.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.m_farek.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    ///  Apakah Adaptor itu? Adaptor adalah objek dari kelas yang mengimplementasikan Adapter interface.
    /// Ini bertindak sebagai penghubung antara kumpulan data dan tampilan adaptor, objek dari kelas yang memperluas kelas AdapterView abstrak.
    /// Data set mampu menyajikan data apa saja secara terstruktur.

    /// data dari transfer fragment, maupun withdraw fragment akan di hubungkan ke adapter melalui fungsi setData pada baris 35 dibawah
    /// kemudian adapter ini akan membuat list vertikal yang bisa di scroll data transaksinya


    private final ArrayList<TransactionModel> listTransaction = new ArrayList<>();
    private final String option;
    public TransactionAdapter(String option) {
        this.option = option;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<TransactionModel> items) {
        listTransaction.clear();
        listTransaction.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listTransaction.get(position), option);
    }

    @Override
    public int getItemCount() {
        return listTransaction.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nominal;
        private TextView transactionId;
        private TextView date;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nominal = itemView.findViewById(R.id.nominal);
            transactionId = itemView.findViewById(R.id.transactionId);
            date = itemView.findViewById(R.id.date);
            view = itemView.findViewById(R.id.view5);
        }

        @SuppressLint("SetTextI18n")
        public void bind(TransactionModel model, String option) {
            NumberFormat formatter = new DecimalFormat("#,###");

            nominal.setText("Nominal: Rp." + formatter.format(model.getNominal()));
            transactionId.setText("No.Transaksi: " + model.getTransactionId());
            date.setText("Waktu: " + model.getDate());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), TransactionDetailActivity.class);
                    intent.putExtra(TransactionDetailActivity.EXTRA_TRANSACTION, model);
                    intent.putExtra(TransactionDetailActivity.EXTRA_OPTION, option);
                    itemView.getContext().startActivity(intent);
                }
            });


        }
    }
}
