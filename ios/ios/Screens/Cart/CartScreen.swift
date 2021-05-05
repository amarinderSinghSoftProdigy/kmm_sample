//
//  CartScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 23.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct CartScreen: View {
    let scope: CartScope
    
    @State private var expandedItems = [String: Bool]()
    
    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var total: SwiftDataSource<DataTotal>
    
    var body: some View {
        if let items = self.items.value as? [DataSellerCart],
           items.count > 0 {
            self.getNonEmptyCartView(withSellersCartData: items)
        }
        else {
            self.emptyCartView
        }
    }
    
    init(scope: CartScope) {
        self.scope = scope
        
        self.items = SwiftDataSource(dataSource: scope.items)
        self.total = SwiftDataSource(dataSource: scope.total)
    }
    
    private var emptyCartView: some View {
        VStack {
            Spacer()
            
            VStack(spacing: 15) {
                Image("EmptyCart")
                
                VStack(spacing: 6) {
                    LocalizedText(localizationKey: "empty_cart_title",
                                  textWeight: .bold,
                                  fontSize: 16)
                    
                    LocalizedText(localizationKey: "empty_cart_description",
                                  textWeight: .medium,
                                  color: .grey3)
                }
            }
            .padding(10)
            
            Spacer()
            
            MedicoButton(localizedStringKey: "go_back") {
                scope.goBack()
            }
        }
        .padding(.horizontal, 16)
        .padding(.bottom, 84)
    }
    
    private func getNonEmptyCartView(withSellersCartData sellersCartData: [DataSellerCart]) -> some View {
        VStack(spacing: 12) {
            HStack(spacing: 8) {
                StatusDescriptionView(color: .green, localizationKey: "available")
                StatusDescriptionView(color: .orange, localizationKey: "limited")
                StatusDescriptionView(color: .placeholderGrey, localizationKey: "quoted")
                
                Spacer()
                
                MedicoButton(localizedStringKey: "empty_cart",
                             width: 105,
                             height: 40,
                             fontSize: 16,
                             fontColor: .red,
                             buttonColor: .lightPink) {
                    scope.clearCart()
                }
            }
            
            VStack(spacing: 8) {
                ForEach(sellersCartData, id: \.self) { seller in
                    let expanded = Binding(get: { expandedItems[seller.sellerCode] == true },
                                           set: { expandedItems[seller.sellerCode] = $0 })
                    
                    SellerCartDataView(seller: seller,
                                       onRemoveSeller: { scope.removeSellerItems(sellerCart: seller) },
                                       onIncreaseItem: { updateItemCount($0,
                                                                         forSeller: seller,
                                                                         increment: 1)},
                                       onDecreaseItem: { updateItemCount($0,
                                                                         forSeller: seller,
                                                                         increment: -1)},
                                       onRemoveItem: { scope.removeItem(sellerCart: seller,
                                                                        item: $0)},
                                       expanded: expanded)
                }
            }.scrollView()
            
            if let price = total.value?.formattedPrice {
                VStack(spacing: 35) {
                    AppColor.black.color
                        .opacity(0.27)
                        .frame(height: 1)
                    
                    HStack {
                        HStack(spacing: 4) {
                            LocalizedText(localizationKey: "total",
                                          textWeight: .medium,
                                          fontSize: 20)
                            
                            Text(price)
                                .medicoText(textWeight: .bold,
                                            fontSize: 20)
                        }
                    
                        Spacer()
                        
                        MedicoButton(localizedStringKey: "continue",
                                     width: 170,
                                     height: 50,
                                     fontSize: 15,
                                     fontWeight: .bold) {
                            scope.continueWithCart()
                        }
                    }
                }
            }
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 25)
    }
    
    private func updateItemCount(_ item: DataCartItem,
                                 forSeller seller: DataSellerCart,
                                 increment: Int32) {
        guard let quantity = item.quantity.value as? Int32 else { return }
        
        scope.updateItemCount(sellerCart: seller,
                              item: item,
                              quantity: quantity + increment)
    }
    
    private struct SellerCartDataView: View {
        private let cornerRadius: CGFloat = 4
        
        let seller: DataSellerCart
        
        let onRemoveSeller: () -> ()
        let onIncreaseItem: (DataCartItem) -> ()
        let onDecreaseItem: (DataCartItem) -> ()
        let onRemoveItem: (DataCartItem) -> ()
        
        @Binding var expanded: Bool
        
        var body: some View {
            VStack {
                HStack(spacing: 16) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(seller.sellerName)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 16,
                                        multilineTextAlignment: .leading)
                        
                        HStack(spacing: 4) {
                            LocalizedText(localizationKey: "payment_method",
                                          textWeight: .medium,
                                          fontSize: 12,
                                          color: .grey3,
                                          multilineTextAlignment: .leading)
                            
                            Text(seller.paymentMethod.serverValue)
                                .medicoText(textWeight: .medium,
                                            fontSize: 12,
                                            color: .lightBlue,
                                            multilineTextAlignment: .leading)
                        }
                    }
                    .padding(.vertical, 8)
                    
                    Spacer()
                    
                    Button(action: { expanded.toggle() }) {
                        Image(systemName: "chevron.right")
                            .foregroundColor(appColor: .darkBlue)
                            .opacity(0.54)
                            .rotationEffect(.degrees(expanded ? -90 : 90))
                            .animation(.linear(duration: 0.2))
                            .padding(.trailing, 18)
                    }
                }
                .padding(.leading, 60)
                .background(
                    HStack {
                        ZStack {
                            AppColor.red.color
                                .cornerRadius(cornerRadius,
                                              corners: expanded ? .topLeft : [.topLeft, .bottomLeft])
                            
                            Image("Bin")
                        }
                        .frame(width: 45)
                        .onTapGesture {
                            onRemoveSeller()
                        }
                        
                        Spacer()
                    }
                )
                .strokeBorder(.darkBlue,
                              borderOpacity: 0.12,
                              fill: .darkBlue,
                              fillOpacity: 0.04,
                              cornerRadius: cornerRadius,
                              corners: expanded ? [.topLeft, .topRight] : .allCorners)
                
                if expanded {
                    VStack {
                        ForEach(seller.items, id: \.self) { item in
                            CartItemView(item: item,
                                         onIncreaseItem: onIncreaseItem,
                                         onDecreaseItem: onDecreaseItem,
                                         onRemoveItem: onRemoveItem)
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                }
            }
            .strokeBorder(.darkBlue,
                          borderOpacity: 0.12,
                          fill: .white,
                          cornerRadius: cornerRadius)
        }
        
        private struct CartItemView: View {
            let item: DataCartItem
            
            let onIncreaseItem: (DataCartItem) -> ()
            let onDecreaseItem: (DataCartItem) -> ()
            let onRemoveItem: (DataCartItem) -> ()
            
            var body: some View {
                ZStack(alignment: .leading) {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack(alignment: .top) {
                            Text(item.productName)
                                .medicoText(textWeight: .bold,
                                            fontSize: 16,
                                            multilineTextAlignment: .leading)
                                .padding(.top, 5)
                            
                            Spacer()
                            
                            Button(action: { onRemoveItem(item) }) {
                                Image(systemName: "xmark")
                                    .resizable()
                                    .frame(width: 11, height: 11)
                                    .font(Font.system(size: 14, weight: .medium))
                                    .foregroundColor(.red)
                                    .padding(7)
                                    .background(
                                        Circle()
                                            .foregroundColor(appColor: .lightPink)
                                    )
                            }
                        }
                        
                        HStack(alignment: .top, spacing: 8) {
                            HStack(alignment: .top, spacing: 4) {
                                Image(systemName: "info.circle.fill")
                                    .foregroundColor(appColor: .lightBlue)
                                
                                Text(item.manufacturerName)
                                    .medicoText(textWeight: .medium,
                                                color: .lightBlue,
                                                multilineTextAlignment: .leading)
                            }
                            
                            AppColor.darkBlue.color
                                .opacity(0.33)
                                .frame(width: 2, height: 13)
                                .padding(.top, 2)
                                .cornerRadius(1)
                            
                            Text(item.standardUnit)
                                .medicoText(textWeight: .medium,
                                            color: .grey3,
                                            multilineTextAlignment: .leading)
                        }
                        
                        HStack {
                            Text(item.price.formatted)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            multilineTextAlignment: .leading)
                            
                            Spacer()
                            
                            NumberPicker(quantity: item.quantity.value as? Int ?? 0,
                                         onQuantityIncrease: { onIncreaseItem(self.item) },
                                         onQuantityDecrease: { onDecreaseItem(self.item) })
                        }
                        
                        if let seasonBoyRetailerInfo = self.item.seasonBoyRetailer {
                            VStack(spacing: 4) {
                                AppColor.darkBlue.color
                                    .opacity(0.33)
                                    .frame(height: 1)
                                    .padding(.leading, -12)
                                    .padding(.trailing, -8)
                                
                                HStack {
                                    Text(seasonBoyRetailerInfo.tradeName)
                                        .medicoText(textWeight: .medium,
                                                    fontSize: 12,
                                                    color: AppColor.grey3,
                                                    multilineTextAlignment: .leading)
                                    
                                    Spacer()
                                    
                                    SmallAddressView(location: seasonBoyRetailerInfo.city)
                                }
                                .frame(height: 20)
                            }
                        }
                    }
                    .fixedSize(horizontal: false, vertical: true)
                    .padding([.vertical, .trailing], 8)
                    .padding(.leading, 12)
                    
                    let statusColor = item.stockInfo?.statusColor ?? .placeholderGrey
                    statusColor.color
                        .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                        .frame(width: 5)
                }
                .strokeBorder(.darkBlue,
                              borderOpacity: 0.12,
                              fill: .white,
                              cornerRadius: 5)
            }
        }
    }
    
    private struct StatusDescriptionView: View {
        let color: AppColor
        let localizationKey: String
        
        var body: some View {
            HStack(spacing: 4) {
                Circle()
                    .foregroundColor(appColor: color)
                    .frame(width: 18, height: 18)
                
                LocalizedText(localizationKey: localizationKey,
                              textWeight: .semiBold,
                              fontSize: 12)
            }
        }
    }
}
