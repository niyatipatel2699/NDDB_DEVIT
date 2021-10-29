package com.devit.nddb.Activity.ui.change_language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devit.nddb.Activity.ui.LanguageList.LanguageUiState
import com.devit.nddb.data.remote.responses.Language.LanguageResponse

class ChangeLanguageViewModel : ViewModel() {


    private var _uiState = MutableLiveData<ChangeLanguageUiState>()
    val uiStateLiveData: LiveData<ChangeLanguageUiState> = _uiState

    private var _lanResponse = MutableLiveData<LanguageResponse>()
    val lanResponseLiveData: LiveData<LanguageResponse> = _lanResponse

    // TODO: Implement the ViewModel
}