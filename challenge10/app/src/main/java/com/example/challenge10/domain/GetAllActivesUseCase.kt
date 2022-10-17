package com.example.challenge10.domain

import com.example.challenge10.ActiveRepository
import com.example.challenge10.model.Active

class GetAllActivesUseCase {

    private val repository = ActiveRepository()

    suspend operator fun invoke():List<Active>? = repository.getAllActives()
}