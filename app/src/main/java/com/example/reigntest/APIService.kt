package com.example.reigntest

import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("?query=android/")
    fun getHits(): Call<HitsPojo>
}