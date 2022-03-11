/*
 * Copyright (C) 2022 Otack Kurano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.copin.nightmodetoggle

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalTime

object NightModeHelper {
    private const val SETTINGS_SECURE_KEY_UI_NIGHT_MODE = "ui_night_mode"
    private const val SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_OFF = "ui_night_mode_override_off"
    private const val SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_ON = "ui_night_mode_override_on"

    fun isInNightMode(context: Context): Boolean {
        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_NO -> {
                return false
            }
            UiModeManager.MODE_NIGHT_YES -> {
                return true
            }
            UiModeManager.MODE_NIGHT_AUTO -> {
                return context.resources.getBoolean(R.bool.night_mode)
            }
            UiModeManager.MODE_NIGHT_CUSTOM -> {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (isInNightModeCustom(context)) {
                        // Night Mode is currently ON.
                        Settings.Secure.getInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_OFF, 0) == 0
                    } else {
                        // Night Mode is currently OFF.
                        Settings.Secure.getInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_ON, 0) != 0
                    }
                } else {
                    context.resources.getBoolean(R.bool.night_mode)
                }
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun isInNightModeCustom(context: Context): Boolean {
        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        val current = LocalTime.now()
        val start = uiModeManager.customNightModeStart
        val end = uiModeManager.customNightModeEnd
        return (start <= end && start <= current && current < end) ||
                (end < start && current < end || start <= current)
    }

    fun toggleNightMode(context: Context) {
        if (!checkPermissionGranted(context)) {
            Toast.makeText(context, R.string.permission_not_granted, Toast.LENGTH_LONG).show()
            return
        }

        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_NO -> {
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE,
                    UiModeManager.MODE_NIGHT_YES)
            }
            UiModeManager.MODE_NIGHT_YES -> {
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE,
                    UiModeManager.MODE_NIGHT_NO)
            }
            UiModeManager.MODE_NIGHT_AUTO -> {
                toggleNightModeAuto(context)
            }
            UiModeManager.MODE_NIGHT_CUSTOM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    toggleNightModeOverride(context)
                } else {
                    toggleNightModeAuto(context)
                }
            }
        }
        restartSystemUi(context)
    }

    private fun toggleNightModeAuto(context: Context) {
        if (context.resources.getBoolean(R.bool.night_mode)) {
            Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE, UiModeManager.MODE_NIGHT_NO)
        } else {
            Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE, UiModeManager.MODE_NIGHT_YES)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun toggleNightModeOverride(context: Context) {
        if (isInNightModeCustom(context)) {
            // Night Mode is currently ON.
            if (Settings.Secure.getInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_OFF, 0) != 0) {
                // Reset if already overridden.
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_OFF, 0)
            } else {
                // Otherwise override.
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_OFF, 1)
            }
        } else {
            // Night Mode is currently OFF.
            if (Settings.Secure.getInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_ON, 0) != 0) {
                // Reset if already overridden.
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_ON, 0)
            } else {
                // Otherwise override.
                Settings.Secure.putInt(context.contentResolver, SETTINGS_SECURE_KEY_UI_NIGHT_MODE_OVERRIDE_ON, 1)
            }
        }
    }

    // This is workaround, so ignore warnings.
    @SuppressLint("WrongConstant")
    private fun restartSystemUi(context: Context) {
        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        uiModeManager.enableCarMode(0)
        uiModeManager.disableCarMode(0)
    }

    fun checkPermissionGranted(context: Context): Boolean {
        val result = context.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
        return result == PackageManager.PERMISSION_GRANTED
    }
}