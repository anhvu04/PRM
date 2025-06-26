package com.example.pe_prm392_phananhvu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Nganh;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNganhActivity extends AppCompatActivity {

    private TextInputEditText etNameNganh;
    private Button btnSaveNganh, btnCancelNganh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nganh);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etNameNganh = findViewById(R.id.etNameNganh);
        btnSaveNganh = findViewById(R.id.btnSaveNganh);
        btnCancelNganh = findViewById(R.id.btnCancelNganh);
    }

    private void setupClickListeners() {
        btnCancelNganh.setOnClickListener(v -> finish());
        
        btnSaveNganh.setOnClickListener(v -> {
            if (validateInput()) {
                saveNganh();
            }
        });
    }

    private boolean validateInput() {
        if (etNameNganh.getText().toString().trim().isEmpty()) {
            etNameNganh.setError("Vui lòng nhập tên ngành");
            etNameNganh.requestFocus();
            return false;
        }
        return true;
    }

    private void saveNganh() {
        String nameNganh = etNameNganh.getText().toString().trim();

        // Tạo đối tượng Nganh mới (không cần ID vì API sẽ tự tạo)
        Nganh nganh = new Nganh();
        nganh.setNameNganh(nameNganh);

        // Gọi API để tạo ngành mới
        Call<Nganh> call = ApiClient.getApiService().createNganh(nganh);
        call.enqueue(new Callback<Nganh>() {
            @Override
            public void onResponse(Call<Nganh> call, Response<Nganh> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddNganhActivity.this, 
                            "Thêm ngành thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(AddNganhActivity.this, 
                            "Lỗi khi thêm ngành", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Nganh> call, Throwable t) {
                Toast.makeText(AddNganhActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
