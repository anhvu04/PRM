package com.example.pe_prm392_phananhvu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static final int REQUEST_CONTACTS_PERMISSION = 1001;

    /**
     * Kiểm tra xem có quyền đọc danh bạ không
     */
    public static boolean hasContactsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) 
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Yêu cầu quyền đọc danh bạ
     */
    public static void requestContactsPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                REQUEST_CONTACTS_PERMISSION
        );
    }

    /**
     * Kiểm tra xem có nên hiển thị lý do yêu cầu quyền không
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                activity, 
                Manifest.permission.READ_CONTACTS
        );
    }
}
