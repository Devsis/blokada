//
//  This file is part of Blokada.
//
//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at https://mozilla.org/MPL/2.0/.
//
//  Copyright © 2020 Blocka AB. All rights reserved.
//
//  @author Karol Gusak
//

import SwiftUI

struct ActivityDetailView: View {

    @ObservedObject var vm: ActivityItemViewModel
    @ObservedObject var activityVM = ViewModels.activity

    var body: some View {
        ScrollView {
            VStack(alignment: .leading) {
                HStack {
                    ZStack {
                        Image(systemName: vm.entry.type == .whitelisted ? "shield.slash"  : "shield")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 64, height: 64)
                            .foregroundColor(vm.entry.type == .blocked ? .red : .green)
                    }

                    VStack(alignment: .leading) {
                        Text(self.vm.entry.name)
                            .lineLimit(1)
                            .truncationMode(.middle)
                            .font(.system(size: 22))
                        (
                            Text(self.vm.entry.type == .blocked ? L10n.activityRequestBlocked : self.vm.entry.type == .whitelisted ? L10n.activityRequestAllowedWhitelisted : L10n.activityRequestAllowed)
                        )
                        .font(.footnote)
                        .foregroundColor(Color.secondary)
                    }
                    .accessibilitySortPriority(1)
                    .frame(height: 96)
                    .padding(.leading, 10)
                }

                Divider()

                Text(L10n.activityActionsHeader)
                    .font(.system(size: 20))
                    .bold()
                    .padding(.bottom)

                    VStack(spacing: 0) {
                        if self.vm.whitelisted {
                            Button(action: {
                                self.activityVM.unallow(self.vm.entry)
                            }) {
                                OptionView(
                                    text: L10n.activityActionAddedToWhitelist,
                                    image: Image.fShieldSlash,
                                    active: true
                                )
                            }
                        } else if self.vm.blacklisted {
                            Button(action: {
                                self.activityVM.undeny(self.vm.entry)
                            }) {
                                OptionView(
                                    text: L10n.activityActionAddedToBlacklist,
                                    image: Image.fShieldSlash,
                                    active: true
                                )
                            }
                            
                        } else if self.vm.entry.type == HistoryEntryType.passed {
                            Button(action: {
                                self.activityVM.deny(self.vm.entry)
                            }) {
                                OptionView(
                                    text: L10n.activityActionAddToBlacklist,
                                    image: Image.fShieldSlash,
                                    active: false
                                )
                            }
                        } else {
                            Button(action: {
                                self.activityVM.allow(self.vm.entry)
                            }) {
                                OptionView(
                                    text: L10n.activityActionAddToWhitelist,
                                    image: Image.fShieldSlash,
                                    active: false
                                )
                            }
                        }

                        Button(action: {
                            UIPasteboard.general.string = self.vm.entry.name
                        }) {
                            OptionView(
                                text: L10n.activityActionCopyToClipboard,
                                image: Image.fCopy,
                                active: false
                            )
                        }
                    }
                    .padding(.bottom)

                Divider()

                Text(L10n.activityInformationHeader)
                    .font(.system(size: 20))
                    .bold()
                    .padding(.bottom)

                VStack(alignment: .leading) {
                    Text(L10n.activityDomainName)
                        .foregroundColor(.secondary)
                    Text(self.vm.entry.name)
                }
                .font(.footnote)
                .padding(.bottom)

                VStack(alignment: .leading) {
                    Text(L10n.activityTimeOfOccurrence)
                        .foregroundColor(.secondary)
                    Text(self.vm.entry.time.human)
                }
                .font(.footnote)
                .padding(.bottom)

                VStack(alignment: .leading) {
                    Text(L10n.activityNumberOfOccurrences)
                        .foregroundColor(.secondary)
                    Text(String(self.vm.entry.requests))
                }
                .font(.footnote)
                .padding(.bottom)

                VStack(alignment: .leading) {
                    Text(L10n.universalLabelDevice)
                        .foregroundColor(.secondary)
                    Text(String(self.vm.entry.device))
                }
                .font(.footnote)
                .padding(.bottom)
            }
            .padding()
        }
        .navigationBarTitle(Text(""), displayMode: .inline)
        .accentColor(Color.cAccent)
    }
}

struct ActivityDetailView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ActivityDetailView(vm: ActivityItemViewModel(entry: HistoryEntry(
                name: "example.com",
                type: .whitelisted,
                time: Date(timeIntervalSinceNow: -50000),
                requests: 458,
                device: "iphone"
            )))

            ActivityDetailView(vm: ActivityItemViewModel(entry: HistoryEntry(
                name: "super.long.boring.name.of.example.com",
                type: .blocked,
                time: Date(timeIntervalSinceNow: -5),
                requests: 1,
                device: "iphone"
            )))
        }
    }
}
