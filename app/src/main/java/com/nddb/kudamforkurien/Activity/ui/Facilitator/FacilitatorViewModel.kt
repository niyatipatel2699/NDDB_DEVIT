package com.nddb.kudamforkurien.Activity.ui.Facilitator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.remote.responses.BaseResponse
import com.nddb.kudamforkurien.data.repository.AddFacilitatorRepo.AddFacilitatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacilitatorViewModel @Inject constructor(
    private val addFacilitatorRepository: AddFacilitatorRepository
) : ViewModel(){

    private val _addFacilitatorResponse = MutableLiveData<BaseResponse>()
    val addFacilitatorLiveData : LiveData<BaseResponse> = _addFacilitatorResponse

    private var _uiState = MutableLiveData<FacilitatorUiState>()
    val uiStateLiveData : LiveData<FacilitatorUiState> = _uiState

    fun addFacilitator(no_of_people : String,location : String){

        /*_uiState.postValue(LoadingState)
        viewModelScope.launch {
            addFacilitatorRepository.addFacilitator(no_of_people,location).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _uiState.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }

        }*/
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            addFacilitatorRepository.addFacilitator(no_of_people,location).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _addFacilitatorResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }

        }
    }
}