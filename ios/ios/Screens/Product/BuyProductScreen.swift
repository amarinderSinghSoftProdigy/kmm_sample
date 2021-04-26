//
//  BuyProductScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct BuyProductScreen: View {
    let scope: BuyProductScope
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16)  {
            ProductInfo(product: scope.product)
            
            ChooseSellerView(product: scope.product,
                             sellersFilter: SwiftDataSource(dataSource: scope.sellersFilter),
                             sellersInfo: SwiftDataSource(dataSource: scope.sellersInfo),
                             quantities: SwiftDataSource(dataSource: scope.quantities),
                             onQuantityIncrease: scope.inc,
                             onQuantityDecrease: scope.dec,
                             onSellerInfoSelect: { scope.addToCart(sellerInfo: $0) },
                             onSellerFilter: { scope.filterSellers(filter: $0) })
        }
        .screenLogger(withScreenName: "BuyProduct",
                      withScreenClass: BuyProductScreen.self)
    }
    
    private struct ProductInfo: View {
        let product: DataProductSearch
        
        var body: some View {
            HStack(spacing: 16) {
                ProductImage(medicineId: product.code,
                             size: .px123)
                    .frame(width: 71, height: 71)
                
                VStack(alignment: .leading, spacing: 7) {
                    Text(product.name)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                    
                    HStack(spacing: 8) {
                        Text(product.code)
                            .medicoText(color: .grey3,
                                        multilineTextAlignment: .leading)
                        
                        if let units = product.standardUnit {
                            Divider()
                                .frame(height: 13)
                            
                            HStack(spacing: 4) {
                                LocalizedText(localizationKey: "units:",
                                              multilineTextAlignment: .leading)
                                
                                Text(units)
                                    .medicoText(textWeight: .bold,
                                                color: .lightBlue,
                                                multilineTextAlignment: .leading)
                            }
                        }
                    }
                    
                    Text(product.uomName)
                        .medicoText(color: .lightBlue,
                                    multilineTextAlignment: .leading)
                }
            }
            .padding(16)
            .frame(maxWidth: .infinity)
            .background(appColor: .white)
        }
    }
    
    private struct ChooseSellerView: View {
        let product: DataProductSearch
        
        @State private var showsSearchBar = false
        
        @ObservedObject var sellersFilter: SwiftDataSource<NSString>
        
        @ObservedObject var sellersInfo: SwiftDataSource<NSArray>
        @ObservedObject var quantities: SwiftDataSource<NSDictionary>
        
        let onQuantityIncrease: (DataSellerInfo) -> ()
        let onQuantityDecrease: (DataSellerInfo) -> ()
        
        let onSellerInfoSelect: (DataSellerInfo) -> ()
        let onSellerFilter: (String) -> ()
        
        var body: some View {
            guard let sellersInfo = self.sellersInfo.value as? [DataSellerInfo] else {
                return AnyView(EmptyView())
            }
            
            return AnyView(
                VStack(alignment: .leading, spacing: 16) {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            LocalizedText(localizationKey: "choose_seller",
                                          textWeight: .semiBold,
                                          fontSize: 16,
                                          multilineTextAlignment: .leading)
                            
                            Spacer()
                            
                            Button(action: { self.showsSearchBar.toggle() }) {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(appColor: .darkBlue)
                                    .padding(7)
                                    .background(
                                        Circle()
                                            .foregroundColor(appColor: .darkBlue)
                                            .opacity(self.showsSearchBar ? 0.08 : 0)
                                    )
                            }
                        }
                            
                        if showsSearchBar {
                            SearchBar(searchText: sellersFilter.value,
                                      style: .small,
                                      showsCancelButton: false,
                                      leadingButton: nil,
                                      onTextChange: { value, _ in onSellerFilter(value) })
                        }
                    }
                    
                    let quantities = self.quantities.value as? [DataSellerInfo: Int]
                    
                    VStack(spacing: 12)  {
                        ForEach(sellersInfo, id: \.self) {
                            SellerView(product: product,
                                       info: $0,
                                       quantity: quantities?[$0] ?? 0,
                                       onQuantityIncrease: onQuantityIncrease,
                                       onQuantityDecrease: onQuantityDecrease,
                                       onSellerInfoSelect: onSellerInfoSelect)
                        }
                    }
                    .scrollView()
                }
                .padding(.horizontal, 16)
            )
        }
        
        private struct SellerView: View {
            let product: DataProductSearch
            let info: DataSellerInfo
            
            let quantity: Int
            
            let onQuantityIncrease: (DataSellerInfo) -> ()
            let onQuantityDecrease: (DataSellerInfo) -> ()
            
            let onSellerInfoSelect: (DataSellerInfo) -> ()
            
            var body: some View {
                ZStack(alignment: .leading) {
                    VStack(alignment: .leading, spacing: 8) {
                        Group {
                            HStack(spacing: 17) {
                                UserNameImage(username: info.tradeName)
                                    .frame(width: 65, height: 65)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(info.tradeName)
                                        .medicoText(textWeight: .semiBold,
                                                    fontSize: 16,
                                                    multilineTextAlignment: .leading)
                                    
                                    HStack {
                                        Text(info.priceInfo.price.formattedPrice)
                                            .medicoText(textWeight: .bold,
                                                        fontSize: 18,
                                                        multilineTextAlignment: .leading)
                                        
                                        Spacer()
                                        
                                        getDetailView(withTitleLocalizationKey: "mrp:",
                                                      withBody: info.priceInfo.mrp.formattedPrice)
                                    }
                                    
                                    HStack {
                                        Text(product.code)
                                            .medicoText(color: .grey3,
                                                        multilineTextAlignment: .leading)
                                        
                                        Spacer()
                                        
                                        getDetailView(withTitleLocalizationKey: "margin:",
                                                      withBody: info.priceInfo.marginPercent)
                                    }
                                    
                                    HStack {
                                        let expiryColor = Color(hex: info.stockInfo.expiry.color)
                                        
                                        getDetailView(withTitleLocalizationKey: "expiry:",
                                                      withBody: info.stockInfo.expiry.formattedDate,
                                                      withBodyColor: expiryColor)
                                            .padding(.horizontal, 6)
                                            .padding(.vertical, 2)
                                            .background(
                                                expiryColor
                                                    .opacity(0.12)
                                                    .cornerRadius(4)
                                            )
                                        
                                        Spacer()
                                        
                                        getDetailView(withTitleLocalizationKey: "stocks:",
                                                      withBody: String(info.stockInfo.availableQty))
                                    }
                                }
                            }
                            .fixedSize(horizontal: false, vertical: true)
                            
                            HStack {
                                SmallAddressView(location: info.geoData.fullAddress())
                                
                                Spacer()
                                
                                Text(info.geoData.formattedDistance)
                                    .medicoText(textWeight: .semiBold,
                                                color: .lightBlue,
                                                multilineTextAlignment: .trailing)
                            }
                        }
                        .padding(.horizontal, 20)
                        
                        AppColor.darkBlue.color
                            .opacity(0.12)
                            .frame(height: 1)
                        
                        HStack {
                            NumberPicker(quantity: quantity,
                                         onQuantityIncrease: { onQuantityIncrease(self.info) },
                                         onQuantityDecrease: { onQuantityDecrease(self.info) })
                            
                            Spacer()
                            
                            MedicoButton(localizedStringKey: "add_to_cart",
                                         isEnabled: quantity > 0,
                                         width: 120,
                                         height: 32,
                                         fontSize: 14,
                                         fontWeight: .bold) {
                                onSellerInfoSelect(self.info)
                            }
                        }
                        .padding(.horizontal, 20)
                    }
                    .padding(.vertical, 8)
                    
                    info.stockInfo.statusColor.color
                        .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                        .frame(width: 5)
                }
                .background(AppColor.white.color.cornerRadius(5))
            }
            
            private func getDetailView(withTitleLocalizationKey titleLocalizationKey: String,
                                       withBody body: String,
                                       withBodyColor bodyColor: Color = AppColor.lightBlue.color) -> some View {
                return AnyView(
                    HStack(spacing: 4) {
                        LocalizedText(localizationKey: titleLocalizationKey,
                                      color: .grey3,
                                      multilineTextAlignment: .leading)
                        
                        Text(body)
                            .medicoText(textWeight: .bold,
                                        color: bodyColor,
                                        multilineTextAlignment: .leading)
                    }
                )
            }
        }
    }
}
