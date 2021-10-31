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
package com.devit.nddb.Activity.ui.LanguageList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devit.nddb.Activity.ui.change_language.ChangeLanguageUiState
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.devit.nddb.data.repository.Language.LanguageRepository
import com.wajahatkarim3.imagine.data.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private var _uiState = MutableLiveData<LanguageUiState>()
    val uiStateLiveData: LiveData<LanguageUiState> = _uiState

    private var ui_State = MutableLiveData<ChangeLanguageUiState>()
    val lanStateLiveData : LiveData<ChangeLanguageUiState> = ui_State

    private var _lanResponse = MutableLiveData<LanguageResponse>()
    val lanResponseLiveData: LiveData<LanguageResponse> = _lanResponse

    fun getLanguage() {

        viewModelScope.launch {
            languageRepository.getLanguage().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        _uiState.postValue(ContentState)
                        dataState.data.let {
                            _lanResponse.postValue(it)
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