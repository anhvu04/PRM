package com.example.pe_prm392_phananhvu.api;

import com.example.pe_prm392_phananhvu.model.Nganh;
import com.example.pe_prm392_phananhvu.model.Sinhvien;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    
    // Sinh viên endpoints
    @GET("Sinhvien")
    Call<List<Sinhvien>> getAllSinhvien();

    @GET("Sinhvien/{id}")
    Call<Sinhvien> getSinhvienById(@Path("id") String id);

    @POST("Sinhvien")
    Call<Sinhvien> createSinhvien(@Body Sinhvien sinhvien);

    @PUT("Sinhvien/{id}")
    Call<Sinhvien> updateSinhvien(@Path("id") String id, @Body Sinhvien sinhvien);

    @DELETE("Sinhvien/{id}")
    Call<Void> deleteSinhvien(@Path("id") String id);
    
    // Ngành endpoints
    @GET("Nganh")
    Call<List<Nganh>> getAllNganh();

    @GET("Nganh/{id}")
    Call<Nganh> getNganhById(@Path("id") String id);

    @POST("Nganh")
    Call<Nganh> createNganh(@Body Nganh nganh);

    @PUT("Nganh/{id}")
    Call<Nganh> updateNganh(@Path("id") String id, @Body Nganh nganh);

    @DELETE("Nganh/{id}")
    Call<Void> deleteNganh(@Path("id") String id);
}
