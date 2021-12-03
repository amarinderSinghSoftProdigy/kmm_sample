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
    @ObservedObject var taxInfo: SwiftDataSource<DataTaxInfo>
    
    @ObservedObject var entries: SwiftDataSource<NSArray>
    
    var body: some View {
        VStack(spacing: 16) {
            CustomerView(b2bData: b2bData.value,
                         seasonBoyRetailerName: nil)
            
            self.invoiceView
            
            self.entriesView
            
            self.payableView
        }
        .padding(.horizontal, 16)
        .padding(.top, 24)
        .padding(.bottom, 10)
    }
    
    init(scope: ViewInvoiceScope) {
        self.scope = scope
        
        self.b2bData = .init(dataSource: scope.b2bData)
        self.taxInfo = .init(dataSource: scope.taxInfo)
        
        self.entries = .init(dataSource: scope.entries)
    }
    
    private var payableView: some View {
        VStack(spacing: 11) {
            HStack(spacing: 0) {
                LocalizedText(localizationKey: "net_payable:",
                              textWeight: .semiBold,
                              fontSize: 16,
                              color: .white)
                Spacer()
                Text(taxInfo.value?.netAmount.formatted ?? "")
                    .medicoText(textWeight: .semiBold, fontSize: 16, color: .white, multilineTextAlignment: .trailing)
            }
            HStack {
                Spacer()
                Text(taxInfo.value?.amountInWords ?? "")
                    .medicoText(textWeight: .medium, fontSize: 14, color: .white)
            }
        }
        .padding(EdgeInsets(top: 8, leading: 14, bottom: 16, trailing: 9))
        .background(AppColor.lightBlue.color.cornerRadius(5))
        .onTapGesture {
            self.scope.viewTaxInfo()
        }
    }
    
    private var invoiceView: some View {
        Group {
            if let taxInfo = taxInfo.value {
                VStack(spacing: 6) {
                    HStack {
                        OrderDetailsView(titleLocalizationKey: "invoice_no:",
                                         bodyText: taxInfo.b2bUnitInvoiceId)
                        
                        Spacer()
                        
                        Text(taxInfo.invoiceDate)
                            .medicoText(textWeight: .medium,
                                        color: .grey3)
                    }
                    
                    HStack {
                        OrderDetailsView(titleLocalizationKey: "type:",
                                         bodyText: taxInfo.paymentMethod.serverValue,
                                         bodyColor: .lightBlue)
                        
                        Spacer()
                        
                        Text(taxInfo.invoiceTime)
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
                            .onTapGesture {
                                self.scope.viewInvoice(entry: entry)
                            }
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
                    OrderDetailsView(titleLocalizationKey: "GST:",
                                     bodyText: entry.gstTaxRate.string)
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
                    OrderDetailsView(titleLocalizationKey: "discount:",
                                     bodyText: entry.discount.formatted)
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
