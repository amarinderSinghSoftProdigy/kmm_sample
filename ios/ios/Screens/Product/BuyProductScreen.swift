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
                                     onQuantitySelect: selectQuantity,
                                     onInfoSelect: { scope.select(item: $0) },
                                     onSellerFilter: { scope.filterItems(filter: $0) })
                }
            }
        }
        .textFieldsModifiers()
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
    
    private func selectQuantity(tradeName: DataWithTradeName, quantity: Double?, freeQuanitity: Double?) {
        if let quantity = quantity,
           let freeQuanitity = freeQuanitity {
            scope.saveQuantitiesAndSelect(item: tradeName, qty: quantity, freeQty: freeQuanitity)
        }
        else {
            scope.select(item: tradeName)
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
        
        let onQuantitySelect: (DataWithTradeName, Double?, Double?) -> Void
        
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
                .padding(.horizontal, 16)
                
                VStack(spacing: 12)  {
                    if let sellersInfo = self.items.value as? [DataSellerInfo],
                       let quantities = self.quantities.value as? [DataSellerInfo: KotlinPair<KotlinDouble, KotlinDouble>] {
                        ForEach(sellersInfo, id: \.self) { sellerInfo in
                            SellerView(product: product,
                                       info: sellerInfo,
                                       isSelectable: itemsSelectable,
                                       quantity: Double(truncating: quantities[sellerInfo]?.first ?? 0),
                                       freeQuantity: Double(truncating: quantities[sellerInfo]?.second ?? 0),
                                       onQuantitySelect: { onQuantitySelect(sellerInfo, $0, $1) },
                                       onInfoSelect: onInfoSelect)
                        }
                    }
                    else if let retailersInfo = self.items.value as? [DataSeasonBoyRetailer],
                            let quantities = self.quantities.value as? [DataSeasonBoyRetailer: KotlinPair<KotlinDouble, KotlinDouble>] {
                        ForEach(retailersInfo, id: \.self) { retailerInfo in
                            RetailerView(info: retailerInfo,
                                         stockInfo: stockInfo,
                                         quantity: Double(truncating: quantities[retailerInfo]?.first ?? 0),
                                         freeQuantity: Double(truncating: quantities[retailerInfo]?.second ?? 0),
                                         onQuantitySelect: { onQuantitySelect(retailerInfo, $0, $1) },
                                         onInfoSelect: onInfoSelect)
                        }
                    }
                    
                }
                .scrollView()
            }
        }
        
        private struct SellerView: View {
            let product: DataProductSearch
            let info: DataSellerInfo
            let isSelectable: Bool
            
            let quantity: Double
            let freeQuantity: Double
            
            let onQuantitySelect: (Double?, Double?) -> ()
            
            let onInfoSelect: (DataSellerInfo) -> ()
            
            var body: some View {
                VStack(spacing: 8) {
                    HStack {
                        if let priceInfo = info.priceInfo {
                            DetailView(titleLocalizationKey: "ptr:",
                                       bodyText: priceInfo.price.formattedPrice)
                        }
                        
                        Spacer()
                        
                        if let stockInfo = info.stockInfo {
                            DetailView(titleLocalizationKey: "stocks:",
                                       bodyText: String(stockInfo.availableQty))
                        }
                    }
                    
                    HStack {
                        if let stockInfo = info.stockInfo {
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
                        }
                        
                        Spacer()
                        
                        DistanceView(distance: info.geoData.formattedDistance)
                    }
                }
                .modifier(
                    BaseSellerView(initialMode: isSelectable ? .select : nil,
                                   header: header,
                                   addActionEnabled: info.stockInfo?.status != .outOfStock,
                                   initialQuantity: quantity,
                                   initialFreeQuantity: freeQuantity,
                                   maxQuantity: Double(info.stockInfo?.availableQty ?? .max),
                                   onQuantitySelect: onQuantitySelect)
                )
                .padding(.vertical, 8)
                .background(appColor: .white)
            }
            
            private var header: some View {
                HStack(spacing: 6) {
                    info.stockInfo?.statusColor.color
                        .cornerRadius(4)
                        .frame(width: 10, height: 10)
                    
                    Text(info.tradeName)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 15,
                                    multilineTextAlignment: .leading)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
    
    private struct RetailerView: View {
        let info: DataSeasonBoyRetailer
        let stockInfo: DataStockInfo?
        
        let quantity: Double
        let freeQuantity: Double
        
        let onQuantitySelect: (Double?, Double?) -> ()
        
        let onInfoSelect: (DataSeasonBoyRetailer) -> ()
        
        var body: some View {
            HStack {
                SmallAddressView(location: info.geoData.fullAddress())
                
                Spacer()
                
                DistanceView(distance: info.geoData.formattedDistance)
            }
            .modifier(BaseSellerView(initialMode: nil,
                                     header: header,
                                     initialQuantity: quantity,
                                     initialFreeQuantity: freeQuantity,
                                     maxQuantity: Double(stockInfo?.availableQty ?? .max),
                                     onQuantitySelect: onQuantitySelect))
            .padding(.vertical, 8)
            .background(appColor: .white)
        }
        
        private var header: some View {
            HStack(spacing: 6) {
                stockInfo?.statusColor.color
                    .cornerRadius(4)
                    .frame(width: 10, height: 10)
                
                Text(info.tradeName)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 15,
                                multilineTextAlignment: .leading)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
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
                                   onQuantitySelect: { _ in },
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
                                   onQuantitySelect: { _ in },
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
                                        onQuantitySelect: @escaping (_ tapMode: DataTapMode) -> (),
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
//                                    NumberPicker(quantity: quantity,
//                                                 maxQuantity: maxQuantity,
//                                                 onQuantityIncrease: onQuantityIncrease,
//                                                 onQuantityDecrease: onQuantityDecrease,
//                                                 longPressEnabled: true)
                                    
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

private struct BaseSellerView<Header: View>: ViewModifier {
    @State private var mode: Mode
    
    let header: Header
    
    private let horizontalPadding: CGFloat
    
    private let initialQuantity: Double
    private let initialFreeQuantity: Double
    private let maxQuantity: Double
    
    @State private var quantity: Double
    @State private var freeQuantity: Double
    
    private let onQuantitySelect: (Double?, Double?) -> Void
    
    private let addActionEnabled: Bool
    
    func body(content: Content) -> some View {
        VStack {
            Group {
                header
                
                if mode == .confirmQuantity {
                    QuantityInput(quantity: $quantity,
                                  freeQuantity: $freeQuantity,
                                  maxQuantity: maxQuantity)
                        .padding(.bottom, 12)
                }
                else {
                    content
                }
            }
            .padding(.horizontal, horizontalPadding)
            
            AppColor.darkBlue.color
                .opacity(0.12)
                .frame(height: 1)
            
            bottomPanel
                .padding(.horizontal, horizontalPadding)
        }
    }
    
    init(initialMode: Mode?,
         header: Header,
         horizontalPadding: CGFloat = 16,
         addActionEnabled: Bool = true,
         initialQuantity: Double,
         initialFreeQuantity: Double,
         maxQuantity: Double,
         onQuantitySelect: @escaping (Double?, Double?) -> Void) {
        let initialMode = initialMode ?? (initialQuantity > 0 || initialFreeQuantity > 0 ? .update : .addToCart)
        self._mode = State(initialValue: initialMode)
        
        self.header = header
        self.horizontalPadding = horizontalPadding
        
        self.addActionEnabled = addActionEnabled
        
        self.initialQuantity = initialQuantity
        self.initialFreeQuantity = initialFreeQuantity
        
        self.maxQuantity = maxQuantity
        
        self._quantity = State(initialValue: initialQuantity)
        self._freeQuantity = State(initialValue: initialFreeQuantity)
        
        self.onQuantitySelect = onQuantitySelect
    }
    
    private var bottomPanel: some View {
        Group {
            switch mode {
            case .addToCart, .select:
                MedicoButton(localizedStringKey: mode == .select ? "select" : "add_to_cart",
                             isEnabled: addActionEnabled,
                             height: 32,
                             cornerRadius: 16,
                             fontSize: 14,
                             fontWeight: .bold) {
                    if mode == .addToCart {
                        mode = .confirmQuantity
                    }
                    else {
                        onQuantitySelect(nil, nil)
                    }
                }
                
            case .update:
                HStack {
                    HStack(spacing: 6) {
                        LocalizedText(localizationKey: "QTY",
                                      textWeight: .semiBold,
                                      fontSize: 12)
                            .opacity(0.6)
                        
                        Text(String(format: "%.1f", quantity))
                            .medicoText(textWeight: .bold,
                                        fontSize: 16)
                        
                        Text(String(format: "+%.1f", freeQuantity))
                            .medicoText(textWeight: .bold,
                                        fontSize: 12,
                                        color: .lightBlue)
                            .padding(.horizontal, 4)
                            .padding(.vertical, 2)
                            .strokeBorder(.lightBlue,
                                          fill: .lightBlue,
                                          fillOpacity: 0.08,
                                          cornerRadius: 4)
                    }
                    
                    Spacer()
                    
                    MedicoButton(localizedStringKey: "update",
                                 width: 100,
                                 height: 32,
                                 cornerRadius: 16,
                                 fontSize: 14,
                                 fontWeight: .bold,
                                 fontColor: .white,
                                 buttonColor: .lightBlue) {
                        mode = .confirmQuantity
                    }
                }
                
            case .confirmQuantity:
                HStack {
                    MedicoButton(localizedStringKey: "cancel",
                                 width: 112,
                                 height: 32,
                                 cornerRadius: 16,
                                 fontSize: 14,
                                 fontWeight: .bold,
                                 buttonColor: .darkBlue,
                                 buttonColorOpacity: 0.08) {
                        mode = initialQuantity > 0 || initialFreeQuantity > 0 ? .update : .addToCart
                        
                        quantity = initialQuantity
                        freeQuantity = initialFreeQuantity
                    }
                    
                    Spacer()
                    
                    MedicoButton(localizedStringKey: "confirm",
                                 isEnabled: (quantity + freeQuantity).truncatingRemainder(dividingBy: 1) == 0,
                                 width: 112,
                                 height: 32,
                                 cornerRadius: 16,
                                 fontSize: 14,
                                 fontWeight: .bold) {
                        mode = quantity > 0 || freeQuantity > 0 ? .update : .addToCart
                        
                        onQuantitySelect(quantity, freeQuantity)
                    }
                }
            }
        }
    }
    
    enum Mode {
        case select
        case addToCart
        case update
        case confirmQuantity
    }
}

struct DistanceView: View {
    let distance: String
    
    var body: some View {
        HStack(spacing: 2) {
            Image("MapPin")
                .resizable()
                .renderingMode(.template)
                .aspectRatio(contentMode: .fit)
                .foregroundColor(appColor: .lightBlue)
                .frame(width: 11, height: 11)
            
            Text(distance)
                .medicoText(textWeight: .semiBold,
                            color: .lightBlue,
                            multilineTextAlignment: .trailing)
        }
        .padding(.horizontal, 6)
        .padding(.vertical, 2)
        .background(
            AppColor.greyBlue.color
                .opacity(0.12)
                .cornerRadius(4)
        )
    }
}
