//
//  ConfirmOrderScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 31.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ConfirmOrderScreen: View {
    let scope: ConfirmOrderScope
    
    @ObservedObject var actions: SwiftDataSource<NSArray>
    
    @ObservedObject var order: SwiftDataSource<DataOrder>
    
    @ObservedObject var activeTab: SwiftDataSource<ConfirmOrderScope.Tab>
    @ObservedObject var entries: SwiftDataSource<NSArray>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            LocalizedText(localizationKey: "action_confirmation",
                          textWeight: .bold,
                          fontSize: 20)
                .padding(.horizontal, 6)
            
            self.tabBar
            
            OrderEntriesView(selectableOrderEntry: scope,
                             entries: entries.value)
            
            if let price = order.value?.info.total.formattedPrice {
                CartOrderTotalPriceView(price: price)
            }
            
            if scope.canEdit {
                self.actionsView
                    .notificationAlertSender(withHandler: scope)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 24)
    }
    
    init(scope: ConfirmOrderScope) {
        self.scope = scope
        
        self.actions = .init(dataSource: scope.actions)
        self.order = .init(dataSource: scope.order)
        
        self.activeTab = .init(dataSource: scope.activeTab)
        self.entries = .init(dataSource: scope.entries)
    }
    
    private var tabBar: some View {
        HStack {
            ForEach(0..<scope.tabs.count) { index in
                let tab = scope.tabs[index]
                
                TabOptionView(localizationKey: tab.stringId,
                              isSelected: activeTab.value == tab,
                              selectedColor: .hex(tab.bgColorHex),
                              itemsNumber: self.entries.value?.count ?? 0)
                    .onTapGesture {
                        scope.selectTab(tab: tab)
                }
            }
        }
        .padding(.vertical, 2)
        .padding(.horizontal, 3)
        .background(AppColor.navigationBar.color.cornerRadius(8))
    }
    
    private var actionsView: some View {
        Group {
            if let actions = self.actions.value as? [ConfirmOrderScope.Action] {
                HStack {
                    ForEach(actions, id: \.self) { action in
                        MedicoButton(localizedStringKey: action.stringId,
                                     isEnabled: true,
                                     height: 40,
                                     cornerRadius: 6,
                                     fontSize: 12,
                                     fontColor: .hex(action.textColorHex),
                                     buttonColor: .hex(action.bgColorHex)) {
                            scope.acceptAction(action: action)
                        }
                    }
                }
            }
        }
    }
}
