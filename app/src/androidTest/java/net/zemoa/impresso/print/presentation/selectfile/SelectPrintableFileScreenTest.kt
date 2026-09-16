package net.zemoa.impresso.print.presentation.selectfile

import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import net.zemoa.impresso.print.domain.PrintableFile
import net.zemoa.impresso.print.domain.PrintableFileType
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SelectPrintableFileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun initialStateExplainsSelectionAndDisablesContinue() {
        setContent(SelectPrintableFileUiState())

        composeRule.onNodeWithText("Choose a file").assertIsDisplayed()
        composeRule.onNodeWithText("Choose a file to continue.").assertIsDisplayed()
        composeRule.onNodeWithTag("continue").assertIsNotEnabled()
    }

    @Test
    fun selectedFileDisplaysNameTypeAndEnablesContinue() {
        setContent(
            SelectPrintableFileUiState(
                selectedFile = PrintableFile(
                    uri = Uri.parse("content://documents/report.pdf"),
                    displayName = "report.pdf",
                    type = PrintableFileType.PDF,
                    declaredMimeType = "application/pdf",
                ),
            ),
        )

        composeRule.onNodeWithTag("selected-file-name").assertIsDisplayed()
        composeRule.onNodeWithText("report.pdf").assertIsDisplayed()
        composeRule.onNodeWithTag("selected-file-type").assertIsDisplayed()
        composeRule.onNodeWithText("Type: PDF document").assertIsDisplayed()
        composeRule.onNodeWithTag("choose-another-file").assertIsEnabled()
        composeRule.onNodeWithTag("continue").assertIsEnabled()
    }

    @Test
    fun validationPreventsDuplicateActions() {
        setContent(
            SelectPrintableFileUiState(
                selectedFile = PrintableFile(
                    uri = Uri.parse("content://documents/report.pdf"),
                    displayName = "report.pdf",
                    type = PrintableFileType.PDF,
                    declaredMimeType = "application/pdf",
                ),
                isValidating = true,
            ),
        )

        composeRule.onNodeWithTag("file-validation-loading").assertIsDisplayed()
        composeRule.onNodeWithTag("choose-another-file").assertIsNotEnabled()
        composeRule.onNodeWithTag("continue").assertIsNotEnabled()
    }

    @Test
    fun chooseFileDispatchesThePickerRequest() {
        var requested = false
        setContent(SelectPrintableFileUiState(), onChooseFile = { requested = true })

        composeRule.onNodeWithTag("choose-file").performClick()

        assertTrue(requested)
    }

    private fun setContent(
        state: SelectPrintableFileUiState,
        onChooseFile: () -> Unit = {},
    ) {
        composeRule.setContent {
            MaterialTheme {
                SelectPrintableFileContent(
                    uiState = state,
                    onChooseFile = onChooseFile,
                    onContinue = {},
                )
            }
        }
    }
}
