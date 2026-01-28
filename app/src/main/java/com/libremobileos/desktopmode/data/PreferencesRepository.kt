package com.libremobileos.desktopmode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "desktop_mode_settings")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val AUTO_RES = booleanPreferencesKey("pc_mode_auto_resolution")
        val RES_WIDTH = intPreferencesKey("pc_mode_res_width")
        val RES_HEIGHT = intPreferencesKey("pc_mode_res_height")
        val SCALING = intPreferencesKey("pc_mode_scaling")
        val EMULATE_TOUCH = booleanPreferencesKey("pc_mode_emulate_touch")
        val AUDIO = booleanPreferencesKey("pc_mode_audio")
        
        val MIRROR_INTERNAL = booleanPreferencesKey("pc_mode_mirror_internal")
        val RELATIVE_INPUT = booleanPreferencesKey("pc_mode_relative_input")
        val REMOTE_CURSOR = booleanPreferencesKey("pc_mode_remote_cursor")
        val CLIPBOARD = booleanPreferencesKey("pc_mode_clipboard")
    }

    val autoRes: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_RES] ?: true }
    val width: Flow<Int> = context.dataStore.data.map { it[Keys.RES_WIDTH] ?: 1920 }
    val height: Flow<Int> = context.dataStore.data.map { it[Keys.RES_HEIGHT] ?: 1080 }
    val scaling: Flow<Int> = context.dataStore.data.map { it[Keys.SCALING] ?: 100 }
    val emulateTouch: Flow<Boolean> = context.dataStore.data.map { it[Keys.EMULATE_TOUCH] ?: false }
    val audio: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUDIO] ?: true }
    
    val mirrorInternal: Flow<Boolean> = context.dataStore.data.map { it[Keys.MIRROR_INTERNAL] ?: false }
    val relativeInput: Flow<Boolean> = context.dataStore.data.map { it[Keys.RELATIVE_INPUT] ?: false }
    val remoteCursor: Flow<Boolean> = context.dataStore.data.map { it[Keys.REMOTE_CURSOR] ?: true }
    val clipboard: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLIPBOARD] ?: true }

    suspend fun setAutoRes(value: Boolean) = context.dataStore.edit { it[Keys.AUTO_RES] = value }
    suspend fun setResolution(w: Int, h: Int) = context.dataStore.edit { 
        it[Keys.RES_WIDTH] = w 
        it[Keys.RES_HEIGHT] = h 
    }
    suspend fun setScaling(value: Int) = context.dataStore.edit { it[Keys.SCALING] = value }
    suspend fun setEmulateTouch(value: Boolean) = context.dataStore.edit { it[Keys.EMULATE_TOUCH] = value }
    suspend fun setAudio(value: Boolean) = context.dataStore.edit { it[Keys.AUDIO] = value }
    
    suspend fun setMirrorInternal(value: Boolean) = context.dataStore.edit { it[Keys.MIRROR_INTERNAL] = value }
    suspend fun setRelativeInput(value: Boolean) = context.dataStore.edit { it[Keys.RELATIVE_INPUT] = value }
    suspend fun setRemoteCursor(value: Boolean) = context.dataStore.edit { it[Keys.REMOTE_CURSOR] = value }
    suspend fun setClipboard(value: Boolean) = context.dataStore.edit { it[Keys.CLIPBOARD] = value }
}
