package com.example.pe_prm392_phananhvu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class NewStudentsActivity extends AppCompatActivity {

    private ListView listViewNewStudents;
    private SinhvienAdapter adapter;
    private List<Sinhvien> newStudentsList;
    private Button btnBackToMainFromNew, btnRefreshNewStudents;
    private TextView tvNewStudentsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_students);

        initViews();
        setupListView();
        setupButtonListeners();
        loadNewStudentsData();
    }

    private void initViews() {
        listViewNewStudents = findViewById(R.id.listViewNewStudents);
        btnBackToMainFromNew = findViewById(R.id.btnBackToMainFromNew);
        btnRefreshNewStudents = findViewById(R.id.btnRefreshNewStudents);
        tvNewStudentsCount = findViewById(R.id.tvNewStudentsCount);
        newStudentsList = new ArrayList<>();
    }

    private void setupListView() {
        adapter = new SinhvienAdapter(this, newStudentsList);
        listViewNewStudents.setAdapter(adapter);

        // Xử lý sự kiện click vào item
        listViewNewStudents.setOnItemClickListener((parent, view, position, id) -> {
            Sinhvien selectedSinhvien = newStudentsList.get(position);
            Intent intent = new Intent(NewStudentsActivity.this, SinhvienDetailActivity.class);
            intent.putExtra("sinhvien_id", selectedSinhvien.getId());
            startActivity(intent);
        });
    }

    private void setupButtonListeners() {
        btnBackToMainFromNew.setOnClickListener(v -> finish());
        
        btnRefreshNewStudents.setOnClickListener(v -> loadNewStudentsData());
    }

    private void loadNewStudentsData() {
        tvNewStudentsCount.setText("Đang tải...");
        
        Call<List<Sinhvien>> call = ApiClient.getApiService().getAllSinhvien();
        call.enqueue(new Callback<List<Sinhvien>>() {
            @Override
            public void onResponse(Call<List<Sinhvien>> call, Response<List<Sinhvien>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sinhvien> allStudents = response.body();
                    
                    // Lọc chỉ lấy sinh viên có isNew = true
                    newStudentsList.clear();
                    for (Sinhvien sinhvien : allStudents) {
                        if (sinhvien.isNew()) {
                            newStudentsList.add(sinhvien);
                        }
                    }
                    
                    adapter.updateData(newStudentsList);
                    
                    // Cập nhật số lượng
                    tvNewStudentsCount.setText("Tổng cộng: " + newStudentsList.size() + " sinh viên mới đăng ký");
                    
                    if (newStudentsList.isEmpty()) {
                        Toast.makeText(NewStudentsActivity.this, 
                                "Chưa có sinh viên nào đăng ký mới", 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvNewStudentsCount.setText("Lỗi tải dữ liệu");
                    Toast.makeText(NewStudentsActivity.this, 
                            "Không thể tải danh sách sinh viên mới", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sinhvien>> call, Throwable t) {
                tvNewStudentsCount.setText("Lỗi kết nối");
                Toast.makeText(NewStudentsActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại từ trang khác
        loadNewStudentsData();
    }
}
