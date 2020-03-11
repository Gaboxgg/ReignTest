package com.example.reigntest

import com.example.reigntest.data.HitsPojo
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("?query=android/")
    fun getHits(): Call<HitsPojo>
}