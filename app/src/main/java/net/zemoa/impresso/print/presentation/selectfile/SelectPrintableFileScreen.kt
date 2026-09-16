package net.zemoa.impresso.print.presentation.selectfile

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import net.zemoa.impresso.R
import net.zemoa.impresso.print.domain.FileSelectionError
import net.zemoa.impresso.print.domain.PrintableFileType

@Composable
fun SelectPrintableFileScreen(
    viewModel: SelectPrintableFileViewModel,
    onContinue: (SelectPrintableFileEffect.Continue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            viewModel.onFilePicked(uri)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            if (effect is SelectPrintableFileEffect.Continue) {
                onContinue(effect)
            }
        }
    }

    SelectPrintableFileContent(
        uiState = uiState,
        onChooseFile = {
            try {
                picker.launch(PrintableFileType.supportedMimeTypes)
            } catch (_: ActivityNotFoundException) {
                viewModel.onPickerUnavailable()
            }
        },
        onContinue = viewModel::onContinue,
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SelectPrintableFileContent(
    uiState: SelectPrintableFileUiState,
    onChooseFile: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.select_file_title)) })
        },
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.select_file_description),
                style = MaterialTheme.typography.bodyLarge,
            )

            uiState.selectedFile?.let { file ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.selected_file),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = file.displayName,
                            modifier = Modifier.testTag("selected-file-name"),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(
                                R.string.selected_file_type,
                                printableFileTypeLabel(file.type),
                            ),
                            modifier = Modifier.testTag("selected-file-type"),
                        )
                    }
                }
            }

            if (uiState.isValidating) {
                CircularProgressIndicator(modifier = Modifier.testTag("file-validation-loading"))
                Text(stringResource(R.string.validating_file))
            }

            uiState.error?.let { error ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = fileSelectionErrorMessage(error),
                        modifier = Modifier.padding(16.dp).testTag("file-selection-error"),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            if (uiState.selectedFile == null) {
                Button(
                    onClick = onChooseFile,
                    enabled = !uiState.isValidating,
                    modifier = Modifier.fillMaxWidth().testTag("choose-file"),
                ) {
                    Text(stringResource(R.string.choose_file))
                }
            } else {
                OutlinedButton(
                    onClick = onChooseFile,
                    enabled = !uiState.isValidating,
                    modifier = Modifier.fillMaxWidth().testTag("choose-another-file"),
                ) {
                    Text(stringResource(R.string.choose_another_file))
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier.fillMaxWidth().testTag("continue"),
            ) {
                Text(stringResource(R.string.continue_to_printers))
            }
            if (uiState.selectedFile == null && uiState.error == null) {
                Text(
                    text = stringResource(R.string.continue_requires_file),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun printableFileTypeLabel(type: PrintableFileType): String = when (type) {
    PrintableFileType.PDF -> stringResource(R.string.file_type_pdf)
    PrintableFileType.JPEG -> stringResource(R.string.file_type_jpeg)
    PrintableFileType.PNG -> stringResource(R.string.file_type_png)
    PrintableFileType.GIF -> stringResource(R.string.file_type_gif)
    PrintableFileType.WEBP -> stringResource(R.string.file_type_webp)
    PrintableFileType.DOCX -> stringResource(R.string.file_type_docx)
    PrintableFileType.ODT -> stringResource(R.string.file_type_odt)
}

@Composable
private fun fileSelectionErrorMessage(error: FileSelectionError): String = when (error) {
    FileSelectionError.UNSUPPORTED_TYPE -> stringResource(R.string.file_error_unsupported)
    FileSelectionError.INACCESSIBLE_FILE -> stringResource(R.string.file_error_unavailable)
    FileSelectionError.UNAVAILABLE_METADATA -> stringResource(R.string.file_error_metadata)
    FileSelectionError.PICKER_UNAVAILABLE -> stringResource(R.string.file_error_picker_unavailable)
}
