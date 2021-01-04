//
//  GlobalSearch.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core
import GridStack

extension SearchScope {
    override var navigationBarTintColor: UIColor? { return nil }
}

struct GlobalSearchScreen: View {
    let scope: SearchScope
    
    @ObservedObject var isFilterOpened: SwiftDataSource<KotlinBoolean>
    @ObservedObject var filters: SwiftDataSource<NSArray>
    
    @ObservedObject var productSearch: SwiftDataSource<NSString>
    @ObservedObject var manufucturerSearch: SwiftDataSource<NSString>
    
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
                .padding(.bottom, 10)
                .scrollView()
                .padding([.horizontal, .top], 16)
                .keyboardResponder()
                .navigationBar(withNavigationBarContent: AnyView(searchBarPanel))
        )
    }
    
    init(scope: SearchScope) {
        self.scope = scope
        
        self.isFilterOpened = SwiftDataSource(dataSource: scope.isFilterOpened)
        self.filters = SwiftDataSource(dataSource: scope.filters)
        
        self.manufucturerSearch = SwiftDataSource(dataSource: scope.manufacturerSearch)
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
        VStack {
            HStack {
                Spacer()
                
                LocalizedText(localizedStringKey: "clear_all",
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
    }
    
    private var productsView: some View {
        Text("Products")
    }
}

private struct FilterView: View {
    let filter: DataFilter
    
    let searchOption: SearchOption?
    
    let onSelectFilterOption: (DataOption<NSString>) -> ()
    let onClearFilter: () -> ()
    
    var body: some View {
        VStack {
            AppColor.lightGrey.color
                .frame(height: 1)
            
            HStack {
                LocalizedText(localizedStringKey: filter.name,
                              textWeight: .semiBold,
                              fontSize: 16)
                
                Spacer()
                
                LocalizedText(localizedStringKey: "clear",
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
                    LocalizedText(localizedStringKey: optionValue as String,
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
