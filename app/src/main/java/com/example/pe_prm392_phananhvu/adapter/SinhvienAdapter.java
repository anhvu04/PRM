package com.example.pe_prm392_phananhvu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pe_prm392_phananhvu.R;
import com.example.pe_prm392_phananhvu.model.Sinhvien;

import java.util.List;

public class SinhvienAdapter extends BaseAdapter {
    private Context context;
    private List<Sinhvien> sinhvienList;
    private LayoutInflater inflater;

    public SinhvienAdapter(Context context, List<Sinhvien> sinhvienList) {
        this.context = context;
        this.sinhvienList = sinhvienList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sinhvienList != null ? sinhvienList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return sinhvienList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_sinhvien, parent, false);
            holder = new ViewHolder();
            holder.tvSinhvienName = convertView.findViewById(R.id.tvSinhvienName);
            holder.tvSinhvienInfo = convertView.findViewById(R.id.tvSinhvienInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Sinhvien sinhvien = sinhvienList.get(position);
        holder.tvSinhvienName.setText(sinhvien.getName());
        
        // Hiển thị thông tin tóm tắt
        String info = "ID: " + sinhvien.getId() + " | " +
                     "Ngày sinh: " + sinhvien.getDate() + " | " +
                     "Giới tính: " + sinhvien.getGender();
        holder.tvSinhvienInfo.setText(info);

        return convertView;
    }

    public void updateData(List<Sinhvien> newSinhvienList) {
        this.sinhvienList = newSinhvienList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvSinhvienName;
        TextView tvSinhvienInfo;
    }
}
