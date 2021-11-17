package com.nddb.kudamforkurien.Activity.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.nddb.kudamforkurien.Activity.ui.registration.ErrorState
import com.nddb.kudamforkurien.Activity.ui.registration.LoadingState
import com.nddb.kudamforkurien.Activity.ui.registration.RegistrationUiState
import com.nddb.kudamforkurien.data.remote.responses.RankResponse.RankResponseModel
import com.nddb.kudamforkurien.data.remote.responses.StepCountResponse
import com.nddb.kudamforkurien.data.repository.HomeRepo.HomeRepository
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.room.entity.Steps
import com.nddb.kudamforkurien.model.DataSteps
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

    private var _rankResponse = MutableLiveData<RankResponseModel>()
    val rankLiveData : LiveData<RankResponseModel> = _rankResponse

    private var _rankState = MutableLiveData<HomeUiState>()
    val rankResponseLiveData: LiveData<HomeUiState> = _rankState

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
        stepsList : JsonObject
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

    fun getRank() {
        _rankState.postValue(com.nddb.kudamforkurien.Activity.ui.home.LoadingState)
        viewModelScope.launch {
            homeRepository.getRank().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _rankResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _rankState.postValue(com.nddb.kudamforkurien.Activity.ui.home.ErrorState(dataState.message))
                    }
                }
            }

        }
    }
}