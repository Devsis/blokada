//
//  This file is part of Blokada.
//
//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at https://mozilla.org/MPL/2.0/.
//
//  Copyright © 2021 Blocka AB. All rights reserved.
//
//  @author Karol Gusak
//

import Foundation

func convertActivity(activity: [Activity]) -> [HistoryEntry] {
    let act = activity.map { act in
        HistoryEntry(
            name: act.domain_name,
            type: convertType(type: act.action),
            time: convertDate(timestamp: act.timestamp),
            requests: 1
        )
    }
    let groups = Dictionary(grouping: act, by: { [$0.type: $0.name] })
    return groups.map { _, value in
        // Assuming items are ordered by most recent first
        let item = value.first!
        return HistoryEntry(
            name: item.name,
            type: item.type,
            time: item.time,
            requests: UInt64(value.count)
        )
    }
}

private func convertType(type: String) -> HistoryEntryType {
    switch (type) {
        case "block": return HistoryEntryType.blocked
        case "allow": return HistoryEntryType.whitelisted
        default: return HistoryEntryType.passed
    }
}

private let dateFormatter = DateFormatter()
private func convertDate(timestamp: String) -> Date {
    dateFormatter.dateFormat = blockaDateFormat
    guard let date = dateFormatter.date(from: timestamp) else {
        dateFormatter.dateFormat = blockaDateFormatNoNanos
        guard let date = dateFormatter.date(from: timestamp) else {
            return Date(timeIntervalSince1970: 0)
        }
        return date
    }
    return date
}
