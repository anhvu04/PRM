package com.example.pe_prm392_phananhvu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Sinhvien;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSinhvienActivity extends AppCompatActivity {

    private TextView tvEditSinhvienId;
    private TextInputEditText etEditName, etEditUsername, etEditPassword, etEditDate, 
                              etEditAddress, etEditIdNganh, etEditPhone;
    private RadioGroup rgEditGender;
    private RadioButton rbEditMale, rbEditFemale;
    private Button btnUpdate, btnCancelEdit;
    private String sinhvienId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sinhvien);

        initViews();
        loadDataFromIntent();
        setupClickListeners();
    }

    private void initViews() {
        tvEditSinhvienId = findViewById(R.id.tvEditSinhvienId);
        etEditName = findViewById(R.id.etEditName);
        etEditUsername = findViewById(R.id.etEditUsername);
        etEditPassword = findViewById(R.id.etEditPassword);
        etEditDate = findViewById(R.id.etEditDate);
        etEditAddress = findViewById(R.id.etEditAddress);
        etEditIdNganh = findViewById(R.id.etEditIdNganh);
        etEditPhone = findViewById(R.id.etEditPhone);
        
        rgEditGender = findViewById(R.id.rgEditGender);
        rbEditMale = findViewById(R.id.rbEditMale);
        rbEditFemale = findViewById(R.id.rbEditFemale);
        
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        sinhvienId = intent.getStringExtra("sinhvien_id");
        
        if (sinhvienId != null) {
            // Load chi tiết sinh viên từ API
            loadSinhvienDetail(sinhvienId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadSinhvienDetail(String id) {
        Call<Sinhvien> call = ApiClient.getApiService().getSinhvienById(id);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Sinhvien sinhvien = response.body();
                    populateFields(sinhvien);
                } else {
                    Toast.makeText(EditSinhvienActivity.this, 
                            "Không thể tải thông tin sinh viên", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(EditSinhvienActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(Sinhvien sinhvien) {
        tvEditSinhvienId.setText(sinhvien.getId());
        etEditName.setText(sinhvien.getName());
        etEditUsername.setText(sinhvien.getUsername());
        etEditPassword.setText(sinhvien.getPassword());
        etEditDate.setText(sinhvien.getDate());
        etEditAddress.setText(sinhvien.getAddress());
        etEditIdNganh.setText(sinhvien.getIdNganh());
        etEditPhone.setText(sinhvien.getPhone());
        
        // Set giới tính
        if ("Nam".equals(sinhvien.getGender())) {
            rbEditMale.setChecked(true);
        } else {
            rbEditFemale.setChecked(true);
        }
    }

    private void setupClickListeners() {
        btnCancelEdit.setOnClickListener(v -> finish());
        
        btnUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateSinhvien();
            }
        });
    }

    private boolean validateInput() {
        if (etEditName.getText().toString().trim().isEmpty()) {
            etEditName.setError("Vui lòng nhập tên sinh viên");
            etEditName.requestFocus();
            return false;
        }

        if (etEditUsername.getText().toString().trim().isEmpty()) {
            etEditUsername.setError("Vui lòng nhập username");
            etEditUsername.requestFocus();
            return false;
        }

        if (etEditPassword.getText().toString().trim().isEmpty()) {
            etEditPassword.setError("Vui lòng nhập password");
            etEditPassword.requestFocus();
            return false;
        }

        if (etEditDate.getText().toString().trim().isEmpty()) {
            etEditDate.setError("Vui lòng nhập ngày sinh");
            etEditDate.requestFocus();
            return false;
        }

        if (rgEditGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etEditAddress.getText().toString().trim().isEmpty()) {
            etEditAddress.setError("Vui lòng nhập địa chỉ");
            etEditAddress.requestFocus();
            return false;
        }

        if (etEditIdNganh.getText().toString().trim().isEmpty()) {
            etEditIdNganh.setError("Vui lòng nhập ID ngành");
            etEditIdNganh.requestFocus();
            return false;
        }

        if (etEditPhone.getText().toString().trim().isEmpty()) {
            etEditPhone.setError("Vui lòng nhập số điện thoại");
            etEditPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void updateSinhvien() {
        String name = etEditName.getText().toString().trim();
        String username = etEditUsername.getText().toString().trim();
        String password = etEditPassword.getText().toString().trim();
        String date = etEditDate.getText().toString().trim();
        String address = etEditAddress.getText().toString().trim();
        String idNganh = etEditIdNganh.getText().toString().trim();
        String phone = etEditPhone.getText().toString().trim();
        
        String gender = rbEditMale.isChecked() ? "Nam" : "Nữ";

        // Tạo đối tượng Sinhvien với thông tin cập nhật
        Sinhvien sinhvien = new Sinhvien();
        sinhvien.setId(sinhvienId);
        sinhvien.setName(name);
        sinhvien.setUsername(username);
        sinhvien.setPassword(password);
        sinhvien.setDate(date);
        sinhvien.setGender(gender);
        sinhvien.setAddress(address);
        sinhvien.setIdNganh(idNganh);
        sinhvien.setPhone(phone);

        // Gọi API để cập nhật sinh viên
        Call<Sinhvien> call = ApiClient.getApiService().updateSinhvien(sinhvienId, sinhvien);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditSinhvienActivity.this, 
                            "Cập nhật sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(EditSinhvienActivity.this, 
                            "Lỗi khi cập nhật sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(EditSinhvienActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
