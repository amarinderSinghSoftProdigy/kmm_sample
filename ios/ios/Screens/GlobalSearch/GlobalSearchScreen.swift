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

extension SearchScope {
    override var navigationBarTintColor: UIColor? { return nil }
}

struct GlobalSearchScreen: View {
    let scope: SearchScope
    
    @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var filters: SwiftDataSource<NSArray>
    @ObservedObject var manufucturerSearch: SwiftDataSource<NSString>
    
    @ObservedObject var products: SwiftDataSource<NSArray>
    @ObservedObject var productSearch: SwiftDataSource<NSString>
    
    @State private var productsListTableView: UITableView?
    
    @State private var scrollElementIndex: Int = 0
    @State private var elementToScrollTo: Int?
    @State private var scrollPostion: UITableView.ScrollPosition = .none
    
    var body: some View {
        guard let isFilterOpened = self.isFilterOpened.value else {
            return AnyView(EmptyView())
        }
        
        let view: AnyView
        
        if isFilterOpened == true {
            view = AnyView(self.filtersView)
        }
        else {
            view = AnyView(self.productsView)
        }
        
        return AnyView(
            view
                .keyboardResponder()
                .navigationBar(withNavigationBarContent: AnyView(searchBarPanel))
        )
    }
    
    init(scope: SearchScope) {
        self.scope = scope
        
        self.isInProgress = SwiftDataSource(dataSource: scope.isInProgress)
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.filters = SwiftDataSource(dataSource: scope.filters)
        self.manufucturerSearch = SwiftDataSource(dataSource: scope.manufacturerSearch)
        
        self.products = SwiftDataSource(dataSource: scope.products)
        self.productSearch = SwiftDataSource(dataSource: scope.productSearch)
    }
    
    private var searchBarPanel: some View {
        HStack {
            SearchBar(searchText: productSearch.value,
                      trailingButton: .filter({ scope.toggleFilter() }),
                      onTextChange: { value in scope.searchProduct(input: value) })
            
            Button(LocalizedStringKey("cancel")) {
                scope.goBack()
            }
            .medicoText(fontSize: 17,
                        color: .blue)
        }
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
                    let searchOption: SearchOption? = filter.queryName == DataFilter.Ids().MANUFACTURER_ID ?
                        SearchOption(text: manufucturerSearch.value,
                                     onSearch: { value in scope.searchManufacturer(input: value) }) : nil
                    
                    FilterView(filter: filter,
                               searchOption: searchOption,
                               onSelectFilterOption: { option in scope.selectFilter(filter: filter,
                                                                                    option: option) },
                               onClearFilter: { scope.clearFilter(filter: filter) })
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 32)
        .scrollView()
    }
    
    private var productsView: some View {
        guard let products = self.products.value as? [DataProductSearch] else { return AnyView(EmptyView()) }
        
        return AnyView (
            ZStack {
                List {
                    ForEach(Array(products.enumerated()), id: \.element) { index, product in
                        let topPadding: CGFloat = index == 0 ? 0 : 16

                        ProductView(product: product)
                            .padding(.top, topPadding)
                            .listRowInsets(.init())
                            .listRowBackground(Color.clear)
                            .background(appColor: .primary)
                            .onTapGesture {
                                scope.selectProduct(product: product, index: Int32(index))
                            }
                            .onAppear {
                                if index == products.count - 1 &&
                                    scope.canLoadMore() &&
                                    isInProgress.value == false {
                                    scope.loadMoreProducts()

                                    self.scrollElementIndex = index
                                }
                            }
                    }
                }
                .introspectTableView { (tableView) in
                    if self.productsListTableView == nil {
                        self.productsListTableView = tableView
                    }
                    
                    if self.isInProgress.value == true { return }
                    
                    setUpInitialScrollData()
                    
                    if let elementToScrollTo = self.elementToScrollTo {
                        scrollToCell(elementToScrollTo)
                    }
                }
                .onAppear {
                    UITableView.appearance().backgroundColor = UIColor.clear
                    UITableViewCell.appearance().backgroundColor = UIColor.clear
                }
                .onReceive(self.products.$value) { value in
                    guard let value = value, value.count > 0 else { return }
                    
                    self.productsListTableView = nil
                }
            }
        )
    }
    
    private func setUpInitialScrollData() {
        let visibleProductIndex = scope.getVisibleProductIndex()
        
        if visibleProductIndex != 0 {
            self.elementToScrollTo = Int(visibleProductIndex)
            self.scrollPostion = .top
        }
        else if self.scrollElementIndex != 0 {
            self.elementToScrollTo = self.scrollElementIndex
            self.scrollPostion = .bottom
        }
        else {
            self.elementToScrollTo = nil
            self.scrollPostion = .none
        }
    }
    
    private func scrollToCell(_ index: Int, animated: Bool = false) {
        let indexPath = IndexPath(row: index, section: 0)
        
        guard let productsListTableView = self.productsListTableView,
           productsListTableView.dataSource != nil,
           productsListTableView.hasRowAtIndexPath(indexPath) else { return }
        
        productsListTableView.scrollToRow(at: indexPath,
                                          at: scrollPostion,
                                          animated: animated)
        self.elementToScrollTo = nil
    }
}

