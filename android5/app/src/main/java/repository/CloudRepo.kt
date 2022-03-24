/*
 * This file is part of Blokada.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright © 2022 Blocka AB. All rights reserved.
 *
 * @author Karol Gusak (karol@blocka.net)
 */

package repository

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.DevicePayload
import service.BlockaApiForCurrentUserService
import service.EnvironmentService
import service.PrivateDnsService
import utils.Logger

object CloudRepo {

    private val dnsPerms = PrivateDnsService
    private val env = EnvironmentService
    private val api = BlockaApiForCurrentUserService

    private val enteredForegroundHot = StageRepo.enteredForegroundHot

    private val writeDeviceInfo = MutableStateFlow<DevicePayload?>(null)

    val deviceInfoHot = writeDeviceInfo.filterNotNull().distinctUntilChanged()

    val dnsStringHot = deviceInfoHot.map {
        // TODO: better sanitize device name
        val deviceName = env.getDeviceAlias().replace(" ", "--")
        val tag = it.device_tag
        Logger.e("xxxx", "setting up device tag")
        "$deviceName-$tag.cloud.blokada.org"
    }

    val dnsProfileActivatedHot = dnsStringHot.map {
        Logger.e("xxxx", "comparing dsn prifol")
        dnsPerms.isPrivateDnsProfileActive(it)
    }

    fun start() {
        GlobalScope.launch { onForeground_refreshDeviceInfo() }
    }

    private suspend fun onForeground_refreshDeviceInfo() {
        Logger.v("Sol", "setting up enter foreground")
        enteredForegroundHot
        .collect {
            Logger.v("Sel", "got foregroudn: $it")
            onRefreshDeviceInfo()
        }
    }

    private suspend fun onRefreshDeviceInfo() {
        writeDeviceInfo.value = api.getDeviceForCurrentUser()
    }

}