//
//  BuyProductScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI
import Combine

struct BuyProductScreen: View {
    private var stockInfo: DataStockInfo?
    private var itemsSelectable = false
    private var searchTitleLocalizationKey = ""
    private var scopeSpecificView: AnyView?
    private var previewStockist: ((DataSellerInfo) -> Void)?
    
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
                                     onViewTap: previewStockist,
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
            previewStockist = { scope.previewStockist(info: $0) }
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
    
    private func previewStockist(_ stockist: DataSellerInfo) {
        previewStockist?(stockist)
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
        let onViewTap: (DataSellerInfo) -> Void
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
                                       onInfoSelect: onInfoSelect,
                                       onViewTap: onViewTap)
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
            let onViewTap: (DataSellerInfo) -> Void
            
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
                            DateExpiryView(dateExpiry: stockInfo.expiry)
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
                                   onQuantitySelect: onQuantitySelect,
                                   onViewTap: { onViewTap(info) })
                )
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
                if let quantities = self.quantities.value as? [DataSellerInfo: KotlinPair<KotlinDouble, KotlinDouble>] {
                    QuoteView(localizationKey: "quote_existing_stockist",
                              isSelected: self.selectedOption.value == .existingStockist,
                              needsSelectedStockist: true,
                              isSeasonBoy: scope.isSeasonBoy,
                              availableStockists: availableStockists.value as? [DataSellerInfo],
                              chosenSeller: selectedStockist.value?.tradeName,
                              onSellerPickerSelect: { scope.chooseSeller(sellerInfo: $0) },
                              quantity: Double(truncating: selectedStockist.value == nil ? 0 : (quantities[selectedStockist.value!]?.first ?? 0)),
                              freeQuantity: Double(truncating: selectedStockist.value == nil ? 0 : (quantities[selectedStockist.value!]?.second ?? 0)),
                              onQuantitySelect: handleQuantitySelect,
                              onToggle: { scope.toggleOption(option: .existingStockist) })
                    
                    QuoteView(localizationKey: "quote_any_stockist",
                              isSelected: selectedOption.value == .anyone,
                              needsSelectedStockist: false,
                              isSeasonBoy: scope.isSeasonBoy,
                              availableStockists: availableStockists.value as? [DataSellerInfo],
                              chosenSeller: selectedStockist.value?.tradeName,
                              onSellerPickerSelect: { scope.chooseSeller(sellerInfo: $0) },
                              quantity: Double(truncating: quantities[DataSellerInfo.Anyone().anyone]?.first ?? 0),
                              freeQuantity: Double(truncating: quantities[DataSellerInfo.Anyone().anyone]?.second ?? 0),
                              onQuantitySelect: handleAnyStockistQuantitySelect,
                              onToggle: { scope.toggleOption(option: .anyone) })
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
        
        private func handleQuantitySelect(quantity: Double?, freeQuantity: Double?) {
            guard let selectedStockist = self.selectedStockist.value else { return }
            
            if let quantity = quantity,
               let freeQuantity = freeQuantity {
                scope.saveQuantitiesAndSelect(item: selectedStockist, qty: quantity, freeQty: freeQuantity)
            }
            else {
                scope.select(item: selectedStockist)
            }
        }
        
        private func handleAnyStockistQuantitySelect(quantity: Double?, freeQuantity: Double?) {
            if let quantity = quantity,
               let freeQuantity = freeQuantity {
                scope.saveQuantities(item: DataSellerInfo.Anyone().anyone, qty: quantity, freeQty: freeQuantity)
                scope.selectAnyone()
            }
            else {
                scope.selectAnyone()
            }
        }
        
        private struct QuoteView: View {
            private let horizontalPadding: CGFloat = 20
            
            let localizationKey: String
            
            let isSelected: Bool
            let needsSelectedStockist: Bool
            let isSeasonBoy: Bool
            
            let availableStockists: [DataSellerInfo]?
            let chosenSeller: String?
            let onSellerPickerSelect: (DataSellerInfo) -> Void
            
            let quantity: Double
            let freeQuantity: Double
            
            let onQuantitySelect: (Double?, Double?) -> ()
            let onToggle: () -> Void
            
            var body: some View {
                Group {
                    if isSelected {
                        Group {
                            if needsSelectedStockist,
                               let availableStockists = self.availableStockists {
                                PickerSelector(placeholder: "select_stockists",
                                               chosenElement: chosenSeller,
                                               data: availableStockists.map { $0.tradeName },
                                               optionsHeight: 40,
                                               backgroundColor: .primary,
                                               chevronColor: .lightBlue) { tradeName in
                                    if let seller = availableStockists.first(where: { $0.tradeName == tradeName }) {
                                        onSellerPickerSelect(seller)
                                    }
                                }
                                .padding(.bottom, 20)
                            }
                            else {
                                EmptyView()
                            }
                        }
                        .modifier(BaseSellerView(initialMode: isSeasonBoy ? .select : nil,
                                                 header: header,
                                                 horizontalPadding: horizontalPadding,
                                                 buttonsHeight: 48,
                                                 showsDivider: false,
                                                 addActionEnabled: !needsSelectedStockist || chosenSeller != nil,
                                                 initialQuantity: quantity,
                                                 initialFreeQuantity: freeQuantity,
                                                 maxQuantity: .infinity,
                                                 onQuantitySelect: onQuantitySelect))
                    }
                    else {
                        header
                            .padding(.horizontal, horizontalPadding)
                    }
                }
                .padding(.vertical, 12)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .foregroundColor(appColor: .white)
                )
            }
            
            private var header: some View {
                VStack(alignment: .leading, spacing: 15) {
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
                    .fixedSize(horizontal: false, vertical: true)
                    
                    if isSelected {
                        VStack(alignment: .leading, spacing: 15) {
                            AppColor.darkBlue.color
                                .opacity(0.12)
                                .frame(height: 1)
                                .padding(.horizontal, -horizontalPadding)
                                .padding(.bottom, 15)
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
}

struct BaseSellerView<Header: View>: ViewModifier {
    @State private var mode: Mode
    
    let header: Header
    
    private let horizontalPadding: CGFloat
    private let standaloneButtonsHeight: CGFloat
    private let buttonsHeight: CGFloat = 38
    
    private let initialQuantity: Double
    private let initialFreeQuantity: Double
    private let maxQuantity: Double
    
    @State private var quantity: Double
    @State private var freeQuantity: Double
    @State private var quantitiesCorrect: Bool = true
    
    private let onQuantitySelect: (Double?, Double?) -> Void
    private let onViewTap: (() -> Void)?
    
    private let addActionEnabled: Bool
    private let showsDivider: Bool
    private let isReadonly: Bool
    private let isQuoted: Bool
    
    func body(content: Content) -> some View {
        VStack {
            Group {
                header
                
                if mode == .confirmQuantity {
                    QuantityInput(quantity: $quantity,
                                  freeQuantity: $freeQuantity,
                                  maxQuantity: maxQuantity,
                                  quantitiesCorrect: $quantitiesCorrect)
                        .padding(.bottom, 12)
                }
                else {
                    content
                }
            }
            .padding(.horizontal, horizontalPadding)
            
            if showsDivider {
                AppColor.darkBlue.color
                    .opacity(0.12)
                    .frame(height: 1)
            }
            
            bottomPanel
                .padding(.horizontal, horizontalPadding)
        }
        .onReceive(Just(initialQuantity)) {
            if self.mode != .confirmQuantity {
                self.quantity = $0
                
                updateMode()
            }
        }
        .onReceive(Just(initialFreeQuantity)) {
            if self.mode != .confirmQuantity {
                self.freeQuantity = $0
                
                updateMode()
            }
        }
        .padding(.vertical, 8)
        .background(appColor: .white)
        .onTapGesture {
            if self.mode != .confirmQuantity {
                onViewTap?()
            }
            else {
                content.hideKeyboard()
            }
        }
    }
    
    init(initialMode: Mode?,
         header: Header,
         horizontalPadding: CGFloat = 16,
         buttonsHeight: CGFloat = 38,
         showsDivider: Bool = true,
         addActionEnabled: Bool = true,
         isReadonly: Bool = false,
         isQuoted: Bool = false,
         initialQuantity: Double,
         initialFreeQuantity: Double,
         maxQuantity: Double,
         onQuantitySelect: @escaping (Double?, Double?) -> Void,
         onViewTap: (() -> Void)? = nil) {
        let initialMode = initialMode ?? (initialQuantity > 0 || initialFreeQuantity > 0 ? .update : .addToCart)
        self._mode = State(initialValue: initialMode)
        
        self.header = header
        self.horizontalPadding = horizontalPadding
        self.standaloneButtonsHeight = buttonsHeight
        
        self.showsDivider = showsDivider
        self.addActionEnabled = addActionEnabled
        self.isReadonly = isReadonly
        self.isQuoted = isQuoted
        
        self.initialQuantity = initialQuantity
        self.initialFreeQuantity = initialFreeQuantity
        
        self.maxQuantity = maxQuantity
        
        self._quantity = State(initialValue: initialQuantity)
        self._freeQuantity = State(initialValue: initialFreeQuantity)
        
        self.onQuantitySelect = onQuantitySelect
        self.onViewTap = onViewTap
    }
    
    private var bottomPanel: some View {
        Group {
            switch mode {
            case .addToCart, .select:
                MedicoButton(localizedStringKey: mode == .select ? "select" : "add_to_cart",
                             isEnabled: addActionEnabled,
                             height: standaloneButtonsHeight,
                             cornerRadius: standaloneButtonsHeight / 2,
                             fontSize: 14,
                             fontWeight: .bold) {
                    if mode == .addToCart {
                        mode = .confirmQuantity
                    }
                    else {
                        onQuantitySelect(nil, nil)
                    }
                }
                .disabled(isReadonly)
                
            case .update:
                HStack {
                    QuantityView(quantity: quantity, freeQuantity: freeQuantity)
                    
                    if isQuoted {
                        LocalizedText(localizationKey: "quoted",
                                      textWeight: .semiBold,
                                      fontSize: 12)
                            .padding(.horizontal, 4)
                            .padding(.vertical, 2)
                            .strokeBorder(.darkBlue,
                                          borderOpacity: 0.3,
                                          fill: .darkBlue,
                                          fillOpacity: 0.08,
                                          cornerRadius: 4)
                    }
                    
                    Spacer()
                    
                    if !isReadonly {
                        MedicoButton(localizedStringKey: "update",
                                     width: 100,
                                     height: buttonsHeight,
                                     cornerRadius: buttonsHeight / 2,
                                     fontSize: 14,
                                     fontWeight: .bold,
                                     fontColor: .white,
                                     buttonColor: .lightBlue) {
                            mode = .confirmQuantity
                        }
                    }
                }
                
            case .confirmQuantity:
                HStack {
                    MedicoButton(localizedStringKey: "cancel",
                                 width: 112,
                                 height: buttonsHeight,
                                 cornerRadius: buttonsHeight / 2,
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
                                 isEnabled: quantitiesCorrect,
                                 width: 112,
                                 height: buttonsHeight,
                                 cornerRadius: buttonsHeight / 2,
                                 fontSize: 14,
                                 fontWeight: .bold) {
                        mode = quantity > 0 || freeQuantity > 0 ? .update : .addToCart
                        
                        onQuantitySelect(quantity, freeQuantity)
                    }
                }
            }
        }
    }
    
    private func updateMode() {
        guard mode != .select else { return }
        
        self.mode = initialQuantity > 0 || initialFreeQuantity > 0 ? .update : .addToCart
    }
    
    enum Mode {
        case select
        case addToCart
        case update
        case confirmQuantity
    }
}

struct DateExpiryView: View {
    let dateExpiry: DataExpiry
    
    var body: some View {
        let expiryColor = AppColor.hex(dateExpiry.color)
        
        HStack(spacing: 4) {
            LocalizedText(localizationKey: "expiry:",
                          color: .grey3,
                          multilineTextAlignment: .leading)
            
            Text(dateExpiry.formattedDate)
                .medicoText(textWeight: .bold,
                            color: expiryColor,
                            multilineTextAlignment: .leading)
        }
        .padding(.horizontal, 6)
        .padding(.vertical, 2)
        .background(
            expiryColor.color
                .opacity(0.12)
                .cornerRadius(4)
        )
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

struct QuantityView: View {
    let quantity: Double
    let freeQuantity: Double
    
    var body: some View {
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
    }
}
