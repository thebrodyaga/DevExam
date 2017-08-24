package com.example.win10.devexam.api;

import com.example.win10.devexam.dummy.DummyContent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Win10 on 22.07.2017.
 */

public interface ITechService {
    @GET("/")
    Call<List<DummyContent>> getDataList();
}
