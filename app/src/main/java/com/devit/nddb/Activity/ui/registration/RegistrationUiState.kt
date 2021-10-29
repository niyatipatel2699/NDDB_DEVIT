package com.devit.nddb.Activity.ui.registration

sealed class RegistrationUiState

object ContentState : RegistrationUiState()
class ErrorState(val message: String) : RegistrationUiState()
object LoadingState : RegistrationUiState()