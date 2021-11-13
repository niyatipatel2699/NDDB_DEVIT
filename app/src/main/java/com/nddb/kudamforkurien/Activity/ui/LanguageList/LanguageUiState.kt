
package com.nddb.kudamforkurien.Activity.ui.LanguageList

sealed class LanguageUiState

object LoadingState : LanguageUiState()
object ContentState : LanguageUiState()
class ErrorState(val message: String) : LanguageUiState()
