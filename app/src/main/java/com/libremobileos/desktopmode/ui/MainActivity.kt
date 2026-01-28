package com.libremobileos.desktopmode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.libremobileos.desktopmode.ui.DesktopModeScreen
import com.libremobileos.desktopmode.ui.DesktopViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DesktopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            DesktopModeTheme {
                DesktopModeScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshStatus()
    }
}

@Composable
fun DesktopModeTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) dynamicDarkColorScheme(androidx.compose.ui.platform.LocalContext.current)
                      else dynamicLightColorScheme(androidx.compose.ui.platform.LocalContext.current)

    MaterialTheme(colorScheme = colorScheme, content = content)
}
