package com.example.pe_prm392_phananhvu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pe_prm392_phananhvu.model.Nganh;

import java.util.List;

public class NganhSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Nganh> nganhList;
    private LayoutInflater inflater;

    public NganhSpinnerAdapter(Context context, List<Nganh> nganhList) {
        this.context = context;
        this.nganhList = nganhList;
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
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        
        if (position < nganhList.size()) {
            Nganh nganh = nganhList.get(position);
            textView.setText(nganh.getNameNganh());
        }

        return convertView;
    }

    public void updateData(List<Nganh> newNganhList) {
        this.nganhList = newNganhList;
        notifyDataSetChanged();
    }

    public Nganh getNganhAtPosition(int position) {
        if (position >= 0 && position < nganhList.size()) {
            return nganhList.get(position);
        }
        return null;
    }
}