// MARK: Filter
private struct FilterView: View {
    let filter: DataFilter
    
    let searchOption: SearchOption?
    
    let onSelectFilterOption: (DataOption<NSString>) -> ()
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
                SearchBar(placeholderLocalizationKey: "search_manufacturer",
                          searchText: searchOption.text,
                          trailingButton: .clear,
                          onTextChange: searchOption.onSearch)
            }
            
            FlexibleView(data: filter.options,
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
    
    let option: DataOption<NSString>
    
    var body: some View {
        guard let optionValue = option.value else { return AnyView(EmptyView()) }
        
        return AnyView(
            ZStack {
                let backgroundColor: AppColor = option.isSelected ? .yellow : .white
                
                backgroundColor.color
                    .cornerRadius(height / 2)
                
                HStack {
                    if option.isSelected {
                        Image(systemName: "checkmark")
                            .foregroundColor(appColor: .darkBlue)
                    }
                    
                    let textWeight: TextWeight = option.isSelected ? .semiBold : .regular
                    LocalizedText(localizationKey: optionValue as String,
                                  textWeight: textWeight)
                }
                .padding(.horizontal, 10)
            }
            .frame(height: height)
        )
    }
    
}

private struct SearchOption {
    let text: NSString?
    let onSearch: (String) -> ()
}

// MARK: Products
private struct ProductView: View {
    private let alignment: HorizontalAlignment = .leading
    private let textAlignment: TextAlignment = .leading
    
    let product: DataProductSearch
    
    var body: some View {
        ZStack(alignment: Alignment(horizontal: alignment, vertical: .center)) {
            AppColor.white.color
                .cornerRadius(5)
            
            VStack(alignment: alignment) {
                HStack(spacing: 17) {
                    ProductImage(medicineId: product.medicineId,
                                 size: .px123)
                        .frame(width: 81, height: 81)
                    
                    VStack(alignment: alignment, spacing: 10) {
                        VStack(alignment: alignment, spacing: 5) {
                            Text(product.name)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            multilineTextAlignment: textAlignment)
                            
                            Text(product.formattedPrice)
                                .medicoText(textWeight: .black,
                                            fontSize: 16,
                                            multilineTextAlignment: textAlignment)
                        }
                        
                        VStack(alignment: alignment, spacing: 5) {
                            HStack(spacing: 20) {
                                LocalizedText(localizedStringKey: LocalizedStringKey("mrp \(String(format: "%.2f", product.mrp))"),
                                              testingIdentifier: "mrp",
                                              fontSize: 12,
                                              color: .grey3,
                                              multilineTextAlignment: textAlignment)
                                
                                LocalizedText(localizedStringKey: LocalizedStringKey("ptr \(product.ptrPercentage)"),
                                              testingIdentifier: "ptr",
                                              fontSize: 12,
                                              color: .grey3,
                                              multilineTextAlignment: .leading)
                            }
                            
                            LocalizedText(localizedStringKey: LocalizedStringKey("code \(product.productCode)"),
                                          testingIdentifier: "code",
                                          fontSize: 12,
                                          color: .grey3,
                                          multilineTextAlignment: textAlignment)
                        }
                    }
                }
                
                Text(product.packageForm)
                    .medicoText(color: .lightBlue,
                                multilineTextAlignment: textAlignment)
            }
            .padding(11)
        }
    }
}
