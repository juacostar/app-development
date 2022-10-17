package com.example.challenge10.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    // code for get de retrofit client api reference
    fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://www.datos.gov.co/resource/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}