package com.example.powermanager.ui.model

import androidx.lifecycle.ViewModel
import com.example.powermanager.ui.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun changeAppScreen(newScreenName: String) {
        _uiState.update { currentState ->
            currentState.copy(currentScreenName = newScreenName)
        }
    }

}