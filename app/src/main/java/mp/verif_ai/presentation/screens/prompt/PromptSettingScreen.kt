package mp.verif_ai.presentation.screens.prompt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.viewmodel.prompt.PromptSettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptSettingsScreen(
    onComplete: () -> Unit,
    viewModel: PromptSettingsViewModel = hiltViewModel()
) {
    var apiKey by remember { mutableStateOf("") }
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prompt Settings") },
                navigationIcon = {
                    IconButton(onClick = onComplete) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key Section
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("OpenAI API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            // Model Selection
            DropdownField(
                label = "Model",
                options = listOf("GPT-4", "GPT-3.5-turbo"),
                selectedOption = settings.model,
                onOptionSelected = { viewModel.updateModel(it) }
            )

            // Temperature Setting
            var temperature by remember { mutableStateOf(settings.temperature) }
            Text("Temperature: ${temperature.format(2)}")
            Slider(
                value = temperature,
                onValueChange = { temperature = it },
                valueRange = 0f..1f,
                onValueChangeFinished = { viewModel.updateTemperature(temperature) }
            )

            // Max Tokens Setting
            var maxTokens by remember { mutableStateOf(settings.maxTokens.toString()) }
            OutlinedTextField(
                value = maxTokens,
                onValueChange = {
                    maxTokens = it
                    it.toIntOrNull()?.let { tokens ->
                        viewModel.updateMaxTokens(tokens)
                    }
                },
                label = { Text("Max Tokens") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveApiKey(apiKey)
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
        }
    }
}


@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)