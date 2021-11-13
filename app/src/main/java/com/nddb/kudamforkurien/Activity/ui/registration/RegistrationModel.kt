package com.nddb.kudamforkurien.Activity.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nddb.kudamforkurien.data.remote.responses.Registration.DistrictResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.RegistrationResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.StateResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.UserTypeResponse
import com.nddb.kudamforkurien.data.remote.responses.StepCountResponse
import com.nddb.kudamforkurien.data.repository.Registration.RegistrationRepository
import com.nddb.kudamforkurien.data.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private var _uiState = MutableLiveData<RegistrationUiState>()
    val uiStateLiveData: LiveData<RegistrationUiState> = _uiState

    private var _userTypeResponse = MutableLiveData<UserTypeResponse>()
    val userTypeResponseLiveData: LiveData<UserTypeResponse> = _userTypeResponse

    private var _registerResponse = MutableLiveData<RegistrationResponse>()
    val registerResponseLiveData : LiveData<RegistrationResponse> = _registerResponse

    private var _stateResponse = MutableLiveData<StateResponse>()
    val stateResponseLiveData: LiveData<StateResponse> = _stateResponse

    private var _cityResponse = MutableLiveData<DistrictResponse>()
    val cityResponseLiveData: LiveData<DistrictResponse> = _cityResponse


    private var _stepCountResponse = MutableLiveData<StepCountResponse>()
    val stepCountResponseLiveData : LiveData<StepCountResponse> = _stepCountResponse

    init {
        fetchUserType()
        fetchStateList()
    }


    fun registerUser(
        first_name: String,
        last_name: String,
        user_type: String,
        state: String,
        city: String,
        gender: String?,
        mobileNumber: String?,
    ) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            registrationRepository.registerUser(first_name,last_name,user_type,state,city,
                gender!!,mobileNumber!!,1).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _registerResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }

        }
    }

    fun fetchUserType() {
        viewModelScope.launch {
            registrationRepository.getUserType().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _userTypeResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }

        }
    }

    fun fetchStateList() {
        viewModelScope.launch {
            registrationRepository.getState().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _stateResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(dataState.message))
                    }
                }
            }
        }

    }

    fun fetchCityList(stateId: Int) {
        viewModelScope.launch {
            registrationRepository.getDistrict(stateId).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        dataState.data.let {
                            _cityResponse.postValue(it)
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