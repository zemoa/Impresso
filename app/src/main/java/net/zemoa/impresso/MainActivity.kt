package net.zemoa.impresso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.zemoa.impresso.app.AppGraph
import net.zemoa.impresso.app.PrintFlowViewModel
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileEffect
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileScreen
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileViewModel

class MainActivity : ComponentActivity() {
    private val appGraph by lazy { AppGraph(applicationContext) }
    private val selectPrintableFileViewModel: SelectPrintableFileViewModel by viewModels {
        appGraph.selectPrintableFileViewModelFactory
    }
    private val printFlowViewModel: PrintFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appGraph.discoveryCoordinator.start()
        setContent {
            MaterialTheme {
                ImpressoApp(selectPrintableFileViewModel, printFlowViewModel)
            }
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            appGraph.discoveryCoordinator.stop()
        }
        super.onDestroy()
    }
}

@Composable
private fun ImpressoApp(
    selectPrintableFileViewModel: SelectPrintableFileViewModel,
    printFlowViewModel: PrintFlowViewModel,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "select-file") {
        composable("select-file") {
            SelectPrintableFileScreen(
                viewModel = selectPrintableFileViewModel,
                onContinue = { result: SelectPrintableFileEffect.Continue ->
                    printFlowViewModel.selectFile(result.file)
                    navController.navigate("printer-discovery")
                },
            )
        }
        composable("printer-discovery") {
            PrinterDiscoveryPlaceholder(printFlowViewModel)
        }
    }
}

@Composable
private fun PrinterDiscoveryPlaceholder(printFlowViewModel: PrintFlowViewModel) {
    val selectedFile by printFlowViewModel.selectedFile.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.printer_discovery_placeholder_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(stringResource(R.string.printer_discovery_placeholder_description))
        selectedFile?.let { file ->
            Text(stringResource(R.string.printer_discovery_selected_file, file.displayName))
        }
    }
}
