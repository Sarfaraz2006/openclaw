package ai.openclaw.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ai.openclaw.android.MainViewModel

@Composable
fun RootScreen(viewModel: MainViewModel) {
  val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
  val pendingActionConfirmation by viewModel.pendingActionConfirmation.collectAsState()

  if (!onboardingCompleted) {
    OnboardingFlow(viewModel = viewModel, modifier = Modifier.fillMaxSize())
  } else {
    PostOnboardingTabs(viewModel = viewModel, modifier = Modifier.fillMaxSize())
  }

  if (pendingActionConfirmation != null) {
    val prompt = pendingActionConfirmation!!
    AlertDialog(
      onDismissRequest = { viewModel.declineActionConfirmationPrompt() },
      title = { Text(prompt.title) },
      text = { Text("${prompt.command}\n\n${prompt.message}") },
      confirmButton = {
        TextButton(onClick = { viewModel.acceptActionConfirmationPrompt() }) {
          Text("Allow")
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.declineActionConfirmationPrompt() }) {
          Text("Deny")
        }
      },
    )
  }
}
