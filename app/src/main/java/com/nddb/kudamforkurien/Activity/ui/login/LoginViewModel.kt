
package com.nddb.kudamforkurien.Activity.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nddb.kudamforkurien.data.remote.responses.OtpValidation.OtpResponse
import com.nddb.kudamforkurien.utils.SingleLiveEvent
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.repository.LoginRepository
import com.nddb.kudamforkurien.ui.home.ContentState
import com.nddb.kudamforkurien.ui.home.ErrorState
import com.nddb.kudamforkurien.ui.home.LoadingState
import com.nddb.kudamforkurien.ui.home.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private var _uiState = MutableLiveData<LoginUiState>()
    val uiStateLiveData: LiveData<LoginUiState> = _uiState

    /*private var _loginResponse = SingleLiveEvent<BaseResponse>()
    val loginResponseLiveData: SingleLiveEvent<BaseResponse> = _loginResponse*/

    private var _loginResponse = SingleLiveEvent<OtpResponse>()
    val loginResponseLiveData: SingleLiveEvent<OtpResponse> = _loginResponse

    fun loginWithOTP(mobileNumber: String, lang_id: Int?) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            loginRepository.loginWithOTP(mobileNumber, lang_id!!).collect { dataState ->
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
