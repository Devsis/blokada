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
import repository.AccountRepo
import repository.Repos
import ui.AccountViewModel
import ui.MainApplication

object BlockaApiForCurrentUserService {

    private val api = BlockaApiService

    private val accountIdHot = Repos.account.accountIdHot

    suspend fun getDeviceForCurrentUser(): DevicePayload {
        return api.getDevice(accountIdHot.first())
    }

}