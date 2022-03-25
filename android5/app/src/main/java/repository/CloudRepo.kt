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
import model.Granted
import service.BlockaApiForCurrentUserService
import service.ConnectivityService
import service.EnvironmentService
import utils.Logger

class CloudRepo {

    private val env = EnvironmentService
    private val api = BlockaApiForCurrentUserService
    private val connectivity = ConnectivityService

    private val enteredForegroundHot = Repos.stage.enteredForegroundHot
    private val accountIdHot = Repos.account.accountIdHot

    private val writeDeviceInfo = MutableStateFlow<DevicePayload?>(null)
    private val writeDnsProfileActivated = MutableStateFlow<Granted?>(null)
    private val writePrivateDnsSetting = MutableStateFlow<String?>(null)

    val deviceInfoHot = writeDeviceInfo.filterNotNull().distinctUntilChanged()

    val expectedDnsStringHot = deviceInfoHot.map {
        // TODO: better sanitize device name
        val deviceName = env.getDeviceAlias().replace(" ", "--")
        val tag = it.device_tag
        "$deviceName-$tag.cloud.blokada.org"
    }

    val dnsProfileActivatedHot = writeDnsProfileActivated.filterNotNull().distinctUntilChanged()

    fun start() {
        GlobalScope.launch { onForeground_refreshDeviceInfo() }
        GlobalScope.launch { onAccountIdChanged_refreshDeviceInfo() }
        GlobalScope.launch { onPrivateDnsProfileChanged_update() }
        GlobalScope.launch { printDnsProfAct() }
        onPrivateDnsSettingChanged_update()
    }

    private suspend fun onForeground_refreshDeviceInfo() {
        enteredForegroundHot
        .collect {
            onRefreshDeviceInfo()
        }
    }

    private suspend fun onAccountIdChanged_refreshDeviceInfo() {
        accountIdHot
        .collect {
            onRefreshDeviceInfo()
        }
    }

    private suspend fun onPrivateDnsProfileChanged_update() {
        expectedDnsStringHot
        .combine(writePrivateDnsSetting) { setting, expected -> setting == expected }
        .collect { writeDnsProfileActivated.value = it }
    }

    private fun onPrivateDnsSettingChanged_update() {
        connectivity.onPrivateDnsChanged = {
            Logger.w("xxxx", "Received priv dns: $it")
            writePrivateDnsSetting.value = it
        }
    }

    // TODO: task with debounce
    private suspend fun onRefreshDeviceInfo() {
        writeDeviceInfo.value = api.getDeviceForCurrentUser()
    }

    private suspend fun printDnsProfAct() {
        dnsProfileActivatedHot
            .collect {
                Logger.w("xxxx", "Dns pro: $it")
            }
    }
}