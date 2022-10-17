package com.example.challenge10

import com.example.challenge10.model.Active
import com.example.challenge10.network.ActiveService

class ActiveRepository {

    private val activeService = ActiveService()

    suspend fun getAllActives():List<Active>{
        return activeService.getQuotes()

    }
}