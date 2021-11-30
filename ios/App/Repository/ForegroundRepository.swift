//
//  This file is part of Blokada.
//
//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at https://mozilla.org/MPL/2.0/.
//
//  Copyright © 2021 Blocka AB. All rights reserved.
//
//  @author Kar
//

import Foundation

class ForegroundRepository {

    private let log = Logger("Foreground")

    private lazy var writeForeground = Pubs.writeForeground

    func onForeground() {
        writeForeground.send(true)
    }

    func onBackground() {
        writeForeground.send(false)
    }

}
