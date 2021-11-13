
package com.nddb.kudamforkurien.Activity.ui.change_language

sealed class ChangeLanguageUiState

object LoadingState : ChangeLanguageUiState()
object ContentState : ChangeLanguageUiState()
class ErrorState(val message: String) : ChangeLanguageUiState()
