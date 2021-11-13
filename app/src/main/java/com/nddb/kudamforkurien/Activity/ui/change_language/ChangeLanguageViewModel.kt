package com.nddb.kudamforkurien.Activity.ui.change_language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nddb.kudamforkurien.data.remote.responses.Language.LanguageResponse
import com.nddb.kudamforkurien.data.repository.Language.LanguageRepository
import com.nddb.kudamforkurien.data.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeLanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {


    private var ui_State = MutableLiveData<ChangeLanguageUiState>()
    val lanStateLiveData : LiveData<ChangeLanguageUiState> = ui_State

    private var _lanResponse = MutableLiveData<LanguageResponse>()
    val lanResponseLiveData: LiveData<LanguageResponse> = _lanResponse

    private var _change_lanResponse = MutableLiveData<LanguageResponse>()
    val changelanResponseLiveData: LiveData<LanguageResponse> = _change_lanResponse


    fun getLanguage() {

        viewModelScope.launch {
            languageRepository.getLanguage().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        ui_State.postValue(ContentState)
                        dataState.data.let {
                            _lanResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        ui_State.postValue(ErrorState(dataState.message))
                    }

                }
            }
        }
    }

    fun updateLanguage(langid : Int){

        ui_State.postValue(LoadingState)
        viewModelScope.launch {
            languageRepository.updateLanguage(langid).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        ui_State.postValue(ContentState)
                        dataState.data.let {
                            _change_lanResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        ui_State.postValue(ErrorState(dataState.message))
                    }

                }
            }
        }
    }
}