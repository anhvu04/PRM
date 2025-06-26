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

public class AddSinhvienActivity extends AppCompatActivity {

    private TextInputEditText etName, etUsername, etPassword, etDate, etAddress, etPhone;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave, btnCancel;
    private Spinner spinnerNganh;
    private NganhSpinnerAdapter nganhAdapter;
    private List<Nganh> nganhList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sinhvien);

        initViews();
        setupSpinner();
        setupClickListeners();
        loadNganhData();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etDate = findViewById(R.id.etDate);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        spinnerNganh = findViewById(R.id.spinnerNganh);
        
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        nganhList = new ArrayList<>();
    }

    private void setupSpinner() {
        nganhAdapter = new NganhSpinnerAdapter(this, nganhList);
        spinnerNganh.setAdapter(nganhAdapter);
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
                    Toast.makeText(AddSinhvienActivity.this,
                            "Không thể tải danh sách ngành", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nganh>> call, Throwable t) {
                Toast.makeText(AddSinhvienActivity.this,
                        "Lỗi kết nối khi tải ngành: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveSinhvien();
            }
        });
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Vui lòng nhập tên sinh viên");
            etName.requestFocus();
            return false;
        }

        if (etUsername.getText().toString().trim().isEmpty()) {
            etUsername.setError("Vui lòng nhập username");
            etUsername.requestFocus();
            return false;
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.setError("Vui lòng nhập password");
            etPassword.requestFocus();
            return false;
        }

        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("Vui lòng nhập ngày sinh");
            etDate.requestFocus();
            return false;
        }

        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            etAddress.requestFocus();
            return false;
        }

        if (spinnerNganh.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn ngành học", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void saveSinhvien() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Lấy ngành được chọn từ Spinner
        Nganh selectedNganh = nganhAdapter.getNganhAtPosition(spinnerNganh.getSelectedItemPosition());
        String idNganh = selectedNganh.getId();
        
        String gender = rbMale.isChecked() ? "Nam" : "Nữ";

        // Tạo đối tượng Sinhvien mới (không cần ID vì API sẽ tự tạo)
        Sinhvien sinhvien = new Sinhvien();
        sinhvien.setName(name);
        sinhvien.setUsername(username);
        sinhvien.setPassword(password);
        sinhvien.setDate(date);
        sinhvien.setGender(gender);
        sinhvien.setAddress(address);
        sinhvien.setIdNganh(idNganh);
        sinhvien.setPhone(phone);
        sinhvien.setNew(false); // Sinh viên được thêm bởi admin, không phải tự đăng ký

        // Gọi API để tạo sinh viên mới
        Call<Sinhvien> call = ApiClient.getApiService().createSinhvien(sinhvien);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddSinhvienActivity.this, 
                            "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(AddSinhvienActivity.this, 
                            "Lỗi khi thêm sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(AddSinhvienActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
