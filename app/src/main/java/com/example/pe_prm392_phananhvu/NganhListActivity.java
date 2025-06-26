package com.example.pe_prm392_phananhvu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.adapter.NganhAdapter;
import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Nganh;
import com.example.pe_prm392_phananhvu.model.Sinhvien;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NganhListActivity extends AppCompatActivity implements NganhAdapter.OnNganhActionListener {

    private ListView listViewNganh;
    private NganhAdapter adapter;
    private List<Nganh> nganhList;
    private Button btnAddNganhFromList, btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nganh_list);

        initViews();
        setupListView();
        setupButtonListeners();
        loadNganhData();
    }

    private void initViews() {
        listViewNganh = findViewById(R.id.listViewNganh);
        btnAddNganhFromList = findViewById(R.id.btnAddNganhFromList);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        nganhList = new ArrayList<>();
    }

    private void setupListView() {
        adapter = new NganhAdapter(this, nganhList, this);
        listViewNganh.setAdapter(adapter);
    }

    private void setupButtonListeners() {
        btnAddNganhFromList.setOnClickListener(v -> {
            Intent intent = new Intent(NganhListActivity.this, AddNganhActivity.class);
            startActivity(intent);
        });

        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void loadNganhData() {
        Call<List<Nganh>> call = ApiClient.getApiService().getAllNganh();
        call.enqueue(new Callback<List<Nganh>>() {
            @Override
            public void onResponse(Call<List<Nganh>> call, Response<List<Nganh>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    nganhList.clear();
                    nganhList.addAll(response.body());
                    adapter.updateData(nganhList);
                    
                    Toast.makeText(NganhListActivity.this, 
                            "Đã tải " + nganhList.size() + " ngành", 
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NganhListActivity.this, 
                            "Không thể tải danh sách ngành", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nganh>> call, Throwable t) {
                Toast.makeText(NganhListActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditNganh(Nganh nganh) {
        // Sẽ implement sau khi tạo EditNganhActivity
        Intent intent = new Intent(NganhListActivity.this, EditNganhActivity.class);
        intent.putExtra("nganh_id", nganh.getId());
        intent.putExtra("nganh_name", nganh.getNameNganh());
        startActivity(intent);
    }

    @Override
    public void onDeleteNganh(Nganh nganh) {
        // Kiểm tra xem có sinh viên nào trong ngành này không
        checkStudentsInMajorBeforeDelete(nganh);
    }

    private void checkStudentsInMajorBeforeDelete(Nganh nganh) {
        // Lấy tất cả sinh viên để kiểm tra
        Call<List<Sinhvien>> call = ApiClient.getApiService().getAllSinhvien();
        call.enqueue(new Callback<List<Sinhvien>>() {
            @Override
            public void onResponse(Call<List<Sinhvien>> call, Response<List<Sinhvien>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sinhvien> allStudents = response.body();

                    // Đếm số sinh viên trong ngành này
                    int studentCount = 0;
                    List<String> studentNames = new ArrayList<>();

                    for (Sinhvien sinhvien : allStudents) {
                        if (nganh.getId().equals(sinhvien.getIdNganh())) {
                            studentCount++;
                            studentNames.add(sinhvien.getName());
                        }
                    }

                    if (studentCount > 0) {
                        // Có sinh viên trong ngành, không cho phép xóa
                        showCannotDeleteDialog(nganh, studentCount, studentNames);
                    } else {
                        // Không có sinh viên, cho phép xóa
                        showConfirmDeleteDialog(nganh);
                    }
                } else {
                    Toast.makeText(NganhListActivity.this,
                            "Không thể kiểm tra danh sách sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sinhvien>> call, Throwable t) {
                Toast.makeText(NganhListActivity.this,
                        "Lỗi kết nối khi kiểm tra sinh viên: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCannotDeleteDialog(Nganh nganh, int studentCount, List<String> studentNames) {
        StringBuilder message = new StringBuilder();
        message.append("Không thể xóa ngành \"").append(nganh.getNameNganh()).append("\" vì còn ")
               .append(studentCount).append(" sinh viên trong ngành này:\n\n");

        // Hiển thị tối đa 5 tên sinh viên đầu tiên
        int displayCount = Math.min(studentNames.size(), 5);
        for (int i = 0; i < displayCount; i++) {
            message.append("• ").append(studentNames.get(i)).append("\n");
        }

        if (studentNames.size() > 5) {
            message.append("• ... và ").append(studentNames.size() - 5).append(" sinh viên khác\n");
        }

        message.append("\nVui lòng chuyển các sinh viên sang ngành khác hoặc xóa sinh viên trước khi xóa ngành.");

        new AlertDialog.Builder(this)
                .setTitle("Không thể xóa ngành")
                .setMessage(message.toString())
                .setPositiveButton("Đã hiểu", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showConfirmDeleteDialog(Nganh nganh) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Ngành \"" + nganh.getNameNganh() + "\" không có sinh viên nào.\n\nBạn có chắc chắn muốn xóa ngành này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteNganh(nganh))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void deleteNganh(Nganh nganh) {
        Call<Void> call = ApiClient.getApiService().deleteNganh(nganh.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NganhListActivity.this,
                            "Xóa ngành thành công!", Toast.LENGTH_SHORT).show();
                    loadNganhData(); // Tải lại danh sách
                } else {
                    Toast.makeText(NganhListActivity.this,
                            "Lỗi khi xóa ngành", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NganhListActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại từ trang khác
        loadNganhData();
    }
}
