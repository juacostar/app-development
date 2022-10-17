package com.example.challenge10.network

import com.example.challenge10.model.Active
import retrofit2.Response
import retrofit2.http.GET

interface SocrataApiClient   {

    @GET("q455-q5b5.json")
    suspend fun getAllActives():Response<List<Active>>

}