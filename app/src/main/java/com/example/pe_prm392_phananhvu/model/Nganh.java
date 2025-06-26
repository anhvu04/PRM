package com.example.pe_prm392_phananhvu.model;

public class Nganh {
    private String id;
    private String nameNganh;

    public Nganh() {
    }

    public Nganh(String id, String nameNganh) {
        this.id = id;
        this.nameNganh = nameNganh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Giữ lại method cũ để tương thích
    public String getIDnganh() {
        return id;
    }

    public void setIDnganh(String id) {
        this.id = id;
    }

    public String getNameNganh() {
        return nameNganh;
    }

    public void setNameNganh(String nameNganh) {
        this.nameNganh = nameNganh;
    }

    @Override
    public String toString() {
        return nameNganh;
    }
}
