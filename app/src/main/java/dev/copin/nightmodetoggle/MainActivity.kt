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

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnToggle).setOnClickListener {
            NightModeHelper.toggleNightMode(applicationContext)
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