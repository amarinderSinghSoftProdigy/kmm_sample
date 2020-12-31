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
    }
    
    private var searchBarPanel: some View {
        HStack {
            SearchBar(trailingButton: .filter({ scope.toggleFilter() }),
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
                    getFilterView(for: filter)
                }
            }
        }
    }
    
    private func getFilterView(for filter: DataFilter) -> AnyView {
        AnyView(
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
                            scope.clearFilter(filter: filter)
                        }
                }
                
                // Remove the scroll bar
                GridStack(minCellWidth: 100,
                          spacing: 20,
                          numItems: filter.options.count) { index, cellWidth in
                    ZStack {
                        AppColor.yellow.color
                        LocalizedText(localizedStringKey: filter.options[index].value! as String)
                    }
                        .frame(width: cellWidth, height: 30)
                }
            }
        )
    }
    
    private var productsView: some View {
        Text("Products")
    }
}
