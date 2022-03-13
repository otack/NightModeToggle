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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnToggle).setOnClickListener {
            NightModeHelper.toggleNightMode(applicationContext)
        }

        findViewById<Button>(R.id.btnCopyCommand).setOnClickListener {
            val clipData = ClipData.newPlainText("nightmodetoggle_permission_command",
                getString(R.string.grant_permission_command))
            getSystemService(ClipboardManager::class.java).setPrimaryClip(clipData)
            Toast.makeText(this, R.string.command_copied, Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnShareCommand).setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.grant_permission_command))
            }
            startActivity(Intent.createChooser(intent, null))
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        val btn = findViewById<Button>(R.id.btnToggle)
        if (NightModeHelper.checkPermissionGranted(applicationContext)) {
            btn.isEnabled = true
            btn.text = getString(R.string.toggle_btn)
        } else {
            btn.isEnabled = false
            btn.text = getString(R.string.permission_not_granted)
        }
    }
}