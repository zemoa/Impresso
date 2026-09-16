package net.zemoa.impresso.print.presentation.selectfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.zemoa.impresso.print.domain.FileSelectionError
import net.zemoa.impresso.print.domain.FileValidationResult
import net.zemoa.impresso.print.domain.PrintableFile
import net.zemoa.impresso.print.domain.UriPermissionManager
import net.zemoa.impresso.print.domain.ValidatePrintableFileUseCase

class SelectPrintableFileViewModel(
    private val validatePrintableFile: ValidatePrintableFileUseCase,
    private val uriPermissionManager: UriPermissionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectPrintableFileUiState())
    val uiState: StateFlow<SelectPrintableFileUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SelectPrintableFileEffect>()
    val effects: SharedFlow<SelectPrintableFileEffect> = _effects.asSharedFlow()

    private val retainedUris = mutableSetOf<Uri>()

    fun onFilePicked(uri: Uri) {
        if (_uiState.value.isValidating) {
            return
        }

        val tookPermission = uriPermissionManager.takeReadPermission(uri)
        if (tookPermission) {
            retainedUris += uri
        }
        validate(uri, releaseOnFailure = tookPermission)
    }

    fun onPickerUnavailable() {
        if (!_uiState.value.isValidating) {
            _uiState.value = _uiState.value.copy(error = FileSelectionError.PICKER_UNAVAILABLE)
        }
    }

    fun onContinue() {
        val selectedFile = _uiState.value.selectedFile ?: return
        if (_uiState.value.isValidating) {
            return
        }

        _uiState.value = _uiState.value.copy(isValidating = true, error = null)
        viewModelScope.launch {
            when (val result = validatePrintableFile(selectedFile.uri)) {
                is FileValidationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        selectedFile = result.file,
                        isValidating = false,
                    )
                    _effects.emit(SelectPrintableFileEffect.Continue(result.file))
                }

                is FileValidationResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isValidating = false,
                        error = result.error,
                    )
                }
            }
        }
    }

    private fun validate(uri: Uri, releaseOnFailure: Boolean) {
        val previousSelection = _uiState.value.selectedFile
        _uiState.value = _uiState.value.copy(isValidating = true, error = null)
        viewModelScope.launch {
            when (val result = validatePrintableFile(uri)) {
                is FileValidationResult.Success -> {
                    releaseReplacedSelection(previousSelection, result.file)
                    _uiState.value = SelectPrintableFileUiState(selectedFile = result.file)
                }

                is FileValidationResult.Failure -> {
                    if (releaseOnFailure) {
                        releasePermission(uri)
                    }
                    _uiState.value = SelectPrintableFileUiState(
                        selectedFile = previousSelection,
                        error = result.error,
                    )
                }
            }
        }
    }

    private fun releaseReplacedSelection(previous: PrintableFile?, replacement: PrintableFile) {
        if (previous != null && previous.uri != replacement.uri) {
            releasePermission(previous.uri)
        }
    }

    private fun releasePermission(uri: Uri) {
        if (retainedUris.remove(uri)) {
            uriPermissionManager.releaseReadPermission(uri)
        }
    }

    override fun onCleared() {
        retainedUris.toList().forEach(::releasePermission)
        super.onCleared()
    }
}
