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

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class NightModeTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        update()
    }

    override fun onStartListening() {
        super.onStartListening()
        update()
    }

    override fun onClick() {
        super.onClick()
        NightModeHelper.toggleNightMode(applicationContext)
        update()
    }

    private fun update() {
        qsTile.state = if (NightModeHelper.isInNightMode(applicationContext)) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }
}