package com.devit.nddb.Activity.ui.OtpValidation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.devit.nddb.data.repository.OtpValidation.OtpRepository
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val otpRepository: OtpRepository
) : ViewModel() {

    private var _uiState = MutableLiveData<OtpUiState>()
    val uiStateLiveData: LiveData<OtpUiState> = _uiState

    private var _loginResponse = MutableLiveData<BaseResponse>()
    val loginResponseLiveData: LiveData<BaseResponse> = _loginResponse

    private var _otpResponse = MutableLiveData<OtpResponse>()
    val otpResponseLiveData : LiveData<OtpResponse> = _otpResponse

    fun otpValidation(/*otp : String,*/mobileNumber : String){
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            otpRepository.otpValidation("0000",mobileNumber).collect { otpValidation ->
                when (otpValidation) {
                    is DataState.Success -> {
                        // Any other page
                        _uiState.postValue(ContentState)
                        otpValidation.data.let {
                            _otpResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        _uiState.postValue(ErrorState(otpValidation.message))
                    }
                }
            }
        }
    }

    fun loginWithOTP(phone_number: String,lang_id : Int) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            loginRepository.loginWithOTP(phone_number,lang_id ).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        _uiState.postValue(ContentState)
                        dataState.data.let {
                            _loginResponse.postValue(it)
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