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

package service

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.first
import model.DevicePayload
import ui.AccountViewModel
import ui.MainApplication

object BlockaApiForCurrentUserService {

    private val context = ContextService
    private val api = BlockaApiService

    // TODO: This a hacky temporary way, it should be AccountRepo, maybe broke BackupAgent
    private val accountVm by lazy {
        val app = context.requireApp() as MainApplication
        ViewModelProvider(app).get(AccountViewModel::class.java)
    }

    suspend fun getDeviceForCurrentUser(): DevicePayload {
        val account = accountVm.account.asFlow().first()
        return api.getDevice(account.id)
    }

}