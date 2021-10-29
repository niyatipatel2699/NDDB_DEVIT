package com.devit.nddb.Activity.ui.OtpValidation

import com.wajahatkarim3.imagine.ui.home.LoginUiState

sealed class OtpUiState

object ContentState : OtpUiState()
class ErrorState(val message: String) : OtpUiState()
object LoadingState : OtpUiState()