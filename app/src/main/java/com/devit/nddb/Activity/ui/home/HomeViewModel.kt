package com.devit.nddb.Activity.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devit.nddb.Activity.ui.registration.ErrorState
import com.devit.nddb.Activity.ui.registration.LoadingState
import com.devit.nddb.Activity.ui.registration.RegistrationUiState
import com.devit.nddb.data.remote.responses.StepCountResponse
import com.devit.nddb.data.repository.HomeRepo.HomeRepository
import com.devit.nddb.data.repository.Registration.RegistrationRepository
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.room.entity.Steps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
/*class HomeViewModel : ViewModel() {*/

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private var _stepCountResponse = MutableLiveData<StepCountResponse>()
    val stepCountResponseLiveData : LiveData<StepCountResponse> = _stepCountResponse

    private var _uiState = MutableLiveData<RegistrationUiState>()
    val uiStateLiveData: LiveData<RegistrationUiState> = _uiState

   /* fun stepCount(
        stepsList : List<Steps>
    ) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            homeRepository.stepCount(stepsList).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _stepCountResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }

        }
    }*/

    fun stepCount(
        stepsList : List<Steps>
    ) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            homeRepository.stepCount(stepsList).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _stepCountResponse.postValue(it)
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