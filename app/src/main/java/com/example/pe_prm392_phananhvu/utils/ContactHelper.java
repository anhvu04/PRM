package com.example.pe_prm392_phananhvu.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactHelper {

    public static class Contact {
        private String name;
        private String phoneNumber;

        public Contact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        @Override
        public String toString() {
            return "Contact{name='" + name + "', phoneNumber='" + phoneNumber + "'}";
        }
    }

    /**
     * Đọc tất cả danh bạ từ điện thoại
     */
    public static List<Contact> getAllContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();

        // Kiểm tra quyền trước khi đọc
        if (!PermissionHelper.hasContactsPermission(context)) {
            return contacts; // Trả về danh sách rỗng nếu không có quyền
        }

        Cursor cursor = null;
        try {
            // Query để lấy tên và số điện thoại
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                do {
                    String name = cursor.getString(nameIndex);
                    String phoneNumber = cursor.getString(phoneIndex);

                    // Chỉ thêm contact nếu có tên và số điện thoại
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNumber)) {
                        // Làm sạch số điện thoại (xóa khoảng trắng, dấu gạch ngang)
                        phoneNumber = cleanPhoneNumber(phoneNumber);
                        contacts.add(new Contact(name.trim(), phoneNumber));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contacts;
    }

    /**
     * Tìm số điện thoại theo tên trong danh bạ
     */
    public static String findPhoneNumberByName(Context context, String targetName) {
        if (TextUtils.isEmpty(targetName)) {
            return null;
        }

        List<Contact> contacts = getAllContacts(context);
        
        // Tìm kiếm chính xác tên
        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(targetName.trim())) {
                return contact.getPhoneNumber();
            }
        }

        // Tìm kiếm tên có chứa từ khóa (fallback)
        for (Contact contact : contacts) {
            if (contact.getName().toLowerCase().contains(targetName.trim().toLowerCase())) {
                return contact.getPhoneNumber();
            }
        }

        return null; // Không tìm thấy
    }

    /**
     * Làm sạch số điện thoại - xóa khoảng trắng, dấu gạch ngang, dấu ngoặc
     */
    private static String cleanPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "";
        }
        
        return phoneNumber.replaceAll("[\\s\\-\\(\\)\\+]", "");
    }

    /**
     * Kiểm tra xem tên có tồn tại trong danh bạ không
     */
    public static boolean isNameExistInContacts(Context context, String targetName) {
        return findPhoneNumberByName(context, targetName) != null;
    }

    /**
     * Lấy danh sách tên tất cả contact (để debug)
     */
    public static List<String> getAllContactNames(Context context) {
        List<String> names = new ArrayList<>();
        List<Contact> contacts = getAllContacts(context);
        
        for (Contact contact : contacts) {
            names.add(contact.getName());
        }
        
        return names;
    }
}
