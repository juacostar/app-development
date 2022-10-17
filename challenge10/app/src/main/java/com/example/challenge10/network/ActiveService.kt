package com.example.challenge10.network

import com.example.challenge10.model.Active
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActiveService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getQuotes():List<Active>{
        // return the list in a sedondary thread
        return withContext(Dispatchers.IO){
            // call the methos in the api client m
            val response = retrofit.create(SocrataApiClient::class.java).getAllActives()
            response.body() ?: emptyList()
        }

    }
}