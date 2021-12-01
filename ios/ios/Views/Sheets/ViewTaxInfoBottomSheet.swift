//
//  ViewTaxInfoBottomSheet.swift
//  Medico
//
//  Created by Pavel on 30.11.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ViewTaxInfoBottomSheet: ViewModifier {
    let bottomSheet: BottomSheet.ViewTaxInfo
    var taxInfo: DataTaxInfo {
        return bottomSheet.taxInfo
    }
        
    let onBottomSheetDismiss: () -> ()
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 600) {
                VStack(alignment: .center, spacing: 13) {
                    firstRowView()
                    TransparentDivider()
                    secondRowView()
                    TransparentDivider()
                    thirdRowView()
                        .padding(.top, -13)
                    TransparentDivider()
                    lastRowView()
                }
                .scrollView()
                .padding(.top, 35)
            }
            .edgesIgnoringSafeArea(.bottom)
        )
    }
    
    init(bottomSheet: BottomSheet.ViewTaxInfo,
         onBottomSheetDismiss: @escaping () -> ())
    {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
    }
    
    private func getDetailsView(titleLocalizationKey: String,
                                bodyText: String,
                                bodyColor: AppColor = .greyBlue) -> some View
    {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          color: .greyBlue)
            
            Text(bodyText)
                .medicoText(textWeight: .bold,
                            color: bodyColor)
        }
    }
    
    fileprivate func firstRowView() -> some View {
        func createCell(localizationKey: String, value: String) -> some View {
            HStack {
                LocalizedText(localizationKey: localizationKey,
                              textWeight: .semiBold,
                              fontSize: 16,
                              color: .darkBlue)
                    .lineLimit(1)
                Spacer()
                Text(value)
                    .medicoText(textWeight: .bold, fontSize: 16, color: .lightBlue)
            }
        }
        return VStack(spacing: 10) {
            HStack(spacing: 30) {
                createCell(localizationKey: "items", value: String(taxInfo.noOfItems))
                createCell(localizationKey: "units", value: String(taxInfo.noOfUnits))
            }
            HStack(spacing: 30) {
                createCell(localizationKey: "adjust", value: taxInfo.adjWithoutRounded.formatted)
                createCell(localizationKey: "rounding", value: taxInfo.adjRounded.formatted)
            }
        }
        .padding(.horizontal, 16)
    }
    
    fileprivate func secondRowView() -> some View {
        func createCell(localizationKey: String, value: String) -> some View {
            HStack {
                LocalizedText(localizationKey: localizationKey,
                              textWeight: .semiBold,
                              fontSize: 16,
                              color: .darkBlue)
                    .lineLimit(1)
                Spacer()
                Text(value)
                    .medicoText(textWeight: .regular, fontSize: 16, color: .black)
                
            }
        }
        return VStack(spacing: 13) {
            createCell(localizationKey: "gross_amount", value: taxInfo.grossAmount.formatted)
            createCell(localizationKey: "discount", value: "\(taxInfo.invoiceDiscount.value ?? 0) | \(taxInfo.totalDiscountAmt.formatted)")
            createCell(localizationKey: "freight", value: taxInfo.freight.formatted)
            createCell(localizationKey: "total_tax", value: taxInfo.totalTaxAmount.formatted)
        }
        .padding(.horizontal, 16)
    }
    
    fileprivate func thirdRowView() -> some View {
        func createCell(title: String, value: String, isMain: Bool) -> some View {
            HStack {
                Text(title)
                    .medicoText(textWeight: isMain ? .semiBold : .medium, fontSize: 16, color: .darkBlue)
                    .opacity(isMain ? 1 : 0.5)
                    .lineLimit(1)
                Spacer()
                Text(value)
                    .medicoText(textWeight: isMain ? .medium : .regular, fontSize: 16, color: .darkBlue)
            }
            .padding(.leading, isMain ? 16 : 32)
            .padding(.trailing, 16)
            .padding(.vertical, 10)
            .background(isMain ? AppColor.blueWhite.color : .white)
        }
        
        let availableTaxes =
            taxInfo.totalTaxRates.filter { $0.totalTaxableAmount.value?.doubleValue ?? 0 > 0.0 }
        let taxTypeString = taxInfo.type == .igst ? "IGST" : "GST"
        return VStack(spacing: 0) {
            if !availableTaxes.isEmpty {
                ForEach(availableTaxes, id: \.self) {
                    tax in
                    createCell(title: "\(taxTypeString)(\(tax.gstDisplayName))", value: tax.totalTaxableAmount.formatted, isMain: true)
                    if taxInfo.type != .igst {
                        if tax.cgstTotalAmt.value?.doubleValue ?? 0 > 0.0 {
                            createCell(title: "CGST(\(tax.cgstTaxPercent.formatted))", value: tax.cgstTotalAmt.formatted, isMain: false)
                        }
                        if tax.sgstTotalAmt.value?.doubleValue ?? 0 > 0.0 {
                            createCell(title: "SGST(\(tax.sgstTaxPercent.formatted))", value: tax.sgstTotalAmt.formatted, isMain: false)
                        }
                    }
                }
            }
            else {
                createCell(title: "GST(0.0%)", value: taxInfo.totalIGST.formatted, isMain: true)
            }
        }
    }
    
    fileprivate func lastRowView() -> some View {
        HStack {
            LocalizedText(localizationKey: "net_payable:",
                          textWeight: .medium,
                          fontSize: 16,
                          color: .darkBlue)
                .lineLimit(1)
            Spacer()
            Text(taxInfo.netAmount.formatted)
                .medicoText(textWeight: .bold, fontSize: 16, color: .black)
            
        }
        .padding(.horizontal, 16)
    }
    
}
