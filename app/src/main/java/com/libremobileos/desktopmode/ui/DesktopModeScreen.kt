package com.libremobileos.desktopmode.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopModeScreen(viewModel: DesktopViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Desktop Mode") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Status Card
            StatusCard(
                isRunning = uiState.isRunning,
                onToggle = { viewModel.toggleService() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 3. Display Settings Header
            Text(
                text = "Display Settings", 
                style = MaterialTheme.typography.titleMedium, 
                color = MaterialTheme.colorScheme.primary
            )

            // Mirror Internal Toggle
            ListItem(
                headlineContent = { Text("Mirror Internal Display") },
                supportingContent = { Text("Show phone screen instead of creating a virtual desktop") },
                leadingContent = { Icon(Icons.Default.Screenshot, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.mirrorInternal, 
                        onCheckedChange = { viewModel.setMirror(it) }
                    )
                }
            )

            // Resolution Settings (Hidden if Mirroring is ON)
            AnimatedVisibility(visible = !uiState.mirrorInternal) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ListItem(
                        headlineContent = { Text("Auto Resolution") },
                        supportingContent = { Text("Match host display size automatically") },
                        trailingContent = {
                            Switch(
                                checked = uiState.autoRes, 
                                onCheckedChange = { viewModel.setAutoRes(it) }
                            )
                        }
                    )

                    // Custom Resolution Inputs (Visible only if Auto Res is OFF)
                    AnimatedVisibility(visible = !uiState.autoRes) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = uiState.width.toString(),
                                onValueChange = { viewModel.setResolution(it, uiState.height.toString()) },
                                label = { Text("Width") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = uiState.height.toString(),
                                onValueChange = { viewModel.setResolution(uiState.width.toString(), it) },
                                label = { Text("Height") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    // Scaling Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "DPI Scaling", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "${uiState.scaling}%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = uiState.scaling.toFloat(),
                            onValueChange = { viewModel.setScaling(it.toInt()) },
                            valueRange = 50f..200f,
                            steps = 14
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 4. Input & Audio Header
            Text(
                text = "Input & Audio", 
                style = MaterialTheme.typography.titleMedium, 
                color = MaterialTheme.colorScheme.primary
            )

            // Audio Toggle
            ListItem(
                headlineContent = { Text("Audio Forwarding") },
                supportingContent = { Text("Route device audio to PC") },
                leadingContent = { Icon(Icons.Default.VolumeUp, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.audio, 
                        onCheckedChange = { viewModel.setAudio(it) }
                    )
                }
            )
            
            // Touch Emulation Toggle
            ListItem(
                headlineContent = { Text("Touch Emulation") },
                supportingContent = { Text("Treat mouse clicks as touch events") },
                leadingContent = { Icon(Icons.Default.TouchApp, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.emulateTouch, 
                        onCheckedChange = { viewModel.setTouch(it) }
                    )
                }
            )

            // Relative Input
            ListItem(
                headlineContent = { Text("Relative Mouse Input") },
                supportingContent = { Text("Capture mouse for gaming/precision") },
                leadingContent = { Icon(Icons.Default.Mouse, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.relativeInput, 
                        onCheckedChange = { viewModel.setRelative(it) }
                    )
                }
            )

            // Remote Cursor
            ListItem(
                headlineContent = { Text("Show Remote Cursor") },
                supportingContent = { Text("Render cursor on the host PC") },
                leadingContent = { Icon(Icons.Default.Mouse, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.remoteCursor, 
                        onCheckedChange = { viewModel.setCursor(it) }
                    )
                }
            )

            // Clipboard Sync
            ListItem(
                headlineContent = { Text("Clipboard Sync") },
                supportingContent = { Text("Share text between phone and PC") },
                leadingContent = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = uiState.clipboard, 
                        onCheckedChange = { viewModel.setClip(it) }
                    )
                }
            )
            
            // Bottom spacing to ensure scrolling clears navigation bar
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatusCard(isRunning: Boolean, onToggle: () -> Unit) {
    val containerColor = if (isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val onContainerColor = if (isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val icon = if (isRunning) Icons.Rounded.CastConnected else Icons.Rounded.PowerSettingsNew
    val text = if (isRunning) "Desktop Mode Active" else "Ready to Connect"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(32.dp),
                    tint = onContainerColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = text, 
                    style = MaterialTheme.typography.headlineSmall,
                    color = onContainerColor
                )
            }
            
            Button(
                onClick = onToggle,
                modifier = Modifier.align(Alignment.BottomEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRunning) "Stop" else "Start")
            }
        }
    }
}
