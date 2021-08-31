//
//  OrderPlacedScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.06.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct OrderPlacedScreen: View {
    let scope: OrderPlacedScope
    
    var body: some View {
        VStack(spacing: 25) {
            VStack(spacing: 10) {
                Image("OrderSuccess")
                
                LocalizedText(localizedStringKey: LocalizedStringKey("thank_you_for_fulfilment \(scope.order.tradeName)"),
                              textWeight: .semiBold,
                              fontSize: 16,
                              multilineTextAlignment: .center)
                
                HStack(spacing: 8) {
                    getOrderInfoDetailView(withTitleLocalizationKey: "date:",
                                           withBody: scope.order.info.date)
                    
                    getOrderInfoDetailView(withTitleLocalizationKey: "time:",
                                           withBody: scope.order.info.time)
                }
            }
            
            MedicoButton(localizedStringKey: "new_orders",
                         isEnabled: true,
                         cornerRadius: 6,
                         fontColor: .white,
                         buttonColor: .lightBlue) {
                scope.goHome()
            }
        }
        .padding(28)
        .frame(maxWidth: 500)
        .strokeBorder(.green,
                      fill: .green,
                      fillOpacity: 0.06,
                      lineWidth: 2)
        .padding(16)
        .frame(maxHeight: .infinity)
    }
    
    private func getOrderInfoDetailView(withTitleLocalizationKey titleLocalizationKey: String,
                                        withBody body: String) -> some View {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          color: .grey3)
            
            Text(body)
                .medicoText(color: .lightBlue)
        }
    }
}
