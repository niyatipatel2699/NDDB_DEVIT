
package com.nddb.kudamforkurien.Activity.ui.walked_history

sealed class HistoryUiState

object LoadingState : HistoryUiState()
object ContentState : HistoryUiState()
class ErrorState(val message: String) : HistoryUiState()
