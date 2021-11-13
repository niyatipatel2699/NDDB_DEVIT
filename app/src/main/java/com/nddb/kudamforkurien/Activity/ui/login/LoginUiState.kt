
package com.nddb.kudamforkurien.ui.home

sealed class LoginUiState

object LoadingState : LoginUiState()
object ContentState : LoginUiState()
object EmptyState : LoginUiState()
class ErrorState(val message: String) : LoginUiState()
