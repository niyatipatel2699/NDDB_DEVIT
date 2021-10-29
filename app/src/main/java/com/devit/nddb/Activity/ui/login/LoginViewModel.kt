/*
* Copyright 2021 Wajahat Karim (https://wajahatkarim.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.devit.nddb.Activity.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.utils.SingleLiveEvent
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.repository.LoginRepository
import com.wajahatkarim3.imagine.ui.home.ContentState
import com.wajahatkarim3.imagine.ui.home.ErrorState
import com.wajahatkarim3.imagine.ui.home.LoadingState
import com.wajahatkarim3.imagine.ui.home.LoginUiState
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

    private var _loginResponse = SingleLiveEvent<BaseResponse>()
    val loginResponseLiveData: SingleLiveEvent<BaseResponse> = _loginResponse

    fun loginWithOTP(mobileNumber: String, lang_id: Int?) {
        _uiState.postValue(LoadingState)
        viewModelScope.launch {
            loginRepository.loginWithOTP(mobileNumber,1).collect { dataState ->
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
