package com.example.pe_prm392_phananhvu;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pe_prm392_phananhvu.api.ApiClient;
import com.example.pe_prm392_phananhvu.model.Sinhvien;
import com.example.pe_prm392_phananhvu.utils.ContactHelper;
import com.example.pe_prm392_phananhvu.utils.PermissionHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SinhvienDetailActivity extends AppCompatActivity {

    private TextView tvDetailId, tvDetailName, tvDetailUsername, tvDetailDate, tvDetailGender,
                     tvDetailAddress, tvDetailIdNganh, tvDetailPhone;
    private Button btnBack, btnEditSinhvien, btnDeleteSinhvien, btnSyncContact;
    private String sinhvienId;
    private Sinhvien currentSinhvien;

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

        btnSyncContact.setOnClickListener(v -> syncContactPhone());
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
        btnSyncContact = findViewById(R.id.btnSyncContact);
    }

    private void loadSinhvienDetail(String id) {
        Call<Sinhvien> call = ApiClient.getApiService().getSinhvienById(id);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentSinhvien = response.body();
                    displaySinhvienInfo(currentSinhvien);
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

    private void syncContactPhone() {
        if (currentSinhvien == null) {
            Toast.makeText(this, "Chưa tải được thông tin sinh viên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra quyền đọc danh bạ
        if (!PermissionHelper.hasContactsPermission(this)) {
            requestContactsPermission();
            return;
        }

        performContactSync();
    }

    private void requestContactsPermission() {
        if (PermissionHelper.shouldShowRequestPermissionRationale(this)) {
            // Hiển thị lý do cần quyền
            new AlertDialog.Builder(this)
                    .setTitle("Cần quyền truy cập danh bạ")
                    .setMessage("Ứng dụng cần quyền đọc danh bạ để tìm và đồng bộ số điện thoại của sinh viên.")
                    .setPositiveButton("Cho phép", (dialog, which) ->
                            PermissionHelper.requestContactsPermission(this))
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            PermissionHelper.requestContactsPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, thực hiện đồng bộ
                performContactSync();
            } else {
                Toast.makeText(this, "Cần quyền đọc danh bạ để đồng bộ số điện thoại",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void performContactSync() {
        String studentName = currentSinhvien.getName();

        // Hiển thị loading
        Toast.makeText(this, "Đang tìm kiếm trong danh bạ...", Toast.LENGTH_SHORT).show();

        // Tìm số điện thoại trong danh bạ
        String phoneFromContacts = ContactHelper.findPhoneNumberByName(this, studentName);

        if (phoneFromContacts != null) {
            // Tìm thấy số điện thoại
            showSyncConfirmDialog(phoneFromContacts);
        } else {
            // Không tìm thấy
            showNotFoundDialog(studentName);
        }
    }

    private void showSyncConfirmDialog(String phoneFromContacts) {
        String currentPhone = currentSinhvien.getPhone();
        String message;

        if (currentPhone != null && !currentPhone.trim().isEmpty()) {
            message = "Tìm thấy số điện thoại trong danh bạ!\n\n" +
                     "Số hiện tại: " + currentPhone + "\n" +
                     "Số trong danh bạ: " + phoneFromContacts + "\n\n" +
                     "Bạn có muốn cập nhật số điện thoại không?";
        } else {
            message = "Tìm thấy số điện thoại trong danh bạ!\n\n" +
                     "Số điện thoại: " + phoneFromContacts + "\n\n" +
                     "Bạn có muốn cập nhật số điện thoại không?";
        }

        new AlertDialog.Builder(this)
                .setTitle("Đồng bộ số điện thoại")
                .setMessage(message)
                .setPositiveButton("Cập nhật", (dialog, which) -> updatePhoneNumber(phoneFromContacts))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showNotFoundDialog(String studentName) {
        new AlertDialog.Builder(this)
                .setTitle("Không tìm thấy")
                .setMessage("Không tìm thấy \"" + studentName + "\" trong danh bạ điện thoại.\n\n" +
                           "Vui lòng kiểm tra:\n" +
                           "• Tên trong danh bạ có chính xác không\n" +
                           "• Đã lưu số điện thoại của sinh viên này chưa")
                .setPositiveButton("Đã hiểu", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void updatePhoneNumber(String newPhoneNumber) {
        // Cập nhật object hiện tại
        currentSinhvien.setPhone(newPhoneNumber);

        // Gọi API để cập nhật
        Call<Sinhvien> call = ApiClient.getApiService().updateSinhvien(currentSinhvien.getId(), currentSinhvien);
        call.enqueue(new Callback<Sinhvien>() {
            @Override
            public void onResponse(Call<Sinhvien> call, Response<Sinhvien> response) {
                if (response.isSuccessful()) {
                    // Cập nhật giao diện
                    tvDetailPhone.setText(newPhoneNumber);
                    Toast.makeText(SinhvienDetailActivity.this,
                            "Đã cập nhật số điện thoại thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SinhvienDetailActivity.this,
                            "Lỗi khi cập nhật số điện thoại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sinhvien> call, Throwable t) {
                Toast.makeText(SinhvienDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
