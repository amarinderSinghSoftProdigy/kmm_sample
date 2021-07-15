//
//  StoreScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 6.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct StoresScreen: View {
    let scope: StoresScope
    
    var body: some View {
        switch self.scope {
        
        case let scope as StoresScope.All:
            StoresListScreen(scope: scope)
            
        case let scope as StoresScope.StorePreview:
            StorePreviewScreen(scope: scope)
            
        default:
            EmptyView()
            
        }
    }
}

private struct StoresListScreen: View {
    let scope: StoresScope.All
    
    @ObservedObject var searchText: SwiftDataSource<NSString>
    @ObservedObject var stores: SwiftDataSource<NSArray>
    
    var body: some View {
        VStack(spacing: 16) {
            SearchBar(placeholderLocalizationKey: "store",
                      searchText: searchText.value,
                      leadingButton: SearchBar.SearchBarButton(emptyTextButton: .custom(AnyView(Image("Store"))),
                                                               enteredTextButton: .smallMagnifyingGlass),
                      trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                enteredTextButton: .clear),
                      onTextChange: { newValue, _ in scope.search(value: newValue) })
            
            
            if let stores = self.stores.value,
               stores.count > 0 {
                TransparentList(data: self.stores,
                                dataType: DataStore.self,
                                listName: .stores,
                                pagination: scope.pagination,
                                onTapGesture: { scope.selectItem(item: $0) },
                                loadItems: { scope.loadItems() }) { _, element in
                    StoreItemView(store: element)
                }
                .hideKeyboardOnTap()
            }
            else {
                EmptyListView(imageName: "EmptyStores",
                              titleLocalizationKey: "empty_stores",
                              handleHomeTap: { })
            }
        }
        .keyboardResponder()
        .padding(.horizontal, 16)
        .padding(.vertical, 32)
        .screenLogger(withScreenName: "StoresListScreen",
                      withScreenClass: StoresListScreen.self)
    }
    
    init(scope: StoresScope.All) {
        self.scope = scope
        
        self.searchText = SwiftDataSource(dataSource: scope.searchText)
        self.stores = SwiftDataSource(dataSource: scope.items)
    }
    
    private struct StoreItemView: View {
        let store: DataStore
        
        var body: some View {
            ZStack {
                AppColor.white.color
                    .cornerRadius(5)
                
                VStack(spacing: 6) {
                    HStack(spacing: 10) {
                        Text(store.tradeName)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 16,
                                        multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        LocalizedText(localizationKey: store.status.serverValue,
                                      textWeight: .medium,
                                      fontSize: 15,
                                      color: store.status == .subscribed ? .lightBlue : .yellow)
                    }
                    
                    HStack(spacing: 10) {
                        SmallAddressView(location: store.fullAddress())
                        
                        Spacer()
                        
                        Text(store.formattedDistance)
                            .medicoText(fontSize: 12,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 7)
            }
        }
    }
}

private struct StorePreviewScreen: View {
    let scope: StoresScope.StorePreview
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var productSearch: SwiftDataSource<NSString>
    @ObservedObject var products: SwiftDataSource<NSArray>
    
    @ObservedObject var activeFilterIds: SwiftDataSource<NSArray>
    
    private var isFilterApplied: Bool {
        if let appliedFiltersNumber = activeFilterIds.value?.count {
            return appliedFiltersNumber > 0
        }
        
        return false
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            StoreInfo(store: scope.store)
            
            SearchBar(placeholderLocalizationKey: "search",
                      searchText: productSearch.value,
                      style: .small,
                      trailingButton: SearchBar.SearchBarButton(button: .filter(isHighlighted: isFilterApplied,
                                                                                { scope.toggleFilter() })),
                      onTextChange: { newValue, _ in scope.searchProduct(input: newValue,
                                                                         withAutoComplete: false) })
            
            self.productsSearchView
        }
        .padding(.top, 32)
        .padding(.horizontal, 16)
    }
    
    init(scope: StoresScope.StorePreview) {
        self.scope = scope
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        
        self.productSearch = SwiftDataSource(dataSource: scope.productSearch)
        self.products = SwiftDataSource(dataSource: scope.products)
        
        self.activeFilterIds = SwiftDataSource(dataSource: scope.activeFilterIds)
    }
    
    private var productsSearchView: some View {
        let view: AnyView
        let screenName: String

        if self.isFilterOpened.value == true {
            view = AnyView(FiltersSection(scope: self.scope))
            screenName = "FiltersView"
        }
        else {
            view = AnyView(self.productsList)
            screenName = "ProductsView"
        }

        return AnyView(
            view
                .hideKeyboardOnTap()
                .screenLogger(withScreenName: "StoreScreen.\(screenName)",
                              withScreenClass: StorePreviewScreen.self)
        )
    }
    
    private var productsList: some View {
        let productsCount = self.products.value?.count ?? 0
        
        return AnyView(
            TransparentList(data: products,
                            dataType: DataProductSearch.self,
                            listName: .storeProducts,
                            pagination: scope.pagination,
                            onTapGesture: { scope.selectProduct(product: $0) },
                            loadItems: { scope.loadMoreProducts() }) { index, element -> AnyView in
                let bottomPadding: CGFloat = index == productsCount - 1 ? 16 : 0
                
                return AnyView(
                    ProductSearchView(product: element) {
                        scope.buy(product: element)
                    }
                    .padding(.bottom, bottomPadding)
                )
            }
        )
    }
    
    private struct StoreInfo: View {
        let store: DataStore
        
        var body: some View {
            VStack(alignment: .leading, spacing: 10) {
                Text(store.tradeName)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 20,
                                multilineTextAlignment: .leading)
                
                VStack(alignment: .leading, spacing: 5) {
                    SmallAddressView(location: store.fullAddress())
                    
                    HStack(spacing: 3) {
                        LocalizedText(localizationKey: "gstin_number",
                                      multilineTextAlignment: .leading)
                        
                        Text(store.gstin)
                            .medicoText(textWeight: .medium,
                                        multilineTextAlignment: .leading)
                    }
                }
            }
        }
    }
}
