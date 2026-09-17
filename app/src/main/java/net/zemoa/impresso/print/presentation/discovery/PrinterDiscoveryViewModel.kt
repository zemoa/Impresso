package net.zemoa.impresso.print.presentation.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.zemoa.impresso.app.DiscoveryCoordinator

class PrinterDiscoveryViewModel(
    private val discoveryCoordinator: DiscoveryCoordinator,
) : ViewModel() {
    val uiState: StateFlow<PrinterDiscoveryUiState> = discoveryCoordinator.state
        .map { state -> state.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = discoveryCoordinator.state.value.toUiState(),
        )

    fun retry() {
        discoveryCoordinator.retry()
    }
}
