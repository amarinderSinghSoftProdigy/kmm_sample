//
//  InvoicesScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 4.06.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct InvoicesScreen: View {
    let scope: InvoicesScope
    
    @ObservedObject var filterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var dateRange: SwiftDataSource<DataDateRange>
    
    @ObservedObject var invoicesSearch: SwiftDataSource<NSString>
    @ObservedObject var invoices: SwiftDataSource<NSArray>
    
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
        VStack(spacing: 32) {
            SearchBar(placeholderLocalizationKey: "search_tradename",
                      searchText: invoicesSearch.value,
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
    }
    
    init(scope: InvoicesScope) {
        self.scope = scope
        
        self.filterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.dateRange = SwiftDataSource(dataSource: scope.dateRange)
        
        self.invoicesSearch = SwiftDataSource(dataSource: scope.searchText)
        self.invoices = SwiftDataSource(dataSource: scope.items)
    }
    
    private var ordersView: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 8) {
                Image("Invoice")
                
                LocalizedText(localizationKey: "invoices",
                              textWeight: .bold,
                              fontSize: 20)
            }
            
            if let invoices = self.invoices.value,
               invoices.count > 0 {
                TransparentList(data: self.invoices,
                                dataType: DataInvoice.self,
                                listName: .invoices,
                                pagination: scope.pagination,
                                elementsSpacing: 8,
                                onTapGesture: { scope.selectItem(item: $0) },
                                loadItems: { scope.loadItems() }) { _, invoice in
                    InvoiceView(invoice: invoice)
                }
            }
            else {
                EmptyListView(imageName: "EmptyInvoices",
                              titleLocalizationKey: "empty_invoices",
                              handleHomeTap: { scope.goHome() })
            }
        }
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
    
    private struct InvoiceView: View {
        let invoice: DataInvoice
        
        var body: some View {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(invoice.tradeName)
                        .medicoText(textWeight: .medium,
                                    fontSize: 15,
                                    color: .darkBlue)
                        .lineLimit(1)
                    
                    HStack(spacing: 2) {
                        Text(invoice.info.date)
                            .medicoText(textWeight: .medium)
                        
                        Text(invoice.info.time)
                            .medicoText(textWeight: .semiBold)
                    }
                    .opacity(0.6)
                }
                .padding(8)
                
                HStack(spacing: 10) {
                    Text(invoice.info.id)
                        .medicoText(textWeight: .medium,
                                    color: .grey3)
                    
                    Spacer()
                    
                    OrderDetailsView(titleLocalizationKey: "inv_total:",
                                     bodyText: invoice.info.total.formattedPrice,
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
