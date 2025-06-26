package com.example.pe_prm392_phananhvu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Sinhvien;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SinhvienDetailActivity extends AppCompatActivity {

    private TextView tvDetailId, tvDetailName, tvDetailUsername, tvDetailDate, tvDetailGender,
                     tvDetailAddress, tvDetailIdNganh, tvDetailPhone;
    private Button btnBack, btnEditSinhvien, btnDeleteSinhvien;
    private String sinhvienId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinhvien_detail);

        initViews();
        
        // Lấy ID sinh viên từ Intent
        Intent intent = getIntent();
        sinhvienId = intent.getStringExtra("sinhvien_id");
        
        if (sinhvienId != null) {
            loadSinhvienDetail(sinhvienId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());

        btnEditSinhvien.setOnClickListener(v -> {
            Intent intent1 = new Intent(SinhvienDetailActivity.this, EditSinhvienActivity.class);
            intent1.putExtra("sinhvien_id", sinhvienId);
            startActivity(intent1);
        });

        btnDeleteSinhvien.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên này?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteSinhvien())
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void initViews() {
        tvDetailId = findViewById(R.id.tvDetailId);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailUsername = findViewById(R.id.tvDetailUsername);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailIdNganh = findViewById(R.id.tvDetailIdNganh);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        btnBack = findViewById(R.id.btnBack);
        btnEditSinhvien = findViewById(R.id.btnEditSinhvien);
        btnDeleteSinhvien = findViewById(R.id.btnDeleteSinhvien);
    }

    private void loadSinhvienDetail(String id) {
        Call<Sinhvien> call = ApiClient.getApiService().getSinhvienById(id);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Sinhvien sinhvien = response.body();
                    displaySinhvienInfo(sinhvien);
                } else {
                    Toast.makeText(SinhvienDetailActivity.this, 
                            "Không thể tải thông tin sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(SinhvienDetailActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySinhvienInfo(Sinhvien sinhvien) {
        tvDetailId.setText(sinhvien.getId());
        tvDetailName.setText(sinhvien.getName());
        tvDetailUsername.setText(sinhvien.getUsername());
        tvDetailDate.setText(sinhvien.getDate());
        tvDetailGender.setText(sinhvien.getGender());
        tvDetailAddress.setText(sinhvien.getAddress());
        tvDetailIdNganh.setText(sinhvien.getIdNganh());
        tvDetailPhone.setText(sinhvien.getPhone());
    }

    private void deleteSinhvien() {
        Call<Void> call = ApiClient.getApiService().deleteSinhvien(sinhvienId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SinhvienDetailActivity.this,
                            "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(SinhvienDetailActivity.this,
                            "Lỗi khi xóa sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SinhvienDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
