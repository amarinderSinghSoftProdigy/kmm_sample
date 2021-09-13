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
        VStack(spacing: 12) {
            if let sellerCarts = self.sellerCarts.value as? [DataSellerCart] {
                VStack(spacing: 0) {
                    HStack(spacing: 8) {
                        CartInfoView(sellers: sellerCarts)
                    }
                    .padding(8)
                    .background(appColor: .white)
                    
                    AppColor.lightGrey.color
                        .frame(height: 1)
                }
            
                VStack(spacing: 8) {
                    ForEach(sellerCarts, id: \.self) { seller in
                        let expanded = Binding(get: { expandedItems[seller.sellerCode] == true },
                                               set: { expandedItems[seller.sellerCode] = $0 })
                        
                        SellerCartDataView(seller: seller,
                                           isReadonly: true,
                                           onRemoveSeller: nil,
                                           onRemoveItem: nil,
                                           expanded: expanded)
                    }
                }.scrollView()
            }
            
            if let price = total.value?.formattedPrice {
                Spacer()
                
                VStack(spacing: 12) {
                    AppColor.black.color
                        .opacity(0.27)
                        .frame(height: 1)
                    
                    HStack(spacing: 6) {
                        HStack(spacing: 4) {
                            LocalizedText(localizationKey: "total:",
                                          textWeight: .medium,
                                          fontSize: 20)
                            
                            Text(price)
                                .medicoText(textWeight: .bold,
                                            fontSize: 20)
                        }
                    
                        Spacer()
                        
                        MedicoButton(localizedStringKey: "place_order",
                                     width: 170,
                                     height: 48,
                                     cornerRadius: 24,
                                     fontSize: 15,
                                     fontWeight: .bold) {
                            scope.placeOrder()
                        }
                    }
                    .padding(.horizontal, 17)
                }
            }
        }
        .padding(.vertical, 25)
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
                    CartItemView(item: $0, isReadonly: true)
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

struct CartOrderTotalPriceView: View {
    let price: String
    
    var body: some View {
        HStack {
            LocalizedText(localizationKey: "order_total:",
                          textWeight: .medium,
                          fontSize: 20,
                          multilineTextAlignment: .leading)
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 4) {
                Text(price)
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
}
