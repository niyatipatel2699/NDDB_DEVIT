package com.nddb.kudamforkurien.Activity.ui.walked_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.nddb.kudamforkurien.data.remote.responses.History.HistoryResponse
import com.nddb.kudamforkurien.data.repository.HistoryRepo.HistoryRepository
import com.nddb.kudamforkurien.data.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkedHistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel()
{

    private var _historyResponse = MutableLiveData<HistoryResponse>()
    val historyResponseLiveData: LiveData<HistoryResponse> = _historyResponse

    private var _uiState = MutableLiveData<HistoryUiState>()
    val uiStateLiveData: LiveData<HistoryUiState> = _uiState

    init {
        getWalkHistory()
    }
    fun getWalkHistory() {

        viewModelScope.launch {
            historyRepository.getWalkHistory().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // Any other page
                        _uiState.postValue(ContentState)
                        dataState.data.let {
                            _historyResponse.postValue(it)
                        }
                    }

                    is DataState.Error -> {
                        //_uiState.postValue(ErrorState(dataState.message))


                    }
                }
            }
        }
    }

}
