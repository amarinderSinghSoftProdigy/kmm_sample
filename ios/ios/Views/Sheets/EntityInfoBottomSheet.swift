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
                                maxHeight: bottomSheet.isSeasonBoy ? 275 : 370) {
                if bottomSheet.isSeasonBoy {
                    SeasonBoyDetailsItem(seasonBoy: bottomSheet.entityInfo)
                }
                else {
                    let onSubscribe: (() -> ())? = bottomSheet.canSubscribe ? { bottomSheet.subscribe() } : nil
                    
                    NonSeasonBoyDetailsItem(entityInfo: bottomSheet.entityInfo,
                                            onSubscribe: onSubscribe)
                }
            }
            .edgesIgnoringSafeArea(.all)
        )
    }
    
    private struct NonSeasonBoyDetailsItem: View {
        let entityInfo: DataEntityInfo
        let onSubscribe: (() -> ())?
        
        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 5) {
                    Text(entityInfo.tradeName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                    
                    Text(entityInfo.geoData.city)
                        .medicoText(textWeight: .medium,
                                    color: .grey3,
                                    multilineTextAlignment: .leading)
                }
                .fixedSize(horizontal: false, vertical: true)
                
                NonSeasonBoyImageAndAddressItem(tradeName: entityInfo.tradeName,
                                                fullAddress: entityInfo.geoData.fullAddress(),
                                                formattedDistance: entityInfo.geoData.formattedDistance,
                                                isVerified: entityInfo.isVerified == true,
                                                subscriptionData: entityInfo.subscriptionData,
                                                onSubscribe: onSubscribe,
                                                imageSize: 96,
                                                fontSize: 12)
                
                VStack(alignment: .leading, spacing: 5) {
                    if let subscriptionData = entityInfo.subscriptionData {
                        UserInfoItemDetailsPanel(titleKey: "status", valueKey: subscriptionData.status.serverValue)
                        
                        self.gstinAndPanFields
                        
                        UserInfoItemDetailsPanel(titleKey: "payment_method", valueKey: subscriptionData.paymentMethod.serverValue)
                        UserInfoItemDetailsPanel(titleKey: "orders", valueKey: String(subscriptionData.orders))
                    }
                    
                    else if let seasonBoyRetailerData = entityInfo.seasonBoyRetailerData {
                        self.gstinAndPanFields
                        
                        UserInfoItemDetailsPanel(titleKey: "orders",
                                                 valueKey: String(seasonBoyRetailerData.orders))
                    }
                    
                    else {
                        self.gstinAndPanFields
                    }
                }
                
                Spacer()
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
        
        private var gstinAndPanFields: some View {
            Group {
                if let gstin = entityInfo.gstin {
                    UserInfoItemDetailsPanel(titleKey: "gstin_number", valueKey: gstin)
                }
                if let panNumber = entityInfo.panNumber {
                    UserInfoItemDetailsPanel(titleKey: "pan_number", valueKey: panNumber)
                }
            }
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
                    
                    Text(seasonBoy.tradeName)
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
                    if let status = seasonBoy.subscriptionData?.status {
                        UserInfoItemDetailsPanel(titleKey: "status", valueKey: status.serverValue)
                    }
                    
                    if let email = seasonBoy.seasonBoyData?.email {
                        UserInfoItemDetailsPanel(titleKey: "email_address:", valueKey: email)
                    }
                    
                    UserInfoItemDetailsPanel(titleKey: "address:", valueKey: seasonBoy.geoData.fullAddress())
                    
                    if let orders = seasonBoy.subscriptionData?.orders {
                        UserInfoItemDetailsPanel(titleKey: "orders", valueKey: String(orders))
                    }
                    
                    if let retailers = seasonBoy.seasonBoyData?.retailers {
                        UserInfoItemDetailsPanel(titleKey: "retailers:", valueKey: String(retailers))
                    }
                }
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
    }
}

struct NonSeasonBoyImageAndAddressItem: View {
    let tradeName: String
    let fullAddress: String
    let formattedDistance: String
    let isVerified: Bool
    let subscriptionData: DataSubscriptionData?
    
    let onSubscribe: (() -> ())?
    
    let imageSize: CGFloat
    let fontSize: CGFloat
    
    var body: some View {
        HStack(alignment: .top, spacing: 50) {
            UserNameImage(username: tradeName)
                .frame(width: 96, height: 96)
            
            VStack(alignment: .leading, spacing: 13) {
                VStack(alignment: .leading, spacing: 5) {
                    SmallAddressView(location: fullAddress)
                    
                    Text(formattedDistance)
                        .medicoText(fontSize: 12,
                                    color: .grey3,
                                    multilineTextAlignment: .leading)
                    
//                    LocalizedText(localizationKey: "see_on_the_map",
//                                  textWeight: .bold,
//                                  fontSize: 12,
//                                  color: .lightBlue,
//                                  multilineTextAlignment: .leading)
                    
                    if isVerified {
                        HStack(spacing: 4) {
                            Image("VerifyMark")
                            
                            LocalizedText(localizationKey: "verified",
                                          textWeight: .semiBold,
                                          fontSize: 12,
                                          color: .green,
                                          multilineTextAlignment: .leading)
                        }
                    }
                }
                
                if let onSubscribe = self.onSubscribe,
                   subscriptionData == nil {
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
    
    init(tradeName: String,
         fullAddress: String,
         formattedDistance: String,
         isVerified: Bool,
         subscriptionData: DataSubscriptionData?,
         onSubscribe: (() -> ())?,
         imageSize: CGFloat = 125,
         fontSize: CGFloat = 14) {
        self.tradeName = tradeName
        self.fullAddress = fullAddress
        self.formattedDistance = formattedDistance
        self.isVerified = isVerified
        self.subscriptionData = subscriptionData
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
