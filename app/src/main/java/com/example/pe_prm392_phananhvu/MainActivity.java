package com.example.pe_prm392_phananhvu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.adapter.SinhvienAdapter;
import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Sinhvien;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listViewSinhvien;
    private SinhvienAdapter adapter;
    private List<Sinhvien> sinhvienList;
    private Button btnAddSinhvien, btnAddNganh, btnViewNganh, btnRegisterStudent, btnLoginStudent, btnViewNewStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListView();
        setupButtonListeners();
        loadSinhvienData();
    }

    private void initViews() {
        listViewSinhvien = findViewById(R.id.listViewSinhvien);
        btnAddSinhvien = findViewById(R.id.btnAddSinhvien);
        btnAddNganh = findViewById(R.id.btnAddNganh);
        btnViewNganh = findViewById(R.id.btnViewNganh);
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        btnLoginStudent = findViewById(R.id.btnLoginStudent);
        btnViewNewStudents = findViewById(R.id.btnViewNewStudents);
        sinhvienList = new ArrayList<>();
    }

    private void setupListView() {
        adapter = new SinhvienAdapter(this, sinhvienList);
        listViewSinhvien.setAdapter(adapter);

        // Xử lý sự kiện click vào item trong ListView
        listViewSinhvien.setOnItemClickListener((parent, view, position, id) -> {
            Sinhvien selectedSinhvien = sinhvienList.get(position);

            // Chuyển sang trang chi tiết sinh viên
            Intent intent = new Intent(MainActivity.this, SinhvienDetailActivity.class);
            intent.putExtra("sinhvien_id", selectedSinhvien.getId());
            startActivity(intent);
        });
    }

    private void setupButtonListeners() {
        btnAddSinhvien.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSinhvienActivity.class);
            startActivity(intent);
        });

        btnAddNganh.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNganhActivity.class);
            startActivity(intent);
        });

        btnViewNganh.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NganhListActivity.class);
            startActivity(intent);
        });

        btnRegisterStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLoginStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnViewNewStudents.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewStudentsActivity.class);
            startActivity(intent);
        });
    }

    private void loadSinhvienData() {
        Call<List<Sinhvien>> call = ApiClient.getApiService().getAllSinhvien();
        call.enqueue(new Callback<List<Sinhvien>>() {
            @Override
            public void onResponse(Call<List<Sinhvien>> call, Response<List<Sinhvien>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sinhvien> allStudents = response.body();

                    // Lọc chỉ lấy sinh viên có isNew = false (do admin thêm)
                    sinhvienList.clear();
                    for (Sinhvien sinhvien : allStudents) {
                        if (!sinhvien.isNew()) {
                            sinhvienList.add(sinhvien);
                        }
                    }

                    adapter.updateData(sinhvienList);

                    Toast.makeText(MainActivity.this,
                            "Đã tải " + sinhvienList.size() + " sinh viên (do admin quản lý)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Không thể tải danh sách sinh viên",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sinhvien>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại từ trang khác
        loadSinhvienData();
    }
}