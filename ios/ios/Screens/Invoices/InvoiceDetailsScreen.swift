//
//  InvoiceDetailsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 4.06.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct InvoiceDetailsScreen: View {
    let scope: ViewInvoiceScope
    
    @ObservedObject var b2bData: SwiftDataSource<DataB2BData>
    @ObservedObject var invoice: SwiftDataSource<DataInvoice>
    
    @ObservedObject var entries: SwiftDataSource<NSArray>
    
    var body: some View {
        VStack(spacing: 16) {
            CustomerView(b2bData: b2bData.value,
                         seasonBoyRetailerName: nil)
            
            self.invoiceView
            
            self.entriesView
            
            if let price = invoice.value?.info.total.formattedPrice {
                CartOrderTotalPriceView(price: price)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 24)
    }
    
    init(scope: ViewInvoiceScope) {
        self.scope = scope
        
        self.b2bData = .init(dataSource: scope.b2bData)
        self.invoice = .init(dataSource: scope.invoice)
        
        self.entries = .init(dataSource: scope.entries)
    }
    
    private var invoiceView: some View {
        Group {
            if let invoice = self.invoice.value {
                VStack(spacing: 6) {
                    HStack {
                        OrderDetailsView(titleLocalizationKey: "invoice_no:",
                                         bodyText: invoice.info.id)
                        
                        Spacer()
                        
                        Text(invoice.info.date)
                            .medicoText(textWeight: .medium,
                                        color: .grey3)
                    }
                    
                    HStack {
//                        OrderDetailsView(titleLocalizationKey: "type:",
//                                         bodyText: order.info.paymentMethod.serverValue,
//                                         bodyColor: .lightBlue)
                        
                        Spacer()
                        
                        Text(invoice.info.time)
                            .medicoText(textWeight: .medium,
                                        color: .grey3)
                    }
                    
                    TransparentDivider()
                }
            }
        }
    }
    
    private var entriesView: some View {
        Group {
            if let entries = self.entries.value as? [DataInvoiceEntry] {
                VStack {
                    ForEach(entries, id: \.self) { entry in
                        EntryView(entry: entry)
                    }
                }
                .scrollView()
            }
        }
        .frame(maxHeight: .infinity, alignment: .topLeading)
    }
    
    private struct EntryView: View {
        let entry: DataInvoiceEntry
        
        var body: some View {
            HStack(spacing: 10) {
                VStack(alignment: .leading, spacing: 6) {
                    Text(entry.productName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 16)
                        .lineLimit(1)
                    
                    if !entry.price.formatted.isEmpty {
                        HStack(spacing: 2) {
                            LocalizedText(localizationKey: "price:",
                                          textWeight: .medium,
                                          color: .grey3)
                            
                            PriceWithFootnoteView(price: entry.price.formatted, fontSize: 14)
                        }
                        .lineLimit(1)
                        .minimumScaleFactor(0.4)
                    }
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 6) {
                    OrderDetailsView(titleLocalizationKey: "qty:",
                                     bodyText: entry.quantity.formatted,
                                     bodyColor: .lightBlue)
                    
                    if !entry.totalAmount.formatted.isEmpty {
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
            )
        }
    }
}
