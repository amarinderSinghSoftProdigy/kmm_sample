//
//  GlobalSearch.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core
import Introspect

struct GlobalSearchScreen: View {
    @EnvironmentObject var scrollData: ListScrollData
    
    let scope: SearchScope
    
    @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var filters: SwiftDataSource<NSArray>
    @ObservedObject var filterSearches: SwiftDataSource<NSDictionary>
    
    @ObservedObject var autoComplete: SwiftDataSource<NSArray>
    
    @ObservedObject var products: SwiftDataSource<NSArray>
    @ObservedObject var productSearch: SwiftDataSource<NSString>
    
    var body: some View {
        guard let isFilterOpened = self.isFilterOpened.value else {
            return AnyView(EmptyView())
        }
        
        let view: AnyView
        let screenName: String
        
        if isFilterOpened == true {
            view = AnyView(self.filtersView)
            screenName = "FiltersView"
        }
        else if let autoComplete = self.autoComplete.value as? [DataAutoComplete],
                !autoComplete.isEmpty,
                let searchInput = self.productSearch.value {
            view = AnyView(AutoCompleteView(input: searchInput as String, autoCompleteData: autoComplete) {
                scope.selectAutoComplete(autoComplete: $0)
            })
            screenName = "autoComplete"
        }
        else {
            view = AnyView(self.productsView)
            screenName = "ProductsView"
        }
        
        return AnyView(
            view
                .hideKeyboardOnTap()
                .navigationBar(withNavigationBarContent: AnyView(searchBarPanel))
                .screenLogger(withScreenName: "GlobalSearchScreen.\(screenName)",
                              withScreenClass: GlobalSearchScreen.self)
        )
    }
    
    init(scope: SearchScope) {
        self.scope = scope
        
        self.isInProgress = SwiftDataSource(dataSource: scope.isInProgress)
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.filters = SwiftDataSource(dataSource: scope.filters)
        self.filterSearches = SwiftDataSource(dataSource: scope.filterSearches)
        
        self.autoComplete = SwiftDataSource(dataSource: scope.autoComplete)
        
        self.products = SwiftDataSource(dataSource: scope.products)
        self.productSearch = SwiftDataSource(dataSource: scope.productSearch)
    }
    
    private var searchBarPanel: some View {
        HStack {
            SearchBar(searchText: productSearch.value,
                      style: .small,
                      showsCancelButton: false,
                      trailingButton: SearchBar.SearchBarButton(button: .filter({ scope.toggleFilter() })),
                      onTextChange: { value in scope.searchProduct(input: value) })
            
            Button(LocalizedStringKey("cancel")) {
                scrollData.clear(list: .globalSearchProducts)
                
                scope.goBack()
            }
            .medicoText(fontSize: 17,
                        color: .blue)
        }
        .padding([.leading, .trailing], 6)
    }
    
