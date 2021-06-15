//
//  CompletedCartOrderScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 21.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct CompletedCartOrderScreen: View {
    let scope: CartOrderCompletedScope
    
    var body: some View {
        VStack(spacing: 15) {
            self.orderInfoView
            
            self.sellersView
            
            CartOrderTotalPriceView(price: scope.total.formattedPrice)
            
            Spacer()
            
            MedicoButton(localizedStringKey: "orders") {
                scope.goToOrders()
            }
         }
        .padding(.horizontal, 20)
        .padding(.top, 23)
        .padding(.bottom, 50)
    }
    
    private var orderInfoView: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 15) {
                Image("OrderSuccess")
                
                VStack(alignment: .leading, spacing: 5) {
                    LocalizedText(localizationKey: "order_success",
                                  textWeight: .semiBold,
                                  fontSize: 16,
                                  multilineTextAlignment: .leading)
                    
                    HStack(spacing: 8) {
                        getOrderInfoDetailView(withTitleLocalizationKey: "date:",
                                               withBody: scope.order.orderDate)
                        
                        getOrderInfoDetailView(withTitleLocalizationKey: "time:",
                                               withBody: scope.order.orderTime)
                    }
                }
            }
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
            .strokeBorder(.green,
                          fill: .green,
                          fillOpacity: 0.06,
                          corners: [.topLeft, .topRight])
            
            HStack(spacing: 12) {
                Image(systemName: "info.circle")
                    .resizable()
                    .foregroundColor(appColor: .lightBlue)
                    .frame(width: 18, height: 18)
                
                VStack(alignment: .leading, spacing: 2) {
                    LocalizedText(localizationKey: "sent_confirmation",
                                  fontSize: 12,
                                  color: .grey3)
                        .minimumScaleFactor(0.2)
                        .lineLimit(1)
                    
                    Text(scope.order.email)
                        .medicoText(textWeight: .medium,
                                    fontSize: 12,
                                    color: .lightBlue,
                                    multilineTextAlignment: .leading)
                }
            }
            .padding(12)
        }
        .strokeBorder(.green,
                      fill: .white,
                      lineWidth: 2)
    }
    
    private var sellersView: some View {
        VStack(spacing: 8) {
            ForEach(self.scope.order.sellersOrder, id: \.self) {
                SellerView(seller: $0)
            }
        }
        .scrollView()
    }
    
    private func getOrderInfoDetailView(withTitleLocalizationKey titleLocalizationKey: String,
                                        withBody body: String) -> some View {
        HStack(spacing: 2) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          textWeight: .medium,
                          fontSize: 12,
                          color: .grey3)
            
            Text(body)
                .medicoText(textWeight: .medium,
                            fontSize: 12,
                            color: .lightBlue)
        }
    }
    
    private struct SellerView: View {
        let seller: DataSellerOrder
        
        var body: some View {
            VStack(spacing: 4) {
                HStack(alignment: .top) {
                    Text(seller.tradeName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 16)
                        .lineLimit(1)
                        .padding(.top, 3)
                    
                    Spacer()
                    
                    Text("99")
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 12)
                        .scaledToFit()
                        .frame(width: 24, height: 24)
                        .background(
                            Circle()
                                .foregroundColor(appColor: .darkBlue)
                                .opacity(0.08)
                        )
                }
                
                HStack(alignment: .bottom) {
                    VStack(spacing: 5) {
                        if let seasonBoyRetailerName = seller.seasonBoyRetailerName {
                            Text(seasonBoyRetailerName)
                                .medicoText(textWeight: .medium,
                                            fontSize: 11,
                                            color: .grey3)
                        }
                        
                        HStack(spacing: 8) {
                            HStack(spacing: 2) {
                                LocalizedText(localizationKey: "type:",
                                              textWeight: .medium,
                                              fontSize: 12,
                                              color: .grey3)
                                
                                Text(seller.paymentMethod.serverValue)
                                    .medicoText(textWeight: .medium,
                                                fontSize: 12,
                                                color: .lightBlue)
                            }
                            
                            AppColor.darkBlue.color
                                .opacity(0.4)
                                .frame(width: 1, height: 10)
                            
                            Text(seller.orderId)
                                .medicoText(textWeight: .medium,
                                            fontSize: 12,
                                            color: .lightBlue)
                        }
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .trailing, spacing: 2) {
                        LocalizedText(localizationKey: "total",
                                      textWeight: .medium,
                                      fontSize: 12,
                                      color: .grey3)
                        
                        PriceWithFootnoteView(price: seller.total.formattedPrice,
                                              fontSize: 16)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .strokeBorder(.darkBlue,
                          borderOpacity: 0.12,
                          fill: .white,
                          cornerRadius: 4)
        }
    }
}
