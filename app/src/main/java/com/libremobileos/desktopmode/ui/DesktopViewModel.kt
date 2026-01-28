package com.libremobileos.desktopmode.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.libremobileos.desktopmode.data.PreferencesRepository
import com.libremobileos.desktopmode.service.VncController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DesktopViewModel(application: Application) : AndroidViewModel(application) {
    
    private val prefs = PreferencesRepository(application)
    private val vncController = VncController(application)

    val uiState = combine(
        listOf(
            prefs.autoRes,          // 0
            prefs.width,            // 1
            prefs.height,           // 2
            prefs.scaling,          // 3
            prefs.emulateTouch,     // 4
            prefs.audio,            // 5
            prefs.mirrorInternal,   // 6
            prefs.relativeInput,    // 7
            prefs.remoteCursor,     // 8
            prefs.clipboard,        // 9
            vncController.isRunning // 10
        )
    ) { args ->
        DesktopUiState(
            autoRes = args[0] as Boolean,
            width = args[1] as Int,
            height = args[2] as Int,
            scaling = args[3] as Int,
            emulateTouch = args[4] as Boolean,
            audio = args[5] as Boolean,
            mirrorInternal = args[6] as Boolean,
            relativeInput = args[7] as Boolean,
            remoteCursor = args[8] as Boolean,
            clipboard = args[9] as Boolean,
            isRunning = args[10] as Boolean
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DesktopUiState())

    fun toggleService() {
        val state = uiState.value
        val dpi = 160 * state.scaling / 100
        
        vncController.toggleService(
            enable = !state.isRunning,
            width = state.width,
            height = state.height,
            dpi = dpi,
            autoResize = state.autoRes,
            emulateTouch = state.emulateTouch,
            audio = state.audio,
            mirrorInternal = state.mirrorInternal,
            relativeInput = state.relativeInput,
            remoteCursor = state.remoteCursor,
            clipboard = state.clipboard
        )
    }

    fun setAutoRes(v: Boolean) = viewModelScope.launch { prefs.setAutoRes(v) }
    fun setScaling(v: Int) = viewModelScope.launch { prefs.setScaling(v) }
    fun setAudio(v: Boolean) = viewModelScope.launch { prefs.setAudio(v) }
    fun setTouch(v: Boolean) = viewModelScope.launch { prefs.setEmulateTouch(v) }
    fun setMirror(v: Boolean) = viewModelScope.launch { prefs.setMirrorInternal(v) }
    fun setRelative(v: Boolean) = viewModelScope.launch { prefs.setRelativeInput(v) }
    fun setCursor(v: Boolean) = viewModelScope.launch { prefs.setRemoteCursor(v) }
    fun setClip(v: Boolean) = viewModelScope.launch { prefs.setClipboard(v) }
    
    fun setResolution(w: String, h: String) = viewModelScope.launch {
        val width = w.toIntOrNull() ?: 1920
        val height = h.toIntOrNull() ?: 1080
        prefs.setResolution(width, height)
    }
    
    fun refreshStatus() = vncController.checkStatus()

    override fun onCleared() {
        super.onCleared()
        vncController.unbind()
    }
}

data class DesktopUiState(
    val autoRes: Boolean = true,
    val width: Int = 1920,
    val height: Int = 1080,
    val scaling: Int = 100,
    val emulateTouch: Boolean = false,
    val audio: Boolean = true,
    val mirrorInternal: Boolean = false,
    val relativeInput: Boolean = false,
    val remoteCursor: Boolean = true,
    val clipboard: Boolean = true,
    val isRunning: Boolean = false
)
