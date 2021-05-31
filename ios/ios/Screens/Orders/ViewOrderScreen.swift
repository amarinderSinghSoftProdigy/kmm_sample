//
//  ViewOrderScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 27.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ViewOrderScreen: View {
    let scope: ViewOrderScope
    
    @ObservedObject var actions: SwiftDataSource<NSArray>
    @ObservedObject var b2bData: SwiftDataSource<DataB2BData>
    
    @ObservedObject var order: SwiftDataSource<DataOrder>
    
    @ObservedObject var checkedEntries: SwiftDataSource<NSArray>
    @ObservedObject var entries: SwiftDataSource<NSArray>
    
    @State private var expandedCustomerView = false
    
    var body: some View {
        VStack(spacing: 16) {
            self.customerView
            
            self.orderView
            
            self.entriesView
            
            Spacer()
            
            if let price = order.value?.info.total.formattedPrice {
                CartOrderTotalPriceView(price: price)
            }
            
            if scope.canEdit {
                self.actionsView
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 24)
        .notificationAlertSender(withHandler: self.scope)
    }
    
    init(scope: ViewOrderScope) {
        self.scope = scope
        
        self.actions = .init(dataSource: scope.actions)
        self.b2bData = .init(dataSource: scope.b2bData)
        self.order = .init(dataSource: scope.order)
        
        self.checkedEntries = .init(dataSource: scope.checkedEntries)
        self.entries = .init(dataSource: scope.entries)
    }
    
    private var customerView: some View {
        Group {
            if let b2bData = self.b2bData.value {
                VStack(spacing: 8) {
                    Group {
                        HStack {
                            Text(b2bData.addressData.address)
                            
                            Spacer()
                            
                            Text(b2bData.gstin)
                        }
                        
                        HStack {
                            SmallAddressView(location: b2bData.addressData.fullAddress(),
                                             fontWeight: .medium,
                                             fontSize: 12,
                                             color: .darkBlue)
                            
                            Spacer()
                            
                            Text(b2bData.panNumber)
                        }
                        
                        HStack {
                            Text(b2bData.drugLicenseNo1)
                            
                            Spacer()
                            
                            Text(b2bData.drugLicenseNo2)
                        }
                    }
                    .medicoText(textWeight: .medium,
                                fontSize: 12)
                    .padding(.horizontal, 4)
                    
                    if let seasonBoyRetailerName = self.order.value?.seasonBoyRetailerName {
                        self.divider
                            .padding(.horizontal, -12)
                        
                        HStack {
                            Text(seasonBoyRetailerName)
                                .medicoText(textWeight: .bold,
                                            fontSize: 12)
                            
                            Spacer()
                            
                            Text(b2bData.phoneNumber)
                                .medicoText(textWeight: .medium,
                                            fontSize: 12)
                        }
                        .padding(.horizontal, 4)
                    }
                }
                .lineLimit(1)
                .expandableView(expanded: $expandedCustomerView) {
                    HStack(spacing: 8) {
                        Image("OrderCustomer")
                        
                        Text(b2bData.tradeName)
                            .medicoText(textWeight: .bold,
                                        fontSize: 15)
                            .lineLimit(1)
                    }
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
            }
        }
    }
    
    private var orderView: some View {
        Group {
            if let order = self.order.value {
                VStack(spacing: 6) {
                    HStack {
                        OrderDetailsView(titleLocalizationKey: "order_no:",
                                         bodyText: order.info.id)
                        
                        Spacer()
                        
                        Text(order.info.date)
                            .medicoText(textWeight: .medium,
                                        color: .grey3)
                    }
                    
                    HStack {
                        OrderDetailsView(titleLocalizationKey: "type:",
                                         bodyText: order.info.paymentMethod.serverValue,
                                         bodyColor: .lightBlue)
                        
                        Spacer()
                        
                        Text(order.info.time)
                            .medicoText(textWeight: .medium,
                                        color: .grey3)
                    }
                    
                    self.divider
                }
            }
        }
    }
    
    private var entriesView: some View {
        Group {
            if let entries = self.entries.value as? [DataOrderEntry] {
                let checkedEntries = self.checkedEntries.value as? [DataOrderEntry]
                
                VStack {
                    ForEach(entries, id: \.self) { entry in
                        EntryView(entry: entry,
                                  selected: checkedEntries?.contains(entry) == true,
                                  canEdit: scope.canEdit)
                            .onTapGesture {
                                scope.selectEntry(entry: entry)
                            }
                    }
                }
                .scrollView()
            }
        }
    }
    
    private var actionsView: some View {
        Group {
            if let actions = self.actions.value as? [ViewOrderScope.Action] {
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
    
    private var divider: some View {
        AppColor.black.color
            .opacity(0.12)
            .frame(height: 1)
    }
    
    private struct EntryView: View {
        let entry: DataOrderEntry
        
        let selected: Bool
        let canEdit: Bool
        
        var body: some View {
            HStack(spacing: 10) {
                if canEdit {
                    CheckBox(selected: .constant(selected))
                        .frame(width: 22, height: 22)
                }
                
                VStack(alignment: .leading, spacing: 6) {
                    Text(entry.productName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 16)
                        .lineLimit(1)
                    
                    if entry.buyingOption == .buy {
                        OrderDetailsView(titleLocalizationKey: "price:",
                                         bodyText: entry.price.formatted)
                    }
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 6) {
                    OrderDetailsView(titleLocalizationKey: "qty:",
                                     bodyText: entry.servedQty.formatted,
                                     bodyColor: .lightBlue)
                    
                    if entry.buyingOption == .buy {
                        OrderDetailsView(titleLocalizationKey: "subtotal:",
                                         bodyText: entry.totalAmount.formatted)
                    }
                    else {
                        LocalizedText(localizationKey: "quote",
                                      textWeight: .bold)
                    }
                }
            }
            .padding(8)
            .background(
                RoundedRectangle(cornerRadius: 5)
                    .foregroundColor(appColor: .white)
                    .padding(2)
                    .background(
                        RoundedRectangle(cornerRadius: 5)
                            .foregroundColor(appColor: entry.buyingOption.borderColor)
                    )
            )
        }
    }
}

extension DataBuyingOption {
    var borderColor: AppColor {
        switch self {
        case .quote:
            return .placeholderGrey
            
        default:
            return .white
        }
    }
}
