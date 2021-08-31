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
    private var stockInfo: DataStockInfo?
    private var itemsSelectable = false
    private var searchTitleLocalizationKey = ""
    private var scopeSpecificView: AnyView?
    
    let scope: BuyProductScope<DataWithTradeName>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16)  {
            VStack(alignment: .leading, spacing: 0) {
                ProductInfo(product: scope.product)
                
                self.scopeSpecificView
            }
            .background(appColor: .white)
            
            Group {
                if let scope = (self.scope as? BuyProductScope<DataSellerInfo>) as? BuyProductScopeChooseQuote {
                    ChooseQuoteView(scope: scope)
                }
                else {
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
            }
        }
        .screenLogger(withScreenName: "BuyProduct",
                      withScreenClass: BuyProductScreen.self)
    }
    
    init(scope: BuyProductScope<DataWithTradeName>) {
        self.scope = scope
        
        if let scope = (self.scope as? BuyProductScope<DataSellerInfo>) as? BuyProductScopeChooseStockist {
            itemsSelectable = scope.isSeasonBoy
            searchTitleLocalizationKey = "choose_seller"
        }
        else if let scope = (self.scope as? BuyProductScope<DataSeasonBoyRetailer>) as? BuyProductScopeChooseRetailer {
            searchTitleLocalizationKey = "choose_retailer"
            stockInfo = scope.sellerInfo?.stockInfo
            
            self.scopeSpecificView = AnyView(
                Group {
                    if let sellerInfo = scope.sellerInfo {
                        AppColor.darkBlue.color
                            .opacity(0.12)
                            .frame(height: 1)
                        
                        StockistInfoView(seller: sellerInfo)
                    }
                }
            )
        }
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
                        
                        if let units = product.standardUnit,
                           !units.isEmpty {
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
                
                Spacer()
            }
            .padding(16)
            .frame(maxWidth: .infinity)
        }
    }
    
    private struct StockistInfoView: View {
        let seller: DataSellerInfo
        
        var body: some View {
            VStack(alignment: .leading, spacing: 8) {
                Text(seller.tradeName)
                    .medicoText(textWeight: .medium,
                                fontSize: 15,
                                multilineTextAlignment: .leading)
                
                HStack(spacing: 8) {
                    if let mrp = seller.priceInfo?.mrp.formattedPrice {
                        DetailView(titleLocalizationKey: "mrp:",
                                   bodyText: mrp)
                        
                        Divider()
                            .frame(height: 13)
                    }
                    
                    if let stockInfo = seller.stockInfo {
                        DetailView(titleLocalizationKey: "stocks:",
                                   bodyText: String(stockInfo.availableQty))
                        
                        Divider()
                            .frame(height: 13)
                        
                        DetailView(titleLocalizationKey: "expiry:",
                                   bodyText: stockInfo.expiry.formattedDate,
                                   bodyColor: .hex(stockInfo.expiry.color))
                    }
                    
                    Spacer()
                    
                    Text(seller.priceInfo?.price.formattedPrice ?? "")
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
        
        let onQuantityIncrease: (DataTapMode, DataWithTradeName) -> ()
        let onQuantityDecrease: (DataTapMode, DataWithTradeName) -> ()
        
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
                                       isSelectable: itemsSelectable,
                                       quantity: quantities[$0] ?? 0,
                                       onQuantityIncrease: onQuantityIncrease,
                                       onQuantityDecrease: onQuantityDecrease,
                                       onInfoSelect: onInfoSelect)
                        }
                    }
                    else if let retailerInfo = self.items.value as? [DataSeasonBoyRetailer],
                            let quantities = self.quantities.value as? [DataSeasonBoyRetailer: Int] {
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
            
            let onQuantityIncrease: (DataTapMode, DataWithTradeName) -> ()
            let onQuantityDecrease: (DataTapMode, DataWithTradeName) -> ()
            
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
                                    
                                    if let priceInfo = info.priceInfo {
                                        HStack {
                                            Text(priceInfo.price.formattedPrice)
                                                .medicoText(textWeight: .bold,
                                                            fontSize: 18,
                                                            multilineTextAlignment: .leading)
                                            
                                            Spacer()
                                            
                                            DetailView(titleLocalizationKey: "mrp:",
                                                       bodyText: priceInfo.mrp.formattedPrice)
                                        }
                                        
                                        HStack {
                                            Text(product.code)
                                                .medicoText(color: .grey3,
                                                            multilineTextAlignment: .leading)
                                            
                                            Spacer()
                                            
                                            DetailView(titleLocalizationKey: "margin:",
                                                       bodyText: priceInfo.marginPercent)
                                        }
                                    }
                                    
                                    if let stockInfo = info.stockInfo {
                                        HStack {
                                            let expiryColor = AppColor.hex(stockInfo.expiry.color)
                                            
                                            DetailView(titleLocalizationKey: "expiry:",
                                                       bodyText: stockInfo.expiry.formattedDate,
                                                       bodyColor: expiryColor)
                                                .padding(.horizontal, 6)
                                                .padding(.vertical, 2)
                                                .background(
                                                    expiryColor.color
                                                        .opacity(0.12)
                                                        .cornerRadius(4)
                                                )
                                            
                                            Spacer()
                                            
                                            DetailView(titleLocalizationKey: "stocks:",
                                                       bodyText: String(stockInfo.availableQty))
                                        }
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
                                MedicoButton(localizedStringKey: "select",
                                             height: 32) {
                                    onInfoSelect(self.info)
                                }
                            }
                            else {
                                HStack {
                                    NumberPicker(quantity: quantity,
                                                 maxQuantity: Int(info.stockInfo?.availableQty ?? .max),
                                                 onQuantityIncrease: { onQuantityIncrease($0, self.info) },
                                                 onQuantityDecrease: { onQuantityDecrease($0, self.info) },
                                                 longPressEnabled: true)
                                    
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
                    
                    info.stockInfo?.statusColor.color
                        .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                        .frame(width: 5)
                }
                .background(AppColor.white.color.cornerRadius(5))
            }
        }
    }
    
    private struct RetailerView: View {
        let info: DataSeasonBoyRetailer
        let stockInfo: DataStockInfo?
        
        let quantity: Int
        
        let onQuantityIncrease: (DataTapMode, DataWithTradeName) -> ()
        let onQuantityDecrease: (DataTapMode, DataWithTradeName) -> ()
        
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
                            
                            
                            SmallAddressView(location: info.geoData.fullAddress())
                        }
                    }
                    .fixedSize(horizontal: false, vertical: true)
                    .padding(.horizontal, 20)
                    
                    AppColor.darkBlue.color
                        .opacity(0.12)
                        .frame(height: 1)
                    
                    HStack {
                        NumberPicker(quantity: quantity,
                                     maxQuantity: Int(stockInfo?.availableQty ?? .max),
                                     onQuantityIncrease: { onQuantityIncrease($0, self.info) },
                                     onQuantityDecrease: { onQuantityDecrease($0, self.info) },
                                     longPressEnabled: true)
                        
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
                
                if let stockInfo = self.stockInfo {
                    stockInfo.statusColor.color
                        .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                        .frame(width: 5)
                }
            }
            .background(AppColor.white.color.cornerRadius(5))
        }
    }
    
    private struct DetailView: View {
        let titleLocalizationKey: String
        let bodyText: String
        
        let bodyColor: AppColor
        
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
             bodyColor: AppColor = .lightBlue) {
            self.titleLocalizationKey = titleLocalizationKey
            self.bodyText = bodyText
            
            self.bodyColor = bodyColor
        }
    }
    
    private struct ChooseQuoteView: View {
        let scope: BuyProductScopeChooseQuote
        
        @ObservedObject var selectedOption: SwiftDataSource<BuyProductScopeChooseQuote.Option>
        
        @ObservedObject var availableStockists: SwiftDataSource<NSArray>
        @ObservedObject var selectedStockist: SwiftDataSource<DataSellerInfo>
        
        @ObservedObject var quantities: SwiftDataSource<NSDictionary>
        
        var body: some View {
            VStack(spacing: 16) {
                let quantities = self.quantities.value as? [DataSellerInfo: Int]
                
                getQuoteOptionView(localizationKey: "quote_existing_stockist",
                                   isSelected: self.selectedOption.value == .existingStockist,
                                   quantity: self.selectedStockist.value == nil ? 0 : (quantities?[self.selectedStockist.value!] ?? 0),
                                   maxQuantity: .max,
                                   needsSelectedStockist: true,
                                   onQuantityIncrease: {
                                      if let selectedStockist = self.selectedStockist.value {
                                          scope.inc(mode: $0,
                                                    item: selectedStockist)
                                      }
                                   },
                                   onQuantityDecrease: {
                                      if let selectedStockist = self.selectedStockist.value {
                                          scope.dec(mode: $0,
                                                    item: selectedStockist)
                                      }
                                   },
                                   onButtonTap: {
                                      if let selectedStockist = self.selectedStockist.value {
                                          scope.select(item: selectedStockist)
                                      }
                                   }) {
                    scope.toggleOption(option: .existingStockist)
                }
                
                getQuoteOptionView(localizationKey: "quote_any_stockist",
                                   isSelected: self.selectedOption.value == .anyone,
                                   quantity: quantities?[DataSellerInfo.Anyone().anyone] ?? 0,
                                   maxQuantity: .max,
                                   needsSelectedStockist: false,
                                   onQuantityIncrease: { scope.inc(mode: $0,
                                                                   item: DataSellerInfo.Anyone().anyone) },
                                   onQuantityDecrease: { scope.dec(mode: $0,
                                                                   item: DataSellerInfo.Anyone().anyone) },
                                   onButtonTap: { _ = scope.selectAnyone() }) {
                    scope.toggleOption(option: .anyone)
                }
            }
            .padding(.horizontal, 16)
            .scrollView()
        }
        
        init(scope: BuyProductScopeChooseQuote) {
            self.scope = scope
            
            self.selectedOption = SwiftDataSource(dataSource: scope.selectedOption)
            
            self.availableStockists = SwiftDataSource(dataSource: scope.items)
            self.selectedStockist = SwiftDataSource(dataSource: scope.chosenSeller)
            
            self.quantities = SwiftDataSource(dataSource: scope.quantities)
        }
        
        private func getQuoteOptionView(localizationKey: String,
                                        isSelected: Bool,
                                        quantity: Int,
                                        maxQuantity: Int,
                                        needsSelectedStockist: Bool,
                                        onQuantityIncrease: @escaping (_ tapMode: DataTapMode) -> (),
                                        onQuantityDecrease: @escaping (_ tapMode: DataTapMode) -> (),
                                        onButtonTap: @escaping () -> (),
                                        onToggle: @escaping () -> ()) -> some View {
            let horizontalPadding: CGFloat = 20
        
            return AnyView(
                VStack(alignment: .leading, spacing: 20)  {
                    HStack(spacing: 16) {
                        Circle()
                            .foregroundColor(appColor: isSelected ? .lightBlue : .white)
                            .frame(width: 15, height: 15)
                            .padding(5)
                            .background(
                                Circle()
                                    .stroke(lineWidth: 1)
                                    .foregroundColor(appColor: .lightBlue)
                                    .opacity(isSelected ? 1 : 0.25)
                            )
                            .onTapGesture {
                                onToggle()
                            }
                        
                        LocalizedText(localizationKey: localizationKey,
                                      textWeight: isSelected ? .bold : .medium,
                                      fontSize: 16,
                                      color: .black,
                                      multilineTextAlignment: .leading)
                    }
                    .padding(.horizontal, horizontalPadding)
                    
                    if isSelected {
                        AppColor.darkBlue.color
                            .opacity(0.12)
                            .frame(height: 1)
                        
                        VStack(alignment: .leading, spacing: 30) {
                            if needsSelectedStockist,
                               let availableStockists = self.availableStockists.value as? [DataSellerInfo] {
                                PickerSelector(placeholder: "select_stockists",
                                               chosenElement: self.selectedStockist.value?.tradeName,
                                               data: availableStockists.map { $0.tradeName },
                                               optionsHeight: 40,
                                               backgroundColor: .primary,
                                               chevronColor: .lightBlue) { tradeName in
                                    if let seller = availableStockists.first(where: { $0.tradeName == tradeName }) {
                                        self.scope.chooseSeller(sellerInfo: seller)
                                    }
                                }
                            }
                            
                            HStack {
                                if !scope.isSeasonBoy {
                                    NumberPicker(quantity: quantity,
                                                 maxQuantity: maxQuantity,
                                                 onQuantityIncrease: onQuantityIncrease,
                                                 onQuantityDecrease: onQuantityDecrease,
                                                 longPressEnabled: true)
                                    
                                    Spacer()
                                
                                    MedicoButton(localizedStringKey: "add_to_cart",
                                                 isEnabled: quantity > 0 && (!needsSelectedStockist || self.selectedStockist.value != nil),
                                                 width: 120,
                                                 height: 48,
                                                 fontSize: 15,
                                                 fontWeight: .bold) {
                                        onButtonTap()
                                    }
                                }
                                else {
                                    MedicoButton(localizedStringKey: "select",
                                                 isEnabled: !needsSelectedStockist || self.selectedStockist.value != nil,
                                                 height: 48) {
                                        onButtonTap()
                                    }
                                }
                            }
                        }
                        .padding(.top, 10)
                        .padding(.horizontal, horizontalPadding)
                    }
                }
                .padding(.vertical, 20)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .foregroundColor(appColor: .white)
                )
            )
        }
    }
}
