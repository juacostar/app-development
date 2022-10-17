package com.example.challenge10.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.challenge10.domain.GetAllActivesUseCase
import com.example.challenge10.model.Active
import kotlinx.coroutines.launch

class ActiveViewModel: ViewModel() {

    val actives = MutableLiveData<List<Active>?>()

    val getAllActivesUseCase = GetAllActivesUseCase()

    fun onCreate(){
        viewModelScope.launch {
            val result = getAllActivesUseCase()
            if(!result.isNullOrEmpty()) {
            }
            actives.postValue(result)
        }
    }
}