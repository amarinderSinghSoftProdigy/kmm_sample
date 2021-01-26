//
//  EntityInfoBottomSheet.swift
//  Medico
//
//  Created by Dasha Gurinovich on 25.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct EntityInfoBottomSheet: ViewModifier {
    let bottomSheet: BottomSheet.PreviewManagementItem
        
    let onBottomSheetDismiss: () -> ()
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened, maxHeight: 350) {
                ManagementItemDetails(entityInfo: bottomSheet.entityInfo,
                                      onSubscribe: { bottomSheet.subscribe() })
            }
            .edgesIgnoringSafeArea(.all)
        )
    }
    
    private struct ManagementItemDetails: View {
        let entityInfo: DataEntityInfo
        let onSubscribe: () -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 5) {
                        Text(entityInfo.traderName)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 20,
                                        multilineTextAlignment: .leading)
                        
                        Text(entityInfo.city)
                            .medicoText(textWeight: .medium,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                    .fixedSize(horizontal: false, vertical: true)
                    
                    Spacer()
                    
                    if entityInfo.subscriptionData == nil {
                        MedicoButton(localizedStringKey: "subscribe",
                                     width: 91,
                                     height: 31,
                                     cornerRadius: 5,
                                     fontSize: 14) {
                            onSubscribe()
                        }
                    }
                }
                
                HStack {
                    HStack(spacing: 60) {
                        URLImage(withURL: "", withDefaultImageName: "DefaultProduct")
                            .frame(width: 96, height: 96)
                        
                        VStack(alignment: .leading, spacing: 13) {
                            SmallAddresView(location: entityInfo.location, pincode: entityInfo.pincode)
                            
                            VStack(alignment: .leading,spacing: 5) {
                                Text(entityInfo.distance)
                                    .medicoText(fontSize: 12,
                                                color: .grey3,
                                                multilineTextAlignment: .leading)
                                
                                LocalizedText(localizationKey: "see_on_the_map",
                                              textWeight: .bold,
                                              fontSize: 12,
                                              color: .lightBlue,
                                              multilineTextAlignment: .leading)
                            }
                        }
                    }
                    
                    Spacer()
                }
                
                VStack(alignment: .leading, spacing: 5) {
                    getDataPanel(withTitleKey: "gstin_number", withValueKey: entityInfo.gstin)
                    
                    if let subscriptionData = entityInfo.subscriptionData{
                        getDataPanel(withTitleKey: "status", withValueKey: subscriptionData.status.serverValue)
                        getDataPanel(withTitleKey: "payment_method", withValueKey: subscriptionData.paymentMethod.serverValue)
                        getDataPanel(withTitleKey: "orders", withValueKey: subscriptionData.orders)
                    }
                }
                
                Spacer()
            }
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
        
        private func getDataPanel(withTitleKey titleKey: String,
                                  withValueKey valueKey: String) -> some View {
            HStack(spacing: 3) {
                LocalizedText(localizationKey: titleKey,
                              multilineTextAlignment: .leading)
                
                LocalizedText(localizationKey: valueKey,
                              textWeight: .medium,
                              multilineTextAlignment: .leading)
            }
        }
    }
}
