package net.zemoa.impresso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.zemoa.impresso.app.AppGraph
import net.zemoa.impresso.app.PrintFlowViewModel
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileEffect
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileScreen
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileViewModel

class MainActivity : ComponentActivity() {
    private val appGraph: AppGraph
        get() = (application as ImpressoApplication).appGraph
    private val selectPrintableFileViewModel: SelectPrintableFileViewModel by viewModels {
        appGraph.selectPrintableFileViewModelFactory
    }
    private val printFlowViewModel: PrintFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ImpressoApp(selectPrintableFileViewModel, printFlowViewModel)
            }
        }
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
                },
            )
        }
    }
}
