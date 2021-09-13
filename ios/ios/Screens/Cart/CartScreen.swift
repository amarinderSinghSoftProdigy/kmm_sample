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
    
    @ObservedObject var isContinueEnabled: SwiftDataSource<KotlinBoolean>
    
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
        
        self.isContinueEnabled = SwiftDataSource(dataSource: scope.isContinueEnabled)
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
            VStack(spacing: 0) {
                HStack(spacing: 8) {
                    CartInfoView(sellers: sellersCartData)
                    
                    Button(action: { scope.clearCart() }) {
                        Image("RemoveCart")
                            .padding(16)
                            .background(appColor: .red)
                    }
                    .frame(maxWidth: .infinity, alignment: .trailing)
                }
                .background(appColor: .white)
                
                AppColor.lightGrey.color
                    .frame(height: 1)
            }
            
            VStack(spacing: 8) {
                ForEach(sellersCartData, id: \.self) { seller in
                    let expanded = Binding(get: { expandedItems[seller.sellerCode] == true },
                                           set: { expandedItems[seller.sellerCode] = $0 })
                    
                    SellerCartDataView(seller: seller,
                                       isReadonly: false,
                                       onRemoveSeller: { scope.removeSellerItems(sellerCart: seller) },
                                       onRemoveItem: { scope.removeItem(sellerCart: seller,
                                                                        item: $0)},
                                       expanded: expanded)
                }
            }.scrollView()
            
            if let price = total.value?.formattedPrice {
                Spacer()
                
                VStack(spacing: 20) {
                    if isContinueEnabled.value != true {
                        HStack {
                            Image("DeleteWarning")
                            
                            LocalizedText(localizationKey: "delete_products",
                                          textWeight: .medium,
                                          fontSize: 12)
                                .lineLimit(1)
                                .minimumScaleFactor(0.5)
                        }
                        .padding(8)
                        .background(
                            RoundedRectangle(cornerRadius: 100)
                                .fill(appColor: .red)
                                .opacity(0.08)
                        )
                    }
                    
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
                            
                            MedicoButton(localizedStringKey: "continue",
                                         isEnabled: isContinueEnabled.value == true,
                                         width: 170,
                                         height: 48,
                                         cornerRadius: 24,
                                         fontSize: 15,
                                         fontWeight: .bold) {
                                scope.continueWithCart()
                            }
                        }
                        .padding(.horizontal, 17)
                    }
                }
            }
        }
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
}

struct SellerCartDataView: View {
    private let cornerRadius: CGFloat = 4
    
    let seller: DataSellerCart
    
    let isReadonly: Bool
    
    let onRemoveSeller: (() -> Void)?
    let onRemoveItem: ((DataCartItem) -> Void)?
    
    @Binding var expanded: Bool
    
    var body: some View {
        ZStack {
            AppColor.darkBlue.color
                .opacity(0.04)
            
            VStack {
                ForEach(seller.items, id: \.self) { item in
                    CartItemView(item: item,
                                 isReadonly: isReadonly,
                                 onRemoveItem: onRemoveItem)
                }
            }
        }
        .expandableView(expanded: $expanded) {
            HStack {
                if !isReadonly {
                    RemoveButton(onRemove: { onRemoveSeller?() })
                        .padding(.leading, -4)
                }
                
                SellerNamePaymentMethodView(seller: seller)
            }
            .padding(.vertical, 8)
            .padding(.leading, 16)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

struct SellerNamePaymentMethodView: View {
    let seller: DataSellerCart
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(seller.sellerName)
                .medicoText(textWeight: .semiBold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
                .lineLimit(1)
            
            HStack(spacing: 8) {
                HStack(spacing: 4) {
                    LocalizedText(localizationKey: "type:",
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
                
                AppColor.black.color
                    .opacity(0.27)
                    .frame(width: 1)
                
                Text(seller.total.formattedPrice)
                    .medicoText(textWeight: .bold)
            }
        }
    }
}

struct CartItemView: View {
    let item: DataCartItem
    
    let isReadonly: Bool
    
    let onRemoveItem: ((DataCartItem) -> ())?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let seasonBoyRetailerInfo = self.item.seasonBoyRetailer {
                Text(seasonBoyRetailerInfo.tradeName)
                    .medicoText(textWeight: .medium,
                                fontSize: 12,
                                color: .grey3,
                                multilineTextAlignment: .leading)
                    .lineLimit(1)
            }
            
            HStack {
                getDetailView(titleLocalizationKey: "ptr:", body: item.price.formatted)
                
                Spacer()
                
                getDetailView(titleLocalizationKey: "tot:", body: item.subtotalPrice.formatted)
            }
            .padding(.top, 2)
            
            if let quotedData = item.quotedData {
                VStack(spacing: 4) {
                    AppColor.darkBlue.color
                        .opacity(0.33)
                        .frame(height: 1)
                        .padding(.leading, -12)
                        .padding(.trailing, -8)
                    
                    HStack {
                        if !quotedData.message.isEmpty {
                            SmallAddressView(location: quotedData.message,
                                             fontWeight: .medium,
                                             fontSize: 12,
                                             color: .lightBlue)
                        }
                        
                        Spacer()
                        
                        LocalizedText(localizationKey: quotedData.isAvailable ? "available" : "not_available",
                                      textWeight: .medium,
                                      fontSize: 12,
                                      color: item.itemViewBorderColor)
                    }
                    .frame(height: 20)
                }
            }
        }
        .fixedSize(horizontal: false, vertical: true)
        .modifier(BaseSellerView(initialMode: nil,
                                 header: header,
                                 isReadonly: isReadonly,
                                 initialQuantity: Double(truncating: item.quantity.value ?? 0),
                                 initialFreeQuantity: Double(truncating: item.freeQuantity.value ?? 0),
                                 maxQuantity: .infinity,
                                 onQuantitySelect: { _, _ in }))
        .padding(.vertical, 8)
        .background(appColor: .white)
    }
    
    init(item: DataCartItem,
         isReadonly: Bool = false,
         onRemoveItem: ((DataCartItem) -> ())? = nil) {
        self.item = item
        
        self.isReadonly = isReadonly
        
        self.onRemoveItem = onRemoveItem
    }
    
    private var header: some View {
        HStack(spacing: 8) {
            Text(item.productName)
                .medicoText(textWeight: .bold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
                .lineLimit(1)
            
            AppColor.darkBlue.color
                .opacity(0.33)
                .frame(width: 1)
                .padding(.vertical, 4)
            
            Text("(\(item.manufacturerCode))")
                .medicoText(textWeight: .semiBold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
                .opacity(0.8)
                .lineLimit(1)
            
            Spacer()
            
            if let onRemoveItem = self.onRemoveItem {
                RemoveButton(onRemove: { onRemoveItem(item) })
            }
        }
    }
    
    private var mainBody: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let seasonBoyRetailerInfo = self.item.seasonBoyRetailer {
                Text(seasonBoyRetailerInfo.tradeName)
                    .medicoText(textWeight: .medium,
                                fontSize: 12,
                                color: .grey3,
                                multilineTextAlignment: .leading)
                    .lineLimit(1)
            }
            
            HStack {
                getDetailView(titleLocalizationKey: "ptr:", body: item.price.formatted)
                
                Spacer()
                
                getDetailView(titleLocalizationKey: "tot:", body: item.subtotalPrice.formatted)
            }
            .padding(.top, 2)
            
            if let quotedData = item.quotedData {
                VStack(spacing: 4) {
                    AppColor.darkBlue.color
                        .opacity(0.33)
                        .frame(height: 1)
                        .padding(.leading, -12)
                        .padding(.trailing, -8)
                    
                    HStack {
                        if !quotedData.message.isEmpty {
                            SmallAddressView(location: quotedData.message,
                                             fontWeight: .medium,
                                             fontSize: 12,
                                             color: .lightBlue)
                        }
                        
                        Spacer()
                        
                        LocalizedText(localizationKey: quotedData.isAvailable ? "available" : "not_available",
                                      textWeight: .medium,
                                      fontSize: 12,
                                      color: item.itemViewBorderColor)
                    }
                    .frame(height: 20)
                }
            }
        }
        .fixedSize(horizontal: false, vertical: true)
    }
    
    private func getDetailView(titleLocalizationKey: String,
                               body: String) -> some View {
        HStack(spacing: 4) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          textWeight: .bold,
                          fontSize: 16,
                          color: .greyBlue)
            
            Text(body)
                .medicoText(textWeight: .semiBold,
                            fontSize: 16)
        }
    }
}

