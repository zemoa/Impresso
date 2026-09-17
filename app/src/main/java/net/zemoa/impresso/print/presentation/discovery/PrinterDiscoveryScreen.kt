package net.zemoa.impresso.print.presentation.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.zemoa.impresso.R
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus

@Composable
fun PrinterDiscoveryScreen(
    viewModel: PrinterDiscoveryViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    PrinterDiscoveryContent(
        uiState = uiState.value,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun PrinterDiscoveryContent(
    uiState: PrinterDiscoveryUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.printer_discovery_title)) })
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
                text = stringResource(R.string.printer_discovery_description),
                style = MaterialTheme.typography.bodyLarge,
            )
            when {
                uiState.status == PrinterDiscoveryStatus.WIFI_FAILURE -> WifiFailure(onRetry)
                uiState.status == PrinterDiscoveryStatus.NO_RESULTS -> NoResults(onRetry)
                else -> PrinterResults(uiState)
            }
        }
    }
}

@Composable
private fun PrinterResults(uiState: PrinterDiscoveryUiState) {
    if (uiState.printers.isEmpty()) {
        if (uiState.isDiscovering || uiState.status == PrinterDiscoveryStatus.NOT_STARTED) {
            CircularProgressIndicator(modifier = Modifier.testTag("printer-discovery-loading"))
            Text(stringResource(R.string.printer_discovery_loading))
        }
        return
    }

    if (uiState.isDiscovering) {
        Text(stringResource(R.string.printer_discovery_loading))
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("printer-discovery-results"),
    ) {
        items(uiState.printers, key = { printer -> printer.id }) { printer ->
            PrinterCard(printer)
        }
    }
}

@Composable
private fun WifiFailure(onRetry: () -> Unit) {
    Text(
        text = stringResource(R.string.printer_discovery_wifi_failure),
        modifier = Modifier.testTag("printer-discovery-wifi-failure"),
        color = MaterialTheme.colorScheme.error,
    )
    RetryButton(onRetry)
}

@Composable
private fun NoResults(onRetry: () -> Unit) {
    Text(
        text = stringResource(R.string.printer_discovery_no_results),
        modifier = Modifier.testTag("printer-discovery-no-results"),
    )
    RetryButton(onRetry)
}

@Composable
private fun RetryButton(onRetry: () -> Unit) {
    Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth().testTag("retry-printer-discovery"),
    ) {
        Text(stringResource(R.string.retry_printer_discovery))
    }
}

@Composable
private fun PrinterCard(printer: DiscoveredPrinter) {
    Card(modifier = Modifier.fillMaxWidth().testTag("discovered-printer-${printer.id}")) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = printer.name ?: printer.model ?: printer.networkAddress.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
            )
            printer.model?.let { model ->
                Text(stringResource(R.string.printer_discovery_model, model))
            }
            printer.networkAddress?.let { address ->
                Text(stringResource(R.string.printer_discovery_address, address))
            }
            Text(
                text = when (printer.availability) {
                    PrinterAvailability.ACTIVE -> stringResource(R.string.printer_discovery_active)
                    PrinterAvailability.INACTIVE -> stringResource(R.string.printer_discovery_inactive)
                },
                color = if (printer.availability == PrinterAvailability.ACTIVE) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
    }
}
