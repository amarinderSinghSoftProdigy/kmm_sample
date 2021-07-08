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
    @ObservedObject var quantity: SwiftDataSource<KotlinInt>
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 370) {
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
                        
                        if bottomSheet.canEdit {
                            NumberPicker(quantity: Int(truncating: self.quantity.value ?? 0),
                                         onQuantityIncrease: { bottomSheet.inc(tapMode: $0) },
                                         onQuantityDecrease: { bottomSheet.dec(tapMode: $0) },
                                         longPressEnabled: true)
                        }
                        else {
                            getDetailsView(titleLocalizationKey: "served_qty:",
                                           bodyText: bottomSheet.orderEntry.servedQty.formatted,
                                           bodyColor: .lightBlue)
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
            .edgesIgnoringSafeArea(.all)
        )
    }
    
    init(bottomSheet: BottomSheet.ModifyOrderEntry,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
        
        self.checked = .init(dataSource: bottomSheet.isChecked)
        self.quantity = .init(dataSource: bottomSheet.quantity)
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
