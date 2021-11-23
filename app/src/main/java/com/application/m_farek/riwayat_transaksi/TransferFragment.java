package com.application.m_farek.riwayat_transaksi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.m_farek.R;
import com.application.m_farek.databinding.FragmentTransferBinding;
import com.application.m_farek.riwayat_transaksi.data.TransactionAdapter;
import com.application.m_farek.riwayat_transaksi.data.TransactionViewModel;
import com.google.firebase.auth.FirebaseAuth;


public class TransferFragment extends Fragment {

    private FragmentTransferBinding binding;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransferBinding.inflate(inflater, container, false);
        initRecyclerView();
        initViewModel();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        binding.rvTransfer.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionAdapter("transfer");
        binding.rvTransfer.setAdapter(adapter);
    }

    private void initViewModel() {
        TransactionViewModel viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setTransferList(uid);
        viewModel.getListTransaction().observe(getViewLifecycleOwner(), transfer -> {
            if (transfer.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(transfer);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}