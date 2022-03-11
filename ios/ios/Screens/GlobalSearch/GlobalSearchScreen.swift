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
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var autoComplete: SwiftDataSource<NSArray>

    @ObservedObject var productSearch: SwiftDataSource<NSString>
    
    var body: some View {
        let view: AnyView
        let screenName: String

        if self.isFilterOpened.value == true {
            view = AnyView(
                FiltersSection(scope: self.scope)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 32)
            )
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
            view = AnyView(
                ProductsView(scope: self.scope)
            )
            screenName = "ProductsView"
        }

        return AnyView(
            view
                .hideKeyboardOnTap()
                .screenLogger(withScreenName: "GlobalSearchScreen.\(screenName)",
                              withScreenClass: GlobalSearchScreen.self)
        )
    }
    
    init(scope: SearchScope) {
        self.scope = scope
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        
        self.autoComplete = SwiftDataSource(dataSource: scope.autoComplete)
        
        self.productSearch = SwiftDataSource(dataSource: scope.productSearch)
    }
    
    private struct ProductsView: View {
        let scope: BaseSearchScope
        
        @ObservedObject var products: SwiftDataSource<NSArray>
        
        var body: some View {
            let productsCount = self.products.value?.count ?? 0
            
            TransparentList(data: self.products,
                            dataType: DataProductSearch.self,
                            listName: .globalSearchProducts,
                            pagination: scope.pagination,
                            onTapGesture: { scope.selectProduct(product: $0) },
                            loadItems: { scope.loadMoreProducts() }) { index, product -> AnyView in
                let topPadding: CGFloat = index == 0 ? 32 : 0
                let bottomPadding: CGFloat = index == productsCount - 1 ? 16 : 0

                return AnyView(
                    ProductSearchView(product: product) {
                        scope.buy(product: product)
                    }
                    .padding(.top, topPadding)
                    .padding(.bottom, bottomPadding)
                )
            }
            .padding(.horizontal, 16)
        }
        
        init(scope: BaseSearchScope) {
            self.scope = scope
            
            self.products = .init(dataSource: scope.products)
        }
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
                          onTextChange: { value, _ in searchOption.onSearch(value) })
            }
            
            let visibleFilterOptions = filter.options
                .filter { ($0 as? DataOption.StringValue)?.isVisible != false  }
            
            FlexibleView(data: visibleFilterOptions,
                         spacing: 8,
                         alignment: .leading) { option in
                Chip(option: option)
                    .onTapGesture {
                        self.onSelectFilterOption(option)
                    }
            }
        }
    }
}

private struct FilterChip: View {
    let localizationKey: String
    let onRemove: () -> Void
    
    var body: some View {
        HStack(spacing: 7) {
            LocalizedText(localizationKey: localizationKey,
                          textWeight: .medium,
                          fontSize: 12,
                          color: .white)
            
            Image(systemName: "xmark")
                .foregroundColor(appColor: .white)
        }
        .padding(10)
        .background(
            RoundedRectangle(cornerRadius: 100)
                .fill(appColor: .lightBlue)
        )
        .onTapGesture {
            onRemove()
        }
    }
}

private struct Chip: View {
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
struct ProductSearchView: View {
    private let alignment: HorizontalAlignment = .leading
    private let textAlignment: TextAlignment = .leading
    
    let product: DataProductSearch
    let handleProductPurchase: () -> Void
    
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
                    
                    VStack(alignment: alignment, spacing: 6) {
                        VStack(alignment: alignment, spacing: 5) {
                            Text(product.name)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            multilineTextAlignment: textAlignment)
                            
                            if let price = product.formattedPrice {
                                Text(price)
                                    .medicoText(textWeight: .black,
                                                fontSize: 16,
                                                multilineTextAlignment: textAlignment)
                            }
                        }
                        .fixedSize(horizontal: false, vertical: true)
                        
