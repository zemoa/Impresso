package net.zemoa.impresso.print.presentation.selectfile

import android.net.Uri
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.zemoa.impresso.MainDispatcherRule
import net.zemoa.impresso.print.domain.FileReferenceValidator
import net.zemoa.impresso.print.domain.FileSelectionError
import net.zemoa.impresso.print.domain.FileValidationResult
import net.zemoa.impresso.print.domain.PrintableFile
import net.zemoa.impresso.print.domain.PrintableFileType
import net.zemoa.impresso.print.domain.UriPermissionManager
import net.zemoa.impresso.print.domain.ValidatePrintableFileUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SelectPrintableFileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `accepting a file enables continuation`() = runTest {
        val file = printableFile("content://documents/file.pdf")
        val viewModel = viewModel { FileValidationResult.Success(file) }

        viewModel.onFilePicked(file.uri)
        advanceUntilIdle()

        assertEquals(file, viewModel.uiState.value.selectedFile)
        assertTrue(viewModel.uiState.value.canContinue)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `rejected replacement preserves the active selection`() = runTest {
        val original = printableFile("content://documents/original.pdf")
        val replacement = Uri.parse("content://documents/replacement.txt")
        val calls = AtomicInteger()
        val viewModel = viewModel {
            if (calls.incrementAndGet() == 1) {
                FileValidationResult.Success(original)
            } else {
                FileValidationResult.Failure(FileSelectionError.UNSUPPORTED_TYPE)
            }
        }

        viewModel.onFilePicked(original.uri)
        advanceUntilIdle()
        viewModel.onFilePicked(replacement)
        advanceUntilIdle()

        assertEquals(original, viewModel.uiState.value.selectedFile)
        assertEquals(FileSelectionError.UNSUPPORTED_TYPE, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `continuing revalidates and emits the typed file handoff`() = runTest {
        val file = printableFile("content://documents/file.pdf")
        val calls = AtomicInteger()
        val viewModel = viewModel {
            calls.incrementAndGet()
            FileValidationResult.Success(file)
        }

        viewModel.onFilePicked(file.uri)
        advanceUntilIdle()
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }
        viewModel.onContinue()
        advanceUntilIdle()

        assertEquals(2, calls.get())
        assertEquals(SelectPrintableFileEffect.Continue(file), effect.await())
    }

    @Test
    fun `an inaccessible file before continuation keeps the user on selection`() = runTest {
        val file = printableFile("content://documents/file.pdf")
        val calls = AtomicInteger()
        val viewModel = viewModel {
            if (calls.incrementAndGet() == 1) {
                FileValidationResult.Success(file)
            } else {
                FileValidationResult.Failure(FileSelectionError.INACCESSIBLE_FILE)
            }
        }

        viewModel.onFilePicked(file.uri)
        advanceUntilIdle()
        viewModel.onContinue()
        advanceUntilIdle()

        assertEquals(file, viewModel.uiState.value.selectedFile)
        assertEquals(FileSelectionError.INACCESSIBLE_FILE, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isValidating)
    }

    private fun viewModel(validate: suspend (Uri) -> FileValidationResult): SelectPrintableFileViewModel =
        SelectPrintableFileViewModel(
            validatePrintableFile = ValidatePrintableFileUseCase(
                object : FileReferenceValidator {
                    override suspend fun validate(uri: Uri): FileValidationResult = validate(uri)
                },
            ),
            uriPermissionManager = object : UriPermissionManager {
                override fun takeReadPermission(uri: Uri): Boolean = false

                override fun releaseReadPermission(uri: Uri) = Unit
            },
        )

    private fun printableFile(uri: String) = PrintableFile(
        uri = Uri.parse(uri),
        displayName = "file.pdf",
        type = PrintableFileType.PDF,
        declaredMimeType = "application/pdf",
    )
}