    private var filtersView: some View {
        VStack(spacing: 12) {
            HStack {
                Spacer()
                
                LocalizedText(localizationKey: "clear_all",
                              textWeight: .medium,
                              fontSize: 16,
                              color: .textGrey,
                              multilineTextAlignment: .trailing)
                    .onTapGesture {
                        scope.clearFilter(filter: nil)
                    }
            }
            
            if let filters = self.filters.value as? [DataFilter] {
                ForEach(filters, id: \.self.name) { filter in
                    if let filtersSearches = self.filterSearches.value as? [String: String] {

                        let searchOption = SearchOption(text: filtersSearches[filter.queryId],
                                                        onSearch: { scope.searchFilter(filter: filter, input: $0) })
                        
                        FilterView(filter: filter,
                                   searchOption: searchOption,
                                   onSelectFilterOption: { option in scope.selectFilter(filter: filter,
                                                                                        option: option) },
                                   onClearFilter: { scope.clearFilter(filter: filter) })
                    }
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 32)
        .scrollView()
    }
    
    private var productsView: some View {
        let productsCount = self.products.value?.count ?? 0
        
        return AnyView (
            TransparentList(data: self.products,
                            dataType: DataProductSearch.self,
                            listName: .globalSearchProducts,
                            isInProgress: scope.isInProgress,
                            pagination: scope.pagination,
                            onTapGesture: { scope.selectProduct(product: $0) },
                            loadItems: { scope.loadMoreProducts() }) { index, product -> AnyView in
                let topPadding: CGFloat = index == 0 ? 32 : 0
                let bottomPadding: CGFloat = index == productsCount - 1 ? 16 : 0
                
                return AnyView(
                    ProductView(product: product)
                        .padding(.top, topPadding)
                        .padding(.bottom, bottomPadding)
                )
            }
            .padding(.horizontal, 16)
        )
    }
    
    private struct AutoCompleteView: View {
        let input: String
        let autoCompleteData: [DataAutoComplete]
        let onAutoCompleteTap: (DataAutoComplete) -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 0) {
                ForEach(autoCompleteData, id: \.self) { element in
                    Group {
                        HStack {
                            VStack(alignment: .leading, spacing: 8) {
                                element.suggestion.getText(withBoldSubstring: input,
                                                           withFontSize: 15)
                                
                                if !element.details.isEmpty {
                                    Text(element.details)
                                        .medicoText(textWeight: .medium,
                                                    fontSize: 12,
                                                    multilineTextAlignment: .leading)
                                }
                            }
                            
                            Spacer()
                            
                            Image(systemName: "arrow.up.forward")
                                .resizable()
                                .foregroundColor(appColor: .lightBlue)
                                .font(Font.title.weight(.medium))
                                .frame(width: 12, height: 12)
                        }
                        .padding(.vertical, 11)
                        .padding(.horizontal, 24)
                        
                        Divider()
                            .foregroundColor(appColor: .navigationBar)
                    }
                    .background(appColor: .white)
                    .onTapGesture {
                        onAutoCompleteTap(element)
                    }
                }
            }
            .scrollView()
        }
    }
}

// MARK: Filter
private struct FilterView: View {
    let filter: DataFilter
    
    let searchOption: SearchOption?
    
    let onSelectFilterOption: (DataOption) -> ()
    let onClearFilter: () -> ()
    
    var body: some View {
        VStack(spacing: 14) {
            AppColor.lightGrey.color
                .frame(height: 1)
            
            HStack {
                LocalizedText(localizationKey: filter.name,
                              textWeight: .semiBold,
                              fontSize: 16)
                
                Spacer()
                
                LocalizedText(localizationKey: "clear",
                              textWeight: .medium,
                              fontSize: 16,
                              color: .textGrey)
                    .onTapGesture {
                        self.onClearFilter()
                    }
            }
            
            if let searchOption = self.searchOption {
                SearchBar(placeholderLocalizationKey: "search",
                          searchText: searchOption.text as NSString?,
                          style: .small,
                          onTextChange: searchOption.onSearch)
            }
            
            let visibleFilterOptions = filter.options
                .filter { ($0 as? DataOption.StringValue)?.isVisible != false  }
            
            FlexibleView(data: visibleFilterOptions,
                         spacing: 8,
                         alignment: .leading) { option in
                FilterOption(option: option)
                    .onTapGesture {
                        self.onSelectFilterOption(option)
                    }
            }
        }
    }
}

private struct FilterOption: View {
    private let height: CGFloat = 32
    
    let option: DataOption
    
    var body: some View {
        Group {
            let cornerRadius = height / 2
            
            switch option {
            case let stringValueOption as DataOption.StringValue:
                if stringValueOption.isVisible {
                    StringValueOption(option: stringValueOption,
                                      cornerRadius: cornerRadius)
                        .frame(height: height)
                }
                
            case is DataOption.ViewMore:
                ViewMoreOption(cornerRadius: cornerRadius)
                    .frame(height: height)
                
            default:
                EmptyView()
            }
        }
    }
    
    private struct StringValueOption: View {
        let option: DataOption.StringValue
        let cornerRadius: CGFloat
        
