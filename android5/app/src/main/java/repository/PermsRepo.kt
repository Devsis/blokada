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
import model.Granted
import org.blokada.R
import service.*
import ui.utils.AndroidUtils
import utils.Logger

object PermsRepo {

    private val writeDnsProfilePerms = MutableStateFlow<Granted?>(null)
    private val writeVpnProfilePerms = MutableStateFlow<Granted?>(null)
    private val writeNotificationPerms = MutableStateFlow<Granted?>(null)

    private val writeDnsString = MutableStateFlow<String?>(null)

    val dnsProfilePermsHot = writeDnsProfilePerms.filterNotNull().distinctUntilChanged()
    val vpnProfilePermsHot = writeVpnProfilePerms.filterNotNull().distinctUntilChanged()
    val notificationPermsHot = writeNotificationPerms.filterNotNull().distinctUntilChanged()

    private val enteredForegroundHot = StageRepo.enteredForegroundHot

    private val context = ContextService
    private val dialog = DialogService
    private val systemNav = SystemNavService
    private val vpnPerms = VpnPermissionService

    private val cloudRepo = CloudRepo

    fun start() {
        Logger.e("xxxx", "start of perms repo")
        GlobalScope.launch { onForeground_recheckPerms() }
        GlobalScope.launch { onDnsString_latest() }
    }

    private suspend fun onForeground_recheckPerms() {
        enteredForegroundHot
        .combine(cloudRepo.dnsProfileActivatedHot) { _, activated -> activated }
        .collect { activated ->
            Logger.e("xxxx", "collect with dns profile activated")
            writeDnsProfilePerms.value = activated
            writeVpnProfilePerms.value = vpnPerms.hasPermission()
        }
    }

    private suspend fun onDnsString_latest() {
        Logger.e("xxxx", "setup listener dns string in perms")
        cloudRepo.dnsStringHot.collect {
            Logger.e("xxxx", "got dns string in perms")
            writeDnsString.value = it
        }
    }

    suspend fun displayDnsProfilePermsInstructions(): Flow<Boolean> {
        val ctx = context.requireContext()
        return dialog.showAlert(
            message = ctx.getString(R.string.dnsprofile_desc),
            header = ctx.getString(R.string.dnsprofile_header),
            okText = ctx.getString(R.string.dnsprofile_action_open_settings),
            okAction = {
                writeDnsString.value?.run {
                    AndroidUtils.copyToClipboard(this)
                    systemNav.openNetworkSettings()
                }
            }
        )
    }

}