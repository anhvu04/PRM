package com.example.pe_prm392_phananhvu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Nganh;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNganhActivity extends AppCompatActivity {

    private TextView tvEditNganhId;
    private TextInputEditText etEditNameNganh;
    private Button btnUpdateNganh, btnCancelEditNganh;
    private String nganhId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nganh);

        initViews();
        loadDataFromIntent();
        setupClickListeners();
    }

    private void initViews() {
        tvEditNganhId = findViewById(R.id.tvEditNganhId);
        etEditNameNganh = findViewById(R.id.etEditNameNganh);
        btnUpdateNganh = findViewById(R.id.btnUpdateNganh);
        btnCancelEditNganh = findViewById(R.id.btnCancelEditNganh);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        nganhId = intent.getStringExtra("nganh_id");
        String nganhName = intent.getStringExtra("nganh_name");

        if (nganhId != null && nganhName != null) {
            tvEditNganhId.setText(nganhId);
            etEditNameNganh.setText(nganhName);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin ngành", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        btnCancelEditNganh.setOnClickListener(v -> finish());
        
        btnUpdateNganh.setOnClickListener(v -> {
            if (validateInput()) {
                updateNganh();
            }
        });
    }

    private boolean validateInput() {
        if (etEditNameNganh.getText().toString().trim().isEmpty()) {
            etEditNameNganh.setError("Vui lòng nhập tên ngành");
            etEditNameNganh.requestFocus();
            return false;
        }
        return true;
    }

    private void updateNganh() {
        String nameNganh = etEditNameNganh.getText().toString().trim();

        // Tạo đối tượng Nganh với thông tin cập nhật
        Nganh nganh = new Nganh();
        nganh.setId(nganhId);
        nganh.setNameNganh(nameNganh);

        // Gọi API để cập nhật ngành
        Call<Nganh> call = ApiClient.getApiService().updateNganh(nganhId, nganh);
        call.enqueue(new Callback<Nganh>() {
            @Override
            public void onResponse(Call<Nganh> call, Response<Nganh> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditNganhActivity.this, 
                            "Cập nhật ngành thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(EditNganhActivity.this, 
                            "Lỗi khi cập nhật ngành", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Nganh> call, Throwable t) {
                Toast.makeText(EditNganhActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