        var body: some View {
            ZStack {
                let backgroundColor: AppColor = option.isSelected ? .yellow : .white

                backgroundColor.color
                    .cornerRadius(cornerRadius)

                HStack {
                    if option.isSelected {
                        Image(systemName: "checkmark")
                            .foregroundColor(appColor: .darkBlue)
                    }

                    let textWeight: TextWeight = option.isSelected ? .semiBold : .regular
                    LocalizedText(localizationKey: option.value as String,
                                  textWeight: textWeight)
                }
                .padding(.horizontal, 10)
            }
        }
    }
    
    private struct ViewMoreOption: View {
        let cornerRadius: CGFloat
        
        var body: some View {
            ZStack {
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(AppColor.lightBlue.color,
                            style: .init(lineWidth: 1))
                
                LocalizedText(localizationKey: "view_more",
                              textWeight: .bold,
                              fontSize: 12,
                              color: .lightBlue)
                .padding(.horizontal, 10)
            }
        }
    }
}

private struct SearchOption {
    let text: String?
    let onSearch: (String) -> ()
}

// MARK: Products
private struct ProductView: View {
    private let alignment: HorizontalAlignment = .leading
    private let textAlignment: TextAlignment = .leading
    
    let product: DataProductSearch
    
    var body: some View {
        ZStack(alignment: Alignment(horizontal: alignment, vertical: .center)) {
            let noStockInfoColor = AppColor.placeholderGrey
            let statusColor = product.stockInfo?.statusColor ?? noStockInfoColor
            
            HStack {
                statusColor.color
                    .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                    .frame(width: 5)
                
                Spacer()
            }
            .background(AppColor.white.color.cornerRadius(5))
            
            VStack(alignment: alignment) {
                HStack(alignment: .top, spacing: 17) {
                    ProductImage(medicineId: product.code,
                                 size: .px123)
                        .frame(width: 81, height: 81)
                    
                    let labelColor: AppColor = product.stockInfo != nil ? .darkBlue : noStockInfoColor
                    VStack(alignment: alignment, spacing: 10) {
                        VStack(alignment: alignment, spacing: 5) {
                            Text(product.name)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            color: labelColor,
                                            multilineTextAlignment: textAlignment)
                            
                            if let price = product.formattedPrice {
                                Text(price)
                                    .medicoText(textWeight: .black,
                                                fontSize: 16,
                                                color: labelColor,
                                                multilineTextAlignment: textAlignment)
                            }
                        }
                        .fixedSize(horizontal: false, vertical: true)
                        
                        VStack(alignment: alignment, spacing: 3) {
                            HStack(spacing: 20) {
                                LocalizedText(localizedStringKey: LocalizedStringKey("mrp \(product.formattedMrp)"),
                                              testingIdentifier: "mrp",
                                              fontSize: 12,
                                              color: .grey3,
                                              multilineTextAlignment: textAlignment)
                                
                                if let margin = product.marginPercent {
                                    LocalizedText(localizedStringKey: LocalizedStringKey("margin \(margin)"),
                                                  testingIdentifier: "margin",
                                                  fontSize: 12,
                                                  color: .grey3,
                                                  multilineTextAlignment: .leading)
                                }
                            }
                            
                            LocalizedText(localizedStringKey: LocalizedStringKey("code \(product.code)"),
                                          testingIdentifier: "code",
                                          fontSize: 12,
                                          color: .grey3,
                                          multilineTextAlignment: textAlignment)
                        }
                    }
                }
                
                HStack(alignment: .top, spacing: 10) {
                    Text(product.uomName)
                        .medicoText(color: product.stockInfo != nil ? .lightBlue : noStockInfoColor,
                                    multilineTextAlignment: textAlignment)
                    
                    Spacer()
                    
                    if let stockInfo = product.stockInfo {
                        LocalizedText(localizationKey: stockInfo.formattedStatus,
                                      textWeight: .bold,
                                      fontSize: 12,
                                      color: stockInfo.statusColor,
                                      multilineTextAlignment: .leading)
                            .padding(.top, 2)
                    }
                }
            }
            .padding(11)
        }
        .opacity(product.stockInfo != nil ? 1 : 0.6)
    }
}
