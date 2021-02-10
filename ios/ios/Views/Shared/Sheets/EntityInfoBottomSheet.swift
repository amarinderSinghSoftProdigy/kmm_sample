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
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: bottomSheet.isSeasonBoy ? 275 : 350) {
                if bottomSheet.isSeasonBoy {
                    SeasonBoyDetailsItem(seasonBoy: bottomSheet.entityInfo)
                }
                else {
                    NonSeasonBoyDetailsItem(entityInfo: bottomSheet.entityInfo,
                                            onSubscribe: { bottomSheet.subscribe() })
                }
            }
            .edgesIgnoringSafeArea(.all)
        )
    }
    
    private struct NonSeasonBoyDetailsItem: View {
        let entityInfo: DataEntityInfo
        let onSubscribe: () -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
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
                
                NonSeasonBoyImageAndAddressItem(previewItem: entityInfo,
                                                onSubscribe: onSubscribe,
                                                imageSize: 96,
                                                fontSize: 12)
                
                VStack(alignment: .leading, spacing: 5) {
                    UserInfoItemDetailsPanel(titleKey: "gstin_number", valueKey: entityInfo.gstin)
                    
                    if let subscriptionData = entityInfo.subscriptionData{
                        UserInfoItemDetailsPanel(titleKey: "status", valueKey: subscriptionData.status.serverValue)
                        UserInfoItemDetailsPanel(titleKey: "payment_method", valueKey: subscriptionData.paymentMethod.serverValue)
                        UserInfoItemDetailsPanel(titleKey: "orders", valueKey: subscriptionData.orders)
                    }
                }
                
                Spacer()
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
    }
    
    private struct SeasonBoyDetailsItem: View {
        let seasonBoy: DataEntityInfo
        
        var body: some View {
            VStack(alignment: .leading) {
                HStack {
                    Image("SeasonBoy")
                        .resizable()
                        .renderingMode(.template)
                        .foregroundColor(appColor: .darkBlue)
                        .frame(width: 24, height: 24)
                    
                    Text(seasonBoy.traderName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                }
                
                let phoneNumber = PhoneNumberUtil.shared.getFormattedPhoneNumber(seasonBoy.phoneNumber)
                Text(phoneNumber)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 16,
                                color: .lightBlue,
                                multilineTextAlignment: .leading)
                
                AppColor.black.color.opacity(0.42)
                    .frame(height: 1)
                
                VStack(alignment: .leading, spacing: 5) {
//                    getDataPanel(withTitleKey: "email_address:", withValueKey: seasonBoy.gstin)
                    UserInfoItemDetailsPanel(titleKey: "address:", valueKey: seasonBoy.location)
//                    getDataPanel(withTitleKey: "pending_orders", withValueKey: "10")
//                    getDataPanel(withTitleKey: "total_orders", withValueKey: "24")
                }
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
    }
}

struct NonSeasonBoyImageAndAddressItem: View {
    let previewItem: DataPreviewItem
    let onSubscribe: () -> ()
    
    let imageSize: CGFloat
    let fontSize: CGFloat
    
    var body: some View {
        HStack(spacing: 50) {
            URLImage(withURL: "", withDefaultImageName: "DefaultProduct")
                .frame(width: 96, height: 96)
            
            VStack(alignment: .leading, spacing: 13) {
                VStack(alignment: .leading,spacing: 5) {
                    SmallAddressView(location: previewItem.location)
                    
                    Text(previewItem.distance)
                        .medicoText(fontSize: 12,
                                    color: .grey3,
                                    multilineTextAlignment: .leading)
                    
                    LocalizedText(localizationKey: "see_on_the_map",
                                  textWeight: .bold,
                                  fontSize: 12,
                                  color: .lightBlue,
                                  multilineTextAlignment: .leading)
                }
                
                if let entityInfo = self.previewItem as? DataEntityInfo,
                   entityInfo.subscriptionData == nil {
                    MedicoButton(localizedStringKey: "subscribe",
                                 width: 91,
                                 height: 31,
                                 cornerRadius: 5,
                                 fontSize: 14,
                                 fontWeight: .bold) {
                        onSubscribe()
                    }
                }
            }
        }
    }
    
    init(previewItem: DataPreviewItem,
         onSubscribe: @escaping () -> (),
         imageSize: CGFloat = 125,
         fontSize: CGFloat = 14) {
        self.previewItem = previewItem
        self.onSubscribe = onSubscribe
        
        self.imageSize = imageSize
        self.fontSize = fontSize
    }
}

struct UserInfoItemDetailsPanel: View {
    let titleKey: String
    let valueKey: String
    
    var body: some View {
        HStack(spacing: 3) {
            LocalizedText(localizationKey: titleKey,
                          multilineTextAlignment: .leading)
            
            LocalizedText(localizationKey: valueKey,
                          textWeight: .medium,
                          multilineTextAlignment: .leading)
        }
    }
}
