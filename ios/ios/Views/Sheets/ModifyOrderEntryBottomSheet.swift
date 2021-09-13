//
//  ModifyOrderEntryBottomSheet.swift
//  Medico
//
//  Created by Dasha Gurinovich on 28.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ModifyOrderEntryBottomSheet: ViewModifier {
    let bottomSheet: BottomSheet.ModifyOrderEntry
        
    let onBottomSheetDismiss: () -> ()
    
    @ObservedObject var checked: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var batch: SwiftDataSource<NSString>
    @ObservedObject var expiry: SwiftDataSource<NSString>
    @ObservedObject var ptr: SwiftDataSource<NSString>
    @ObservedObject var quantity: SwiftDataSource<KotlinDouble>
    @ObservedObject var freeQuantity: SwiftDataSource<KotlinDouble>
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: bottomSheet.canEdit ? 425 : 370) {
                VStack(spacing: 30) {
                    HStack(alignment: .top, spacing: 20) {
                        if bottomSheet.canEdit {
                            CheckBox(selected: .init(get: { checked.value == true },
                                                     set: { _ in bottomSheet.toggleCheck() }))
                                .frame(width: 22, height: 22)
                        }
                        
                        VStack(alignment: .leading) {
                            Text(bottomSheet.orderEntry.productName)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 20,
                                            multilineTextAlignment: .leading)
                            
                            if !bottomSheet.orderEntry.batchNo.isEmpty {
                                LocalizedText(localizedStringKey: LocalizedStringKey("batch_no \(bottomSheet.orderEntry.batchNo)"),
                                              textWeight: .medium)
                            }
                        }
                        .padding(.top, -2)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Group {
                        if bottomSheet.canEdit {
                            editableOrderFieldsView
                        }
                        else {
                            noneditableOrderFieldsView
                        }
                    }
                    .padding(.vertical, 20)
                    .background(
                        VStack {
                            AppColor.black.color
                                .opacity(0.12)
                                .frame(height: 1)
                            
                            Spacer()
                            
                            AppColor.black.color
                                .opacity(0.12)
                                .frame(height: 1)
                        }
                    )
                    
                    HStack {
                        LocalizedText(localizedStringKey: LocalizedStringKey("subtotal \(bottomSheet.orderEntry.totalAmount.formatted)"),
                                      textWeight: .semiBold,
                                      fontSize: 20)
                        
                        if bottomSheet.canEdit {
                            Spacer()
                            
                            MedicoButton(localizedStringKey: "save",
                                         isEnabled: true,
                                         width: 88,
                                         height: 40,
                                         cornerRadius: 6,
                                         fontSize: 14,
                                         fontColor: .white,
                                         buttonColor: .lightBlue) {
                                bottomSheet.save()
                            }
                        }
                    }
                }
                .padding(25)
            }
            .textFieldsModifiers()
            .edgesIgnoringSafeArea(.bottom)
        )
    }
    
    init(bottomSheet: BottomSheet.ModifyOrderEntry,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
        
        self.checked = .init(dataSource: bottomSheet.isChecked)
        
        self.batch = .init(dataSource: bottomSheet.batch)
        self.expiry = .init(dataSource: bottomSheet.expiry)
        self.ptr = .init(dataSource: bottomSheet.ptr)
        self.quantity = .init(dataSource: bottomSheet.quantity)
        self.freeQuantity = .init(dataSource: bottomSheet.freeQuantity)
    }
    
    private var editableOrderFieldsView: some View {
        VStack(spacing: 28) {
            let columnsSpacing: CGFloat = 20
            let leftColumnWidth: CGFloat = 120
            
            HStack(spacing: columnsSpacing) {
                EditableInput(titleLocalizationKey: "QTY",
                              text: "\(quantity.value ?? 0)",
                              onTextChange: {
                                if let newValue = Double($0) {
                                    bottomSheet.updateQuantity(value: newValue)
                                }
                              },
                              keyboardType: .decimalPad)
                    .frame(width: leftColumnWidth)
                
                Spacer()
                
                EditableInput(titleLocalizationKey: "FREE",
                              text: "\(freeQuantity.value ?? 0)",
                              onTextChange: {
                                if let newValue = Double($0) {
                                    bottomSheet.updateFreeQuantity(value: newValue)
                                }
                              },
                              keyboardType: .decimalPad)
            }
            
            HStack(spacing: columnsSpacing) {
                EditableInput(titleLocalizationKey: "PTR",
                              text: ptr.value as String?,
                              onTextChange: { bottomSheet.updatePtr(value: $0) },
                              keyboardType: .decimalPad)
                    .frame(width: leftColumnWidth)
                
                Spacer()
                
                EditableInput(titleLocalizationKey: "BATCH",
                              text: batch.value as String?,
                              onTextChange: { bottomSheet.updateBatch(value: $0) })
            }
            
            HStack(spacing: columnsSpacing) {
                EditableInput(titleLocalizationKey: "EXP",
                              text: expiry.value as String?,
                              onTextChange: { bottomSheet.updateExpiry(value: $0) },
                              keyboardType: .numbersAndPunctuation)
                    .frame(width: 180)
                
                Spacer()
            }
        }
    }
    
    private var noneditableOrderFieldsView: some View {
        HStack {
            VStack(alignment: .leading, spacing: 8) {
                getDetailsView(titleLocalizationKey: "price:",
                               bodyText: bottomSheet.orderEntry.price.formatted)
                
                getDetailsView(titleLocalizationKey: "mrp:",
                               bodyText: bottomSheet.orderEntry.mrp.formatted)
                
                getDetailsView(titleLocalizationKey: "requested_qty:",
                               bodyText: bottomSheet.orderEntry.requestedQty.formatted,
                               bodyColor: .lightBlue)
            }
            
            Spacer()
            
            getDetailsView(titleLocalizationKey: "served_qty:",
                           bodyText: bottomSheet.orderEntry.servedQty.formatted,
                           bodyColor: .lightBlue)
        }
    }
    
    private func getDetailsView(titleLocalizationKey: String,
                                bodyText: String,
                                bodyColor: AppColor = .darkBlue) -> some View {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          textWeight: .medium)
            
            Text(bodyText)
                .medicoText(textWeight: .bold,
                            color: bodyColor)
        }
    }
}
