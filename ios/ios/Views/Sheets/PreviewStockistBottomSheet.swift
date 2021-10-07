//
//  PreviewStockistBottomSheet.swift
//  Medico
//
//  Created by Dasha Gurinovich on 8.09.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct PreviewStockistBottomSheet: ViewModifier {
    let bottomSheet: BottomSheet.PreviewStockist
        
    let onBottomSheetDismiss: () -> ()
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 380) {
                VStack(alignment: .leading, spacing: 16) {
                    HStack(spacing: 16) {
                        UserNameImage(username: bottomSheet.sellerInfo.tradeName)
                            .frame(width: 78, height: 78)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text(bottomSheet.sellerInfo.tradeName)
                                .medicoText(textWeight: .bold,
                                            fontSize: 16,
                                            multilineTextAlignment: .leading)
                            
                            Text(bottomSheet.sellerInfo.geoData.full())
                                .medicoText(fontSize: 12,
                                            multilineTextAlignment: .leading)
                        }
                        .fixedSize(horizontal: false, vertical: true)
                    }
                    
                    VStack(spacing: 13) {
                        HStack {
                            if let price = bottomSheet.sellerInfo.priceInfo?.price.formattedPrice {
                                Text(price)
                                    .medicoText(textWeight: .bold,
                                                fontSize: 20)
                            }
                            
                            Spacer()
                            
                            if let stocks = bottomSheet.sellerInfo.stockInfo?.availableQty {
                                HStack(spacing: 5) {
                                    LocalizedText(localizationKey: "stocks:",
                                                  color: .grey3)
                                    
                                    Text(String(stocks))
                                        .medicoText(textWeight: .bold,
                                                    fontSize: 12,
                                                    color: .blue)
                                        .padding(.vertical, 2)
                                        .padding(.horizontal, 4)
                                        .background(
                                            RoundedRectangle(cornerRadius: 4)
                                                .fill(appColor: .blue)
                                                .opacity(0.08)
                                        )
                                }
                            }
                        }
                        
                        HStack {
                            if let expiry = bottomSheet.sellerInfo.stockInfo?.expiry {
                                DateExpiryView(dateExpiry: expiry)
                            }
                                
                            Spacer()
                            
                            DistanceView(distance: bottomSheet.sellerInfo.geoData.formattedDistance)
                        }
                        
                        if let priceInfo = bottomSheet.sellerInfo.priceInfo {
                            HStack {
                                getDetailsView(titleLocalizationKey: "mrp:", bodyText: priceInfo.mrp.formattedPrice)
                                
                                Spacer()
                                
                                getDetailsView(titleLocalizationKey: "margin:", bodyText: priceInfo.marginPercent)
                            }
                        }
                        
                        HStack {
                            getDetailsView(titleLocalizationKey: "batch_no:", bodyText: "N/A")
                            
                            Spacer()
                        }
                    }
                    .padding(.vertical, 12)
                    .padding(.horizontal, 16)
                    .background(
                        RoundedRectangle(cornerRadius: 4)
                            .stroke(AppColor.darkBlue.color, lineWidth: 1)
                            .opacity(0.12)
                    )
                }
                .padding(.horizontal, 22)
            }
            .edgesIgnoringSafeArea(.bottom)
        )
    }
    
    init(bottomSheet: BottomSheet.PreviewStockist,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
    }
    
    private func getDetailsView(titleLocalizationKey: String,
                                bodyText: String,
                                bodyColor: AppColor = .greyBlue) -> some View {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          color: .greyBlue)
            
            Text(bodyText)
                .medicoText(textWeight: .bold,
                            color: bodyColor)
        }
    }
}
