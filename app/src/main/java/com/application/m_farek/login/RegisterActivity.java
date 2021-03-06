package com.application.m_farek.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.application.m_farek.R;
import com.application.m_farek.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        /// kembali ke halaman login
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        /// klik registrasi / buka rekening
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });



    }

    /// sebelum buka rekening, sistem perlu mendeteksi tiap kolom inputan: name, email, password, dan pin, memastikan kolom tersebut terisi
    private void formValidation() {
        String name = binding.name.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String pin = binding.pin.getText().toString().trim();
        String username = binding.username.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Nama Lengkap tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }  else if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Kata Sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }  else if (pin.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "PIN tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        /// tampilkan loading bar ketika klik login
        binding.progressBar.setVisibility(View.VISIBLE);

        /// fungsi untuk membuat akun baru / buka rekening baru
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            /// jika sukses, maka data inputan akan di simpan kedalam database
                            saveUserDataToDatabase(name, email, password, pin, username);
                        } else {
                            /// jika gagal, maka muncul box dialog gagal
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });



    }


    /// fungsi untuk menyimpan data inputan ke dalam database
    private void saveUserDataToDatabase(String name, String email, String password, String pin, String username) {
        /// generate unik ID user
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        ///generate nomor rekening: 16 digit
        long smallest = 1000_0000_0000_0000L; /// minimum 16 digit
        long biggest =  9999_9999_9999_9999L; /// maksimum 16 digit
        /// proses mengenerate nomor rekening
        long rekeningNumber = ThreadLocalRandom.current().nextLong(smallest, biggest+1);


        /// simpan data menggunakan Hash - map
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);
        user.put("pin", pin);
        user.put("uid", uid);
        user.put("username", username);
        user.put("balance", 100000000);
        user.put("pengeluaran", 0);
        user.put("rekening", rekeningNumber);
        user.put("isUserBlocked", false);


        /// simpan hash map kedalam database, collection users
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            /// jika sukses, maka akan muncul dialog box sukses
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            /// jika gagal, maka akan muncul dialog boc gagal
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });
    }




    /// munculkan dialog ketika gagal registrasi
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan registrasi")
                .setMessage("Silahkan mendaftar kembali dengan informasi yang benar")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// munculkan dialog ketika sukses registrasi
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan registrasi")
                .setMessage("Silahkan login")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
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