//
//  ViewItemTaxBottomSheet.swift
//  Medico
//
//  Created by Pavel on 30.11.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ViewItemTaxBottomSheet: ViewModifier {
    let bottomSheet: BottomSheet.ViewItemTax
    
    var invoiceEntry: DataInvoiceEntry {
        return bottomSheet.invoiceEntry
    }
        
    let onBottomSheetDismiss: () -> ()
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 430) {
                VStack(alignment: .center, spacing: 13) {
                    firstRowView()
                        .padding(.horizontal, 18)
                    TransparentDivider()
                    secondRowView()
                    TransparentDivider()
                    thirdRowView()
                        .padding(.top, -13)
                }
                .scrollView()
                .padding(.top, 35)
            }
            .edgesIgnoringSafeArea(.bottom)
        )
    }
    
    init(bottomSheet: BottomSheet.ViewItemTax,
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
        HStack(spacing: 12.5) {
            ProductImage(medicineId: invoiceEntry.productCode,
                         size: .px123)
                .frame(width: 74, height: 74)
                .cornerRadius(17)
            VStack(alignment: .leading, spacing: 7) {
                Text(invoiceEntry.productName)
                    .medicoText(textWeight: .semiBold, fontSize: 20, color: .darkBlue)
                HStack(spacing: 8) {
                    Text(invoiceEntry.productCode)
                        .medicoText(textWeight: .regular, fontSize: 20, color: .placeholderGrey)
                    Text("|")
                        .medicoText(textWeight: .regular, fontSize: 20, color: .placeholderGrey)
                    Text("Units:")
                        .medicoText(textWeight: .regular, fontSize: 18, color: .placeholderGrey)
                    Text(invoiceEntry.standardUnit)
                        .medicoText(textWeight: .bold, fontSize: 14, color: .lightBlue)
                }
                Text(invoiceEntry.manufacturerName)
                    .medicoText(textWeight: .regular, fontSize: 14, color: .lightBlue)
            }
            Spacer()
        }
    }
    
    fileprivate func secondRowView() -> some View {
        func createCell(localizationKey: String, value: String) -> some View {
            HStack {
                LocalizedText(localizationKey: localizationKey,
                              textWeight: .semiBold,
                              fontSize: 16,
                              color: .lightBlue)
                    .lineLimit(1)
                Spacer()
                Text(value)
                    .medicoText(textWeight: .regular, fontSize: 16, color: .black)
                    .multilineTextAlignment(.leading)
            }
        }
        return VStack(spacing: 10) {
            HStack(spacing: 40) {
                createCell(localizationKey: "qty", value: invoiceEntry.quantity.formatted)
                createCell(localizationKey: "free", value: invoiceEntry.freeQty.formatted)
            }
            HStack(spacing: 40) {
                createCell(localizationKey: "price", value: invoiceEntry.price.formatted)
                createCell(localizationKey: "total", value: invoiceEntry.totalAmount.formatted)
            }
            HStack(spacing: 40) {
                createCell(localizationKey: "discount", value: invoiceEntry.discount.formatted)
                createCell(localizationKey: "discount", value: invoiceEntry.discount.formatted)
                    .opacity(0)
            }
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
        return VStack(spacing: 0) {
            if invoiceEntry.cgstTax.amount.value?.doubleValue ?? 0 > 0.0 || invoiceEntry.sgstTax.amount.value?.doubleValue ?? 0 > 0.0 || invoiceEntry.igstTax.amount.value?.doubleValue ?? 0 > 0.0 {
                let taxTypeString = invoiceEntry.taxType == .igst ? "IGST" : "GST"
                createCell(title: "\(taxTypeString)(\(invoiceEntry.gstTaxRate.string))", value: invoiceEntry.totalTaxAmount.formatted, isMain: true)
                if invoiceEntry.cgstTax.amount.value?.doubleValue ?? 0 > 0.0 {
                    createCell(title: "CGST(\(invoiceEntry.cgstTax.percent.formatted))", value: invoiceEntry.cgstTax.amount.formatted, isMain: false)
                }
                if invoiceEntry.sgstTax.amount.value?.doubleValue ?? 0 > 0.0 {
                    createCell(title: "SGST(\(invoiceEntry.sgstTax.percent.formatted))", value: invoiceEntry.sgstTax.amount.formatted, isMain: false)
                }
            }
            else {
                createCell(title: "GST(0.0%)", value: invoiceEntry.totalTaxAmount.formatted, isMain: true)
            }
        }
    
    }
}
