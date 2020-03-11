package com.example.reigntest.data

import com.google.gson.annotations.SerializedName

data class HitsPojo (@SerializedName("hits") var hits:List<HitPojo>)