                        VStack(alignment: alignment, spacing: 3) {
                            HStack(spacing: 20) {
                                Text(product.code)
                                    .medicoText(fontSize: 12,
                                                color: .grey3,
                                                multilineTextAlignment: textAlignment)
                                
                                Spacer()
                                
                                DetailView(titleLocalizationKey: "mrp:", bodyText: product.formattedMrp)
                            }
                            
                            HStack(spacing: 20) {
                                if let productStatus = product.stockInfo {
                                    Text(productStatus.formattedStatus)
                                        .medicoText(textWeight: .bold,
                                                    fontSize: 12,
                                                    color: productStatus.statusColor,
                                                    multilineTextAlignment: textAlignment)
                                }
                                
                                Spacer()
                                
                                if let margin = product.marginPercent {
                                    DetailView(titleLocalizationKey: "margin:", bodyText: margin)
                                }
                            }
                        }
                    }
                }
                
                HStack(spacing: 10) {
                    VStack(alignment: .leading) {
                        AppColor.darkBlue.color
                            .opacity(0.12)
                            .frame(width: 155, height: 1)
                        
                        Text(product.uomName)
                            .medicoText(color: .lightBlue,
                                        multilineTextAlignment: textAlignment)
                    }
                    
                    Spacer()
                    
                    itemButton
                }
            }
            .padding(11)
        }
    }
    
    private var itemButton: some View {
        let localizedStringKey: String
        let isEnabled: Bool
        let backgroundOpacity: Double
        let action: () -> Void
            
        switch product.buyingOption {
        case .some(.buy):
            localizedStringKey = "buy"
            isEnabled = true
            backgroundOpacity = 1
            action = { handleProductPurchase() }
            
        case .some(.quote):
            localizedStringKey = "get_quote"
            isEnabled = true
            backgroundOpacity = 0.12
            action = { handleProductPurchase() }
        
        default:
            localizedStringKey = "buy"
            isEnabled = false
            backgroundOpacity = 1
            action = { }
        }
        
        return MedicoButton(localizedStringKey: localizedStringKey,
                            isEnabled: isEnabled,
                            width: 97,
                            height: 38,
                            cornerRadius: 2,
                            fontSize: 14,
                            buttonColorOpacity: backgroundOpacity,
                            action: action)
            .buttonStyle(BorderlessButtonStyle())
            .strokeBorder(isEnabled ? .yellow : .grey1,
                          fill: .yellow,
                          fillOpacity: backgroundOpacity,
                          lineWidth: 2,
                          cornerRadius: 2)
    }
    
    private struct DetailView: View {
        let titleLocalizationKey: String
        let bodyText: String
        
        let bodyColor: AppColor
        
        var body: some View {
            HStack(spacing: 4) {
                LocalizedText(localizationKey: titleLocalizationKey,
                              fontSize: 12,
                              color: .grey3,
                              multilineTextAlignment: .leading)
                
                Text(bodyText)
                    .medicoText(textWeight: .bold,
                                fontSize: 12,
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
}

struct FiltersSection: View {
    let scope: BaseSearchScope
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var filters: SwiftDataSource<NSArray>
    @ObservedObject var filterSearches: SwiftDataSource<NSDictionary>
    @ObservedObject var activeFiltersIds: SwiftDataSource<NSArray>
    
    var body: some View {
        VStack(spacing: 25) {
            VStack(spacing: 12) {
                HStack {
                    LocalizedText(localizationKey: "filters",
                                  textWeight: .semiBold,
                                  fontSize: 20)
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
                
                if let activeFiltersIds = self.activeFiltersIds.value as? [String],
                   !activeFiltersIds.isEmpty {
                    AppColor.lightGrey.color
                        .frame(height: 1)
                
                    FlexibleView(data: activeFiltersIds,
                                 spacing: 8,
                                 alignment: .leading) { filterId in
                        FilterChip(localizationKey: filterId) {
                            let filter = scope.getFilterNameById(id: filterId)
                            scope.clearFilter(filter: filter)
                        }
                    }
                }
                
                SortSection(selectedSortOption: .init(dataSource: scope.selectedSortOption),
                            sortOptions: .init(dataSource: scope.sortOptions)) {
                    scope.selectSortOption(option: $0)
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
            .scrollView()
            
            AppColor.darkBlue.color
                .opacity(0.2)
                .frame(height: 1)
            
            HStack(spacing: 25) {
                MedicoButton(localizedStringKey: "clear",
                             isEnabled: true,
                             fontColor: .lightBlue,
                             buttonColor: .clear) {
                    scope.clearFilter(filter: nil)
                }
                .strokeBorder(.lightBlue,
                              fill: .clear)
                
                MedicoButton(localizedStringKey: "apply",
                             isEnabled: true) {
                    scope.toggleFilter()
                }
            }
        }
    }
    
    init(scope: BaseSearchScope) {
        self.scope = scope
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.filters = SwiftDataSource(dataSource: scope.filters)
        self.filterSearches = SwiftDataSource(dataSource: scope.filterSearches)
        self.activeFiltersIds = SwiftDataSource(dataSource: scope.activeFilterIds)
    }
    
    private struct SortSection: View {
        @ObservedObject var selectedSortOption: SwiftDataSource<DataSortOption>
        @ObservedObject var sortOptions: SwiftDataSource<NSArray>
        
        let onOptionSelect: (DataSortOption?) -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 15) {
                AppColor.lightGrey.color
                    .frame(height: 1)
                
                HStack {
                    LocalizedText(localizationKey: "sort_by",
                                  textWeight: .semiBold,
                                  fontSize: 16)
                    
                    Spacer()
                    
                    LocalizedText(localizationKey: "clear",
                                  textWeight: .medium,
                                  fontSize: 16,
                                  color: .grey3)
                        .onTapGesture {
                            onOptionSelect(nil)
                        }
                }
                
                if let sortOptions = self.sortOptions.value as? [DataSortOption] {
                    FlexibleView(data: sortOptions,
                                 spacing: 8,
                                 alignment: .leading) { option in
                        Chip(option: .StringValue(value: option.name,
                                                  isSelected: option == selectedSortOption.value,
                                                  isVisible: true,
                                                  id: ""))
                            .onTapGesture {
                                self.onOptionSelect(option)
                            }
                    }
                }
            }
        }
    }
}
