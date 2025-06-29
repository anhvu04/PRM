package com.example.pe_prm392_phananhvu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.adapter.NganhSpinnerAdapter;
import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Nganh;
import com.example.pe_prm392_phananhvu.model.Sinhvien;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegisterName, etRegisterUsername, etRegisterPassword,
                              etRegisterDate, etRegisterAddress, etRegisterPhone;
    private RadioGroup rgRegisterGender;
    private RadioButton rbRegisterMale, rbRegisterFemale;
    private Button btnRegister, btnBackToMain;
    private Spinner spinnerRegisterNganh;
    private NganhSpinnerAdapter nganhAdapter;
    private List<Nganh> nganhList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupSpinner();
        setupClickListeners();
        loadNganhData();
    }

    private void initViews() {
        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterDate = findViewById(R.id.etRegisterDate);
        etRegisterAddress = findViewById(R.id.etRegisterAddress);
        etRegisterPhone = findViewById(R.id.etRegisterPhone);
        spinnerRegisterNganh = findViewById(R.id.spinnerRegisterNganh);
        
        rgRegisterGender = findViewById(R.id.rgRegisterGender);
        rbRegisterMale = findViewById(R.id.rbRegisterMale);
        rbRegisterFemale = findViewById(R.id.rbRegisterFemale);
        
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        nganhList = new ArrayList<>();
    }

    private void setupSpinner() {
        nganhAdapter = new NganhSpinnerAdapter(this, nganhList);
        spinnerRegisterNganh.setAdapter(nganhAdapter);
    }

    private void loadNganhData() {
        Call<List<Nganh>> call = ApiClient.getApiService().getAllNganh();
        call.enqueue(new Callback<List<Nganh>>() {
            @Override
            public void onResponse(Call<List<Nganh>> call, Response<List<Nganh>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    nganhList.clear();
                    nganhList.addAll(response.body());
                    nganhAdapter.updateData(nganhList);
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Không thể tải danh sách ngành", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nganh>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Lỗi kết nối khi tải ngành: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnBackToMain.setOnClickListener(v -> finish());
        
        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                registerSinhvien();
            }
        });
    }

    private boolean validateInput() {
        if (etRegisterName.getText().toString().trim().isEmpty()) {
            etRegisterName.setError("Vui lòng nhập họ và tên");
            etRegisterName.requestFocus();
            return false;
        }

        if (etRegisterUsername.getText().toString().trim().isEmpty()) {
            etRegisterUsername.setError("Vui lòng nhập tên đăng nhập");
            etRegisterUsername.requestFocus();
            return false;
        }

        if (etRegisterPassword.getText().toString().trim().isEmpty()) {
            etRegisterPassword.setError("Vui lòng nhập mật khẩu");
            etRegisterPassword.requestFocus();
            return false;
        }

        if (etRegisterDate.getText().toString().trim().isEmpty()) {
            etRegisterDate.setError("Vui lòng nhập ngày sinh");
            etRegisterDate.requestFocus();
            return false;
        }

        if (rgRegisterGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etRegisterAddress.getText().toString().trim().isEmpty()) {
            etRegisterAddress.setError("Vui lòng nhập địa chỉ");
            etRegisterAddress.requestFocus();
            return false;
        }

        if (spinnerRegisterNganh.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn ngành học", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etRegisterPhone.getText().toString().trim().isEmpty()) {
            etRegisterPhone.setError("Vui lòng nhập số điện thoại");
            etRegisterPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void registerSinhvien() {
        String name = etRegisterName.getText().toString().trim();
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String date = etRegisterDate.getText().toString().trim();
        String address = etRegisterAddress.getText().toString().trim();
        String phone = etRegisterPhone.getText().toString().trim();

        // Lấy ngành được chọn từ Spinner
        Nganh selectedNganh = nganhAdapter.getNganhAtPosition(spinnerRegisterNganh.getSelectedItemPosition());
        String idNganh = selectedNganh.getId();
        
        String gender = rbRegisterMale.isChecked() ? "Nam" : "Nữ";

        // Tạo đối tượng Sinhvien mới với isNew = true
        Sinhvien sinhvien = new Sinhvien();
        sinhvien.setName(name);
        sinhvien.setUsername(username);
        sinhvien.setPassword(password);
        sinhvien.setDate(date);
        sinhvien.setGender(gender);
        sinhvien.setAddress(address);
        sinhvien.setIdNganh(idNganh);
        sinhvien.setPhone(phone);
        sinhvien.setNew(true); // Đánh dấu là sinh viên mới đăng ký

        // Gọi API để tạo sinh viên mới
        Call<Sinhvien> call = ApiClient.getApiService().createSinhvien(sinhvien);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, 
                            "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.", 
                            Toast.LENGTH_LONG).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(RegisterActivity.this, 
                            "Lỗi khi đăng ký tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
