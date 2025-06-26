package com.example.pe_prm392_phananhvu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.pe_prm392_phananhvu.R;
import com.example.pe_prm392_phananhvu.model.Nganh;

import java.util.List;

public class NganhAdapter extends BaseAdapter {
    private Context context;
    private List<Nganh> nganhList;
    private LayoutInflater inflater;
    private OnNganhActionListener listener;

    public interface OnNganhActionListener {
        void onEditNganh(Nganh nganh);
        void onDeleteNganh(Nganh nganh);
    }

    public NganhAdapter(Context context, List<Nganh> nganhList, OnNganhActionListener listener) {
        this.context = context;
        this.nganhList = nganhList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return nganhList != null ? nganhList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return nganhList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_nganh, parent, false);
            holder = new ViewHolder();
            holder.tvNganhName = convertView.findViewById(R.id.tvNganhName);
            holder.tvNganhId = convertView.findViewById(R.id.tvNganhId);
            holder.btnEditNganh = convertView.findViewById(R.id.btnEditNganh);
            holder.btnDeleteNganh = convertView.findViewById(R.id.btnDeleteNganh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Nganh nganh = nganhList.get(position);
        holder.tvNganhName.setText(nganh.getNameNganh());
        holder.tvNganhId.setText("ID: " + nganh.getId());

        // Xử lý sự kiện click cho nút Sửa
        holder.btnEditNganh.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditNganh(nganh);
            }
        });

        // Xử lý sự kiện click cho nút Xóa
        holder.btnDeleteNganh.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteNganh(nganh);
            }
        });

        return convertView;
    }

    public void updateData(List<Nganh> newNganhList) {
        this.nganhList = newNganhList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvNganhName;
        TextView tvNganhId;
        Button btnEditNganh;
        Button btnDeleteNganh;
    }
}
