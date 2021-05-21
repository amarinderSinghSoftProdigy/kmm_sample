//
//  PlaceOrderScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 21.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct PlaceOrderScreen: View {
    let scope: CartPreviewScope
    
    @State private var expandedItems = [String: Bool]()
    
    @ObservedObject var sellerCarts: SwiftDataSource<NSArray>
    @ObservedObject var total: SwiftDataSource<DataTotal>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            LocalizedText(localizationKey: "place_your_order",
                          textWeight: .bold,
                          fontSize: 20,
                          multilineTextAlignment: .leading)
            
            Text("11-32-12, Challamraju Vari Streen, One Town, Vijaywada, Krishna, 520000")
                .medicoText(multilineTextAlignment: .leading)
                .padding(.horizontal, 5)
                .padding(.bottom, 13)
                .frame(maxWidth: .infinity, alignment: .leading)
                .expandableView(expanded: .init(get: { expandedItems["shipping_address"] == true },
                                                set: { expandedItems["shipping_address"] = $0 })) {
                    LocalizedText(localizationKey: "shipping_address",
                                  textWeight: .bold,
                                  multilineTextAlignment: .leading)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.leading, 12)
                        .padding(.vertical, 10)
                }
            
            if let sellerCarts = self.sellerCarts.value as? [DataSellerCart] {
                VStack(spacing: 8) {
                    ForEach(sellerCarts, id: \.self) { sellerCart in
                        let expanded = Binding(get: { expandedItems[sellerCart.sellerCode] == true },
                                               set: { expandedItems[sellerCart.sellerCode] = $0 })
                        
                        SellerCartView(sellerCart: sellerCart,
                                       expanded: expanded)
                    }
                }
                .scrollView()
            }
            
            Spacer()
            
            VStack(spacing: 32) {
                if let totalPrice = self.total.value?.formattedPrice {
                    HStack {
                        LocalizedText(localizationKey: "order_total:",
                                      textWeight: .medium,
                                      fontSize: 20,
                                      multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        VStack(alignment: .trailing, spacing: 4) {
                            Text(totalPrice)
                                .medicoText(textWeight: .bold,
                                            fontSize: 20,
                                            multilineTextAlignment: .trailing)
                            
                            LocalizedText(localizationKey: "exclusive_of_all_taxes",
                                          textWeight: .semiBold,
                                          fontSize: 10,
                                          color: .lightBlue,
                                          multilineTextAlignment: .trailing)
                        }
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(
                        VStack {
                            AppColor.black.color
                                .opacity(0.27)
                                .frame(height: 1)
                            
                            Spacer()
                            
                            AppColor.black.color
                                .opacity(0.27)
                                .frame(height: 1)
                        }
                    )
                }
                
                MedicoButton(localizedStringKey: "place_order") {
                    scope.placeOrder()
                }
            }
        }
        .padding(.horizontal, 22)
        .padding(.top, 28)
        .padding(.bottom, 20)
        .notificationAlertSender(withHandler: self.scope)
    }
    
    init(scope: CartPreviewScope) {
        self.scope = scope
        
        self.sellerCarts = SwiftDataSource(dataSource: scope.items)
        self.total = SwiftDataSource(dataSource: scope.total)
    }
    
    private struct SellerCartView: View {
        let sellerCart: DataSellerCart
        let expanded: Binding<Bool>
        
        var body: some View {
            VStack(spacing: 8) {
                ForEach(sellerCart.items, id: \.self) {
                    CartItemView(item: $0,
                                 showPriceCaption: true)
                }
            }
            .expandableView(expanded: expanded) {
                HStack {
                    SellerNamePaymentMethodView(seller: sellerCart)
                    
                    Spacer()
                    
                    VStack(alignment: .trailing, spacing: 2) {
                        LocalizedText(localizationKey: "total",
                                      textWeight: .medium,
                                      fontSize: 12,
                                      color: .grey3)
                        
                        PriceWithFootnoteView(price: sellerCart.total.formattedPrice,
                                              fontSize: 16)
                    }
                }
                .padding(.vertical, 8)
                .padding(.leading, 20)
            }
        }
    }
}
