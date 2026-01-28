package com.libremobileos.desktopmode.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.libremobileos.vncflinger.IVncFlinger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class VncController(private val context: Context) {
    
    private var vncService: IVncFlinger? = null
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()
    private val isBound = AtomicBoolean(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            vncService = IVncFlinger.Stub.asInterface(binder)
            checkStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            vncService = null
            _isRunning.value = false
        }
    }

    init {
        bind()
    }

    private fun bind() {
        if (isBound.get()) return
        val intent = Intent().apply {
            component = ComponentName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger")
        }
        try {
            val success = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            isBound.set(success)
        } catch (e: Exception) {
            Log.e("VncController", "Failed to bind", e)
        }
    }

    fun unbind() {
        if (isBound.compareAndSet(true, false)) {
            try {
                context.unbindService(serviceConnection)
                vncService = null
            } catch (e: Exception) {
                Log.e("VncController", "Failed to unbind", e)
            }
        }
    }

    fun checkStatus() {
        try {
            _isRunning.value = vncService?.isRunning == true
        } catch (e: Exception) {
            _isRunning.value = false
        }
    }

    fun toggleService(
        enable: Boolean,
        width: Int, height: Int, dpi: Int,
        autoResize: Boolean, emulateTouch: Boolean, audio: Boolean,
        mirrorInternal: Boolean, relativeInput: Boolean, remoteCursor: Boolean, clipboard: Boolean
    ) {
        val intent = Intent().apply {
            component = ComponentName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger")
        }

        if (enable) {
            bind()
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.putExtra("dpi", dpi)
            intent.putExtra("allowResize", autoResize)
            intent.putExtra("emulateTouch", emulateTouch)
            intent.putExtra("hasAudio", audio)
            intent.putExtra("mirrorInternal", mirrorInternal)
            intent.putExtra("useRelativeInput", relativeInput)
            intent.putExtra("remoteCursor", remoteCursor)
            intent.putExtra("clipboard", clipboard)
            intent.putExtra("intentEnable", true)
            intent.putExtra("intentPkg", context.packageName)
            intent.putExtra("intentComponent", "${context.packageName}.MainActivity")
            
            context.startService(intent)
        } else {
            unbind() 
            context.stopService(intent)
            _isRunning.value = false
        }
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (enable) checkStatus() 
        }, 500)
    }
}
