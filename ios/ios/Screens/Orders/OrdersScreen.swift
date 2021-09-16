//
//  OrdersScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 26.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct OrdersScreen: View {
    let scope: OrdersScope
    
    @ObservedObject var filterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var dateRange: SwiftDataSource<DataDateRange>
    
    @ObservedObject var ordersNumber: SwiftDataSource<KotlinInt>
    
    @ObservedObject var ordersSearch: SwiftDataSource<NSString>
    @ObservedObject var orders: SwiftDataSource<NSArray>
    
    private var fromDate: Binding<Date?> {
        .init(get: {
            KotlinLong.msToDate(dateRange.value?.fromMs)
        }, set: {
            if let milliseconds = $0?.millisecondsSince1970 {
                scope.setFrom(fromMs: Int64(milliseconds))
            }
        })
    }
    
    private var toDate: Binding<Date?> {
        .init(get: {
            KotlinLong.msToDate(dateRange.value?.toMs)
        }, set: {
            if let milliseconds = $0?.millisecondsSince1970 {
                scope.setTo(toMs: Int64(milliseconds))
            }
        })
    }
    
    var body: some View {
        VStack(spacing: 20) {
            SearchBar(placeholderLocalizationKey: "search_tradename",
                      searchText: ordersSearch.value,
                      style: .custom(fontWeight: .medium, placeholderOpacity: 0.5),
                      leadingButton: .init(button: .custom(
                                            AnyView(
                                                Image(systemName: "magnifyingglass")
                                                    .foregroundColor(appColor: .darkBlue)
                                                    .font(.system(size: 16, weight: .medium))
                                            ))),
                      trailingButton: .init(button: .filter({ scope.toggleFilter() }))) { text, _ in
                scope.search(value: text)
            }
            
            if self.filterOpened.value == true {
                self.filterView
            }
            else {
                self.ordersView
            }
        }
        .padding(.vertical, 32)
        .padding(.horizontal, 16)
        .onAppear {
            scope.firstLoad()
        }
    }
    
    init(scope: OrdersScope) {
        self.scope = scope
        
        self.filterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.dateRange = SwiftDataSource(dataSource: scope.dateRange)
        
        self.ordersNumber = SwiftDataSource(dataSource: scope.totalItems)
        
        self.ordersSearch = SwiftDataSource(dataSource: scope.searchText)
        self.orders = SwiftDataSource(dataSource: scope.items)
    }
    
    private var ordersView: some View {
        VStack(spacing: 16) {
            TabOptionView(localizationKey: scope.type.localizationKey,
                          isSelected: true,
                          itemsNumber: (self.ordersNumber.value as? Int) ?? 0)
            
            if let orders = self.orders.value,
               orders.count > 0 {
                TransparentList(data: self.orders,
                                dataType: DataOrder.self,
                                listName: .orders,
                                pagination: scope.pagination,
                                elementsSpacing: 8,
                                onTapGesture: { scope.selectItem(item: $0) },
                                loadItems: { scope.loadItems() }) { _, order in
                    OrderView(order: order)
                }
            }
            else {
                emptyListView
            }
        }
    }
    
    private var emptyListView: some View {
        let imageName: String
        let titleLocalizationKey: String
        
        switch scope.type {
        case .purchaseOrder:
            imageName = "EmptyOrders"
            titleLocalizationKey = "empty_new_orders"
            
        case .order:
            imageName = "EmptyOrders"
            titleLocalizationKey = "empty_orders"
            
        case .history:
            imageName = "EmptyOrdersHistory"
            titleLocalizationKey = "empty_orders_history"
            
        default:
            return AnyView(EmptyView())
        }
        
        return AnyView(EmptyListView(imageName: imageName,
                                     titleLocalizationKey: titleLocalizationKey,
                                     handleHomeTap: { scope.goHome() }))
    }
    
    private var filterView: some View {
        VStack(spacing: 16) {
            HStack {
                LocalizedText(localizationKey: "date",
                              textWeight: .medium,
                              fontSize: 16)
                
                Spacer()
                
                Button(action: { scope.clearFilters() }) {
                    LocalizedText(localizationKey: "clear",
                                  textWeight: .medium,
                                  fontSize: 16,
                                  color: .grey3)
                }
            }
            
            DatePicker(placeholderLocalizationKey: "from:", date: fromDate)
            DatePicker(placeholderLocalizationKey: "to:", date: toDate)
        }
    }
    
    private struct OrderView: View {
        let order: DataOrder
        
        var body: some View {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(order.tradeName)
                        .medicoText(textWeight: .medium,
                                    fontSize: 15,
                                    color: .darkBlue)
                        .lineLimit(1)
                    
                    HStack(spacing: 2) {
                        OrderStatusView(status: order.info.status.stringValue)
                        
                        Spacer()
                        
                        Group {
                            Text(order.info.date)
                                .medicoText(textWeight: .medium)
                            
                            Text(order.info.time)
                                .medicoText(textWeight: .semiBold)
                        }
                        .opacity(0.6)
                    }
                }
                .padding(8)
                
                HStack(spacing: 10) {
                    Text(order.info.id)
                        .medicoText(textWeight: .medium,
                                    color: .grey3)
                        .lineLimit(1)
                    
                    Spacer()
                    
                    OrderDetailsView(titleLocalizationKey: "total:",
                                     bodyText: order.info.total.formattedPrice,
                                     bodyColor: .lightBlue)
                }
                .padding(8)
                .background(
                    RoundedCorner(radius: 5, corners: [.bottomLeft, .bottomRight])
                        .foregroundColor(appColor: .darkBlue)
                        .opacity(0.04)
                    
                )
            }
            .strokeBorder(.darkBlue,
                          borderOpacity: 0.12,
                          fill: .white,
                          cornerRadius: 5)
        }
    }
}

struct OrderStatusView: View {
    var status: String
    
    var body: some View {
        let statusColor: AppColor =
            status == "COMPLETED" ? .green :
            status == "" || status == "" ? .red :
            .orange
        
        return AnyView(
            HStack(spacing: 4) {
                statusColor.color
                    .cornerRadius(3)
                    .frame(width: 10, height: 10)
                
                Text(status)
                    .medicoText(textWeight: .medium)
                    .opacity(0.7)
            }
        )
    }
}

extension DataOrderType {
    var localizationKey: String {
        switch self {
        case .purchaseOrder:
            return "new_orders"
            
        case .order:
            return "my_orders"
            
        case .history:
            return "orders"
            
        default:
            return ""
        }
    }
}

struct OrderDetailsView: View {
    let titleLocalizationKey: String
    let bodyText: String
    let bodyColor: AppColor
    
    var body: some View {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          textWeight: .medium,
                          color: .grey3)
            
            Text(bodyText)
                .medicoText(textWeight: .bold,
                            color: bodyColor)
        }
        .lineLimit(1)
        .minimumScaleFactor(0.4)
    }
    
    init(titleLocalizationKey: String,
         bodyText: String,
         bodyColor: AppColor = .darkBlue) {
        self.titleLocalizationKey =  titleLocalizationKey
        self.bodyText = bodyText
        self.bodyColor = bodyColor
    }
}
