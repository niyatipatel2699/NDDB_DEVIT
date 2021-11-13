package com.nddb.kudamforkurien.Activity.ui.OtpValidation

sealed class OtpUiState

object ContentState : OtpUiState()
class ErrorState(val message: String) : OtpUiState()
object LoadingState : OtpUiState()