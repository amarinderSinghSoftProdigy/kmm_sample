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
    @State private var stockInfo: DataStockInfo?
    
    let scope: BuyProductScope<DataWithTradeName>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16)  {
            var itemsSelectable = false
            var searchTitleLocalizationKey = ""
            
            VStack(alignment: .leading, spacing: 10) {
                ProductInfo(product: scope.product)
                
                Group { () -> AnyView in
                    if let scope = (self.scope as? BuyProductScope<DataSellerInfo>) as? BuyProductScopeChooseStockist {
                        itemsSelectable = scope.isSeasonBoy
                        searchTitleLocalizationKey = "choose_seller"
                    }
                    else if let scope = (self.scope as? BuyProductScope<DataSeasonBoyRetailer>) as? BuyProductScopeChooseRetailer {
                        searchTitleLocalizationKey = "choose_retailer"
                        stockInfo = scope.sellerInfo.stockInfo
                        
                        return AnyView(
                            Group {
                                AppColor.darkBlue.color
                                    .opacity(0.12)
                                    .frame(height: 1)
                                
                                StockistInfoView(seller: scope.sellerInfo)
                            }
                        )
                    }
                    return AnyView(EmptyView())
                }
            }
            .background(appColor: .white)
            
            ChooseSellerView(product: scope.product,
                             itemsSelectable: itemsSelectable,
                             stockInfo: stockInfo,
                             searchTitleLocalizationKey: searchTitleLocalizationKey,
                             filter: SwiftDataSource(dataSource: scope.itemsFilter),
                             items: SwiftDataSource(dataSource: scope.items),
                             quantities: SwiftDataSource(dataSource: scope.quantities),
                             onQuantityIncrease: scope.inc,
                             onQuantityDecrease: scope.dec,
                             onInfoSelect: { scope.select(item: $0) },
                             onSellerFilter: { scope.filterItems(filter: $0) })
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
        }
    }
    
    private struct StockistInfoView: View {
        let seller: DataSellerInfo
        
        var body: some View {
            VStack(alignment: .leading) {
                Text(seller.tradeName)
                    .medicoText(textWeight: .medium,
                                fontSize: 15,
                                multilineTextAlignment: .leading)
                
                HStack(spacing: 8) {
                    DetailView(titleLocalizationKey: "mrp:",
                               bodyText: seller.priceInfo.mrp.formattedPrice)
                    
                    Divider()
                    
                    DetailView(titleLocalizationKey: "stocks:",
                               bodyText: String(seller.stockInfo.availableQty))
                    
                    Divider()
                    
                    DetailView(titleLocalizationKey: "expiry:",
                               bodyText: seller.stockInfo.expiry.formattedDate,
                               bodyColor: Color(hex: seller.stockInfo.expiry.color))
                    
                    Spacer()
                    
                    Text(seller.priceInfo.price.formattedPrice)
                        .medicoText(textWeight: .bold,
                                    fontSize: 16,
                                    multilineTextAlignment: .leading)
                }
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 16)
        }
    }
    
    private struct ChooseSellerView: View {
        let product: DataProductSearch
        
        let itemsSelectable: Bool
        let stockInfo: DataStockInfo?
        let searchTitleLocalizationKey: String
        
        @State private var showsSearchBar = false
        
        @ObservedObject var filter: SwiftDataSource<NSString>
        
        @ObservedObject var items: SwiftDataSource<NSArray>
        @ObservedObject var quantities: SwiftDataSource<NSDictionary>
        
        let onQuantityIncrease: (DataWithTradeName) -> ()
        let onQuantityDecrease: (DataWithTradeName) -> ()
        
        let onInfoSelect: (DataWithTradeName) -> ()
        let onSellerFilter: (String) -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        LocalizedText(localizationKey: searchTitleLocalizationKey,
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
                        SearchBar(searchText: filter.value,
                                  style: .small,
                                  showsCancelButton: false,
                                  leadingButton: nil,
                                  onTextChange: { value, _ in onSellerFilter(value) })
                    }
                }
                
                
                
                VStack(spacing: 12)  {
                    if let sellersInfo = self.items.value as? [DataSellerInfo],
                       let quantities = self.quantities.value as? [DataSellerInfo: Int] {
                        ForEach(sellersInfo, id: \.self) {
                            SellerView(product: product,
                                       info: $0,
                                       isSelectable: false,
                                       quantity: quantities[$0] ?? 0,
                                       onQuantityIncrease: onQuantityIncrease,
                                       onQuantityDecrease: onQuantityDecrease,
                                       onInfoSelect: onInfoSelect)
                        }
                    }
                    else if let retailerInfo = self.items.value as? [DataSeasonBoyRetailer],
                            let quantities = self.quantities.value as? [DataSeasonBoyRetailer: Int],
                            let stockInfo = self.stockInfo {
                        ForEach(retailerInfo, id: \.self) {
                            RetailerView(info: $0,
                                         stockInfo: stockInfo,
                                         quantity: quantities[$0] ?? 0,
                                         onQuantityIncrease: onQuantityIncrease,
                                         onQuantityDecrease: onQuantityDecrease,
                                         onInfoSelect: onInfoSelect)
                        }
                    }
                    
                }
                .scrollView()
            }
            .padding(.horizontal, 16)
        }
        
        private struct SellerView: View {
            let product: DataProductSearch
            let info: DataSellerInfo
            let isSelectable: Bool
            
            let quantity: Int
            
            let onQuantityIncrease: (DataSellerInfo) -> ()
            let onQuantityDecrease: (DataSellerInfo) -> ()
            
            let onInfoSelect: (DataSellerInfo) -> ()
            
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
                                        
                                        DetailView(titleLocalizationKey: "mrp:",
                                                   bodyText: info.priceInfo.mrp.formattedPrice)
                                    }
                                    
                                    HStack {
                                        Text(product.code)
                                            .medicoText(color: .grey3,
                                                        multilineTextAlignment: .leading)
                                        
                                        Spacer()
                                        
                                        DetailView(titleLocalizationKey: "margin:",
                                                   bodyText: info.priceInfo.marginPercent)
                                    }
                                    
                                    HStack {
                                        let expiryColor = Color(hex: info.stockInfo.expiry.color)
                                        
                                        DetailView(titleLocalizationKey: "expiry:",
                                                   bodyText: info.stockInfo.expiry.formattedDate,
                                                   bodyColor: expiryColor)
                                            .padding(.horizontal, 6)
                                            .padding(.vertical, 2)
                                            .background(
                                                expiryColor
                                                    .opacity(0.12)
                                                    .cornerRadius(4)
                                            )
                                        
                                        Spacer()
                                        
                                        DetailView(titleLocalizationKey: "stocks:",
                                                   bodyText: String(info.stockInfo.availableQty))
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
                        
                        Group {
                            if isSelectable {
                                MedicoButton(localizedStringKey: "select") {
                                    onInfoSelect(self.info)
                                }
                            }
                            else {
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
                                        onInfoSelect(self.info)
                                    }
                                }
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
        }
    }
    
    private struct RetailerView: View {
        let info: DataSeasonBoyRetailer
        let stockInfo: DataStockInfo
        
        let quantity: Int
        
        let onQuantityIncrease: (DataSeasonBoyRetailer) -> ()
        let onQuantityDecrease: (DataSeasonBoyRetailer) -> ()
        
        let onInfoSelect: (DataSeasonBoyRetailer) -> ()
        
        var body: some View {
            ZStack(alignment: .leading) {
                VStack(alignment: .leading, spacing: 8) {
                    HStack(spacing: 17) {
                        UserNameImage(username: info.tradeName)
                            .frame(width: 65, height: 65)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text(info.tradeName)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            multilineTextAlignment: .leading)
                            
                            
                            SmallAddressView(location: info.fullAddress())
                        }
                    }
                    .fixedSize(horizontal: false, vertical: true)
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
                            onInfoSelect(self.info)
                        }
                    }
                    .padding(.horizontal, 20)
                }
                .padding(.vertical, 8)
                
                stockInfo.statusColor.color
                    .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                    .frame(width: 5)
            }
            .background(AppColor.white.color.cornerRadius(5))
        }
    }
    
    private struct DetailView: View {
        let titleLocalizationKey: String
        let bodyText: String
        
        let bodyColor: Color
        
        var body: some View {
            HStack(spacing: 4) {
                LocalizedText(localizationKey: titleLocalizationKey,
                              color: .grey3,
                              multilineTextAlignment: .leading)
                
                Text(bodyText)
                    .medicoText(textWeight: .bold,
                                color: bodyColor,
                                multilineTextAlignment: .leading)
            }
        }
        
        init(titleLocalizationKey: String,
             bodyText: String,
             bodyColor: Color = AppColor.lightBlue.color) {
            self.titleLocalizationKey = titleLocalizationKey
            self.bodyText = bodyText
            
            self.bodyColor = bodyColor
        }
    }
    
}
