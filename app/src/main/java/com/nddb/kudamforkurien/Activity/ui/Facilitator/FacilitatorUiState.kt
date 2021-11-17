package com.nddb.kudamforkurien.Activity.ui.Facilitator

sealed class FacilitatorUiState

object LoadingState : FacilitatorUiState()
object ContentState : FacilitatorUiState()
class ErrorState(val message: String) : FacilitatorUiState()