private struct RemoveButton: View {
    let onRemove: () -> Void
    
    var body: some View {
        Button(action: { onRemove() }) {
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
}

struct PriceWithFootnoteView: View {
    let price: String
    let fontSize: CGFloat
    
    var body: some View {
        HStack(alignment: .top, spacing: 0) {
            Text(price)
                .medicoText(textWeight: .bold,
                            fontSize: fontSize)
            
            Text("*")
                .medicoText(textWeight: .bold,
                            fontSize: fontSize,
                            color: AppColor.lightBlue)
        }
    }
}

extension DataCartItem {
    var itemViewBorderColor: AppColor {
        switch self.quotedData?.isAvailable {
        case .some(true):
            return .green
            
        case .some(false):
            return .red
            
        default:
            return .darkBlue
        }
    }
    
    var itemViewBorderOpacity: Double {
        self.quotedData != nil ? 1 : 0.12
    }
}

struct CartInfoView: View {
    let sellers: [DataSellerCart]
    
    var body: some View {
        Group {
            let quantity = self.quantity
            
            ElementsNumberView(titleLocalizationKey: "items", number: items)
            ElementsNumberView(titleLocalizationKey: "qty", number: quantity.qty)
            ElementsNumberView(titleLocalizationKey: "free", number: quantity.free)
            ElementsNumberView(titleLocalizationKey: "stockists", number: sellers.count)
        }
    }
    
    private var items: Int {
        sellers.reduce(0) { $0 + $1.items.count }
    }
    
    private var quantity: (qty: Double, free: Double) {
        sellers.reduce((0.0, 0.0)) { result, seller in
            let quantity = seller.items.reduce((0.0, 0.0)) {
                ($0.0 + Double(truncating: $1.quantity.value ?? 0),
                 $0.1 + Double(truncating: $1.freeQuantity.value ?? 0)) }
            
            return (result.0 + quantity.0, result.1 + quantity.1)
        }
    }
    
    private struct ElementsNumberView: View {
        let titleLocalizationKey: String
        let number: String
        
        var body: some View {
            VStack(alignment: .leading, spacing: 6) {
                LocalizedText(localizationKey: titleLocalizationKey,
                              fontSize: 12,
                              multilineTextAlignment: .leading)
                
                Text(number)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 12,
                                multilineTextAlignment: .leading)
            }
            .frame(maxWidth: .infinity)
        }
        
        private init(titleLocalizationKey: String, number: String) {
            self.titleLocalizationKey = titleLocalizationKey
            self.number = number
        }
        
        init(titleLocalizationKey: String, number: Int) {
            self.init(titleLocalizationKey: titleLocalizationKey,
                      number: String(number))
        }
        
        init(titleLocalizationKey: String, number: Double) {
            self.init(titleLocalizationKey: titleLocalizationKey,
                      number: String(format: "%.1f", number))
        }
    }
}
