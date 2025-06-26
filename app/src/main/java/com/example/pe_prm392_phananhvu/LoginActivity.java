package com.example.pe_prm392_phananhvu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Sinhvien;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etLoginUsername, etLoginPassword;
    private Button btnLogin, btnBackToMainFromLogin, btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBackToMainFromLogin = findViewById(R.id.btnBackToMainFromLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
    }

    private void setupClickListeners() {
        btnBackToMainFromLogin.setOnClickListener(v -> finish());
        
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        btnLogin.setOnClickListener(v -> {
            if (validateInput()) {
                performLogin();
            }
        });
    }

    private boolean validateInput() {
        if (etLoginUsername.getText().toString().trim().isEmpty()) {
            etLoginUsername.setError("Vui lòng nhập tên đăng nhập");
            etLoginUsername.requestFocus();
            return false;
        }

        if (etLoginPassword.getText().toString().trim().isEmpty()) {
            etLoginPassword.setError("Vui lòng nhập mật khẩu");
            etLoginPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin() {
        String username = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // Lấy tất cả sinh viên từ API
        Call<List<Sinhvien>> call = ApiClient.getApiService().getAllSinhvien();
        call.enqueue(new Callback<List<Sinhvien>>() {
            @Override
            public void onResponse(Call<List<Sinhvien>> call, Response<List<Sinhvien>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sinhvien> sinhvienList = response.body();
                    
                    // Tìm sinh viên với username và password khớp, và isNew = true
                    Sinhvien foundSinhvien = null;
                    for (Sinhvien sinhvien : sinhvienList) {
                        if (sinhvien.getUsername().equals(username) && 
                            sinhvien.getPassword().equals(password) && 
                            sinhvien.isNew()) {
                            foundSinhvien = sinhvien;
                            break;
                        }
                    }
                    
                    if (foundSinhvien != null) {
                        // Đăng nhập thành công
                        Toast.makeText(LoginActivity.this, 
                                "Đăng nhập thành công! Chào mừng " + foundSinhvien.getName(), 
                                Toast.LENGTH_LONG).show();
                        
                        // Chuyển đến trang thông tin sinh viên
                        Intent intent = new Intent(LoginActivity.this, SinhvienDetailActivity.class);
                        intent.putExtra("sinhvien_id", foundSinhvien.getId());
                        startActivity(intent);
                        finish();
                    } else {
                        // Kiểm tra xem có sinh viên với username/password này nhưng không phải mới đăng ký
                        boolean existsButNotNew = false;
                        for (Sinhvien sinhvien : sinhvienList) {
                            if (sinhvien.getUsername().equals(username) && 
                                sinhvien.getPassword().equals(password) && 
                                !sinhvien.isNew()) {
                                existsButNotNew = true;
                                break;
                            }
                        }
                        
                        if (existsButNotNew) {
                            Toast.makeText(LoginActivity.this, 
                                    "Tài khoản này không phải là sinh viên mới đăng ký!", 
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, 
                                    "Tên đăng nhập hoặc mật khẩu không đúng!", 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, 
                            "Lỗi khi kiểm tra thông tin đăng nhập", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sinhvien>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
