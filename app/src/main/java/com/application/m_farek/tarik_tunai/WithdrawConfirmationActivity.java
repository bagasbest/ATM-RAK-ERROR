package com.application.m_farek.tarik_tunai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityWithdrawConfirmationBinding;
import com.application.m_farek.homepage.HomeActivity;
import com.application.m_farek.homepage.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class WithdrawConfirmationActivity extends AppCompatActivity {


    public static final String EXTRA_USER = "users";
    public static final String EXTRA_NOMINAL = "nominal";
    private ActivityWithdrawConfirmationBinding binding;
    private UserModel model;
    private int chance =3;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NumberFormat formatter = new DecimalFormat("#,###");

        model = getIntent().getParcelableExtra(EXTRA_USER);


        binding.rekening.setText("No.Rekening: " + model.getRekening());
        binding.name.setText("Nasabah, " + model.getName());


        binding.nominal.setText("Rp" + formatter.format(getIntent().getLongExtra(EXTRA_NOMINAL, 0)));
        binding.total.setText("Rp" + formatter.format(getIntent().getLongExtra(EXTRA_NOMINAL, 0)));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPin();
            }
        });
    }




    private void inputPin() {
        Dialog dialog;
        Button btnSubmit;
        EditText etPin;

        dialog = new Dialog(this);

        dialog.setContentView(R.layout.popup_pin);
        dialog.setCanceledOnTouchOutside(false);


        btnSubmit = dialog.findViewById(R.id.submit);
        etPin = dialog.findViewById(R.id.pin);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPin = etPin.getText().toString().trim();

                /// validasi pin oleh sistem
                if(inputPin.isEmpty()) {
                    Toast.makeText(WithdrawConfirmationActivity.this, "PIN tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!inputPin.equals(model.getPin()) && chance > 0) {
                    chance--;
                    Toast.makeText(WithdrawConfirmationActivity.this, "PIN Salah!, Kesempatan " + chance + " kali lagi", Toast.LENGTH_SHORT).show();
                    return;
                } else if (chance == 0) {
                    showBlockedUser();
                    return;
                }

                /// ke halaman tarik tunai
                Intent intent = new Intent(WithdrawConfirmationActivity.this, WithdrawResultActivity.class);
                intent.putExtra(WithdrawResultActivity.EXTRA_USER, model);
                intent.putExtra(WithdrawResultActivity.EXTRA_NOMINAL, getIntent().getLongExtra(EXTRA_NOMINAL, 0));
                startActivity(intent);


            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void showBlockedUser() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.getUid())
                .update("isUserBlocked", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {


                            new AlertDialog.Builder(WithdrawConfirmationActivity.this)
                                    .setTitle("Rekening Anda Terblokir")
                                    .setMessage("Maaf, rekening anda terblokir karena sudah 3 kali salah menginputkan PIN, silahkan hubungi CS M-FAREK")
                                    .setIcon(R.drawable.ic_baseline_warning_24)
                                    .setPositiveButton("OKE", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(WithdrawConfirmationActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        dialogInterface.dismiss();
                                        startActivity(intent);
                                        finish();
                                    })
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}