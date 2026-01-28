package com.libremobileos.desktopmode.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.libremobileos.desktopmode.data.PreferencesRepository
import com.libremobileos.desktopmode.service.VncController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class DesktopModeTileService : TileService() {

    private var vncController: VncController? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var prefs: PreferencesRepository

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesRepository(this)
        vncController = VncController(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        vncController?.unbind()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        val isCurrentlyActive = qsTile.state == Tile.STATE_ACTIVE
        
        qsTile.state = if (isCurrentlyActive) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        qsTile.updateTile()

        serviceScope.launch {
            if (isCurrentlyActive) {
                vncController?.toggleService(
                    enable = false, 
                    width = 0, height = 0, dpi = 0, 
                    autoResize = false, emulateTouch = false, audio = false,
                    mirrorInternal = false, relativeInput = false, remoteCursor = false, clipboard = false
                )
            } else {
                val autoRes = prefs.autoRes.first()
                val w = prefs.width.first()
                val h = prefs.height.first()
                val scale = prefs.scaling.first()
                val touch = prefs.emulateTouch.first()
                val audio = prefs.audio.first()
                val mirror = prefs.mirrorInternal.first()
                val relative = prefs.relativeInput.first()
                val cursor = prefs.remoteCursor.first()
                val clip = prefs.clipboard.first()
                
                val dpi = 160 * scale / 100
                
                vncController?.toggleService(
                    enable = true, 
                    width = w, 
                    height = h, 
                    dpi = dpi, 
                    autoResize = autoRes, 
                    emulateTouch = touch, 
                    audio = audio,
                    mirrorInternal = mirror,
                    relativeInput = relative,
                    remoteCursor = cursor,
                    clipboard = clip
                )
            }
            
            delay(500)
            updateTileState()
        }
    }

    private fun updateTileState() {
        serviceScope.launch {
            vncController?.checkStatus()
            delay(100) 
            val isRunning = vncController?.isRunning?.first() ?: false
            qsTile.state = if (isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                qsTile.subtitle = if (isRunning) "Active" else "Off"
            }
            qsTile.updateTile()
        }
    }
}
