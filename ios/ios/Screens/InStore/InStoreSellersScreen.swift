//
//  InstoreScreen.swift
//  Medico
//
//  Created by user on 01/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

//MARK: Main Screen
struct InStoreSellersScreen: View {
    
    let scope: InStoreSellerScope

    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var totalItems: SwiftDataSource<KotlinInt>
    @ObservedObject var searchText: SwiftDataSource<NSString>

    var body: some View {
                
        VStack {
            
            VStack(alignment: .leading) {
                Text("instore_orders")
                    .medicoText(textWeight: .semiBold,
                                fontSize: 20,
                                multilineTextAlignment: .leading)
                SearchBar(placeholderLocalizationKey: "search_tradename_mobile_no",
                          searchText: searchText.value,
                          style: .custom(fontWeight: .medium, placeholderOpacity: 0.5),
                          leadingButton: nil,
                          trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                    enteredTextButton: .clear),
                          onTextChange: { newValue, _ in scope.search(value: newValue) })
                    
            }
            .padding([.horizontal,.top], 20)
            
            let isItemAvailable = (self.items.value?.count ?? 0) > 0
            
            if isItemAvailable {
                TransparentList(data: items,
                                dataType: DataInStoreSeller.self,
                                listName: .instoreOrders,
                                pagination: scope.pagination,
                                elementsSpacing: 5,
                                onTapGesture: { scope.selectItem(item: $0) },
                                loadItems: { scope.loadItems() }) { _, seller in
                    
                    InStoreSellerItem(seller: seller)
                }
            } else {
                InStoreEmptyListView(imageName: "EmptyInstoreOrders",
                              titleLocalizationKey: "empty_instore_orders")
            }
            
            MedicoImageButton(localizedStringKey: "instore_order",
                         isEnabled: true,
                         cornerRadius: 24,
                         fontSize: 15,
                         fontWeight: .bold,
                         imageName: "Plus") {
                scope.goToInStoreUsers()
            }
            .padding(.horizontal, 20)
        }
    }
    
    init(scope: InStoreSellerScope) {
        self.scope = scope
        self.searchText = SwiftDataSource(dataSource: scope.searchText)
        self.items = SwiftDataSource(dataSource: scope.items)
        self.totalItems = SwiftDataSource(dataSource: scope.totalItems)
    }
}

//MARK: Seller Item
private struct InStoreSellerItem: View {
    
    let seller: DataInStoreSeller
    
    var body: some View {
        VStack(alignment: .leading, spacing: 5) {
            VStack(alignment: .leading) {
                Text(seller.tradeName)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 16,
                                color: .darkBlue)
                    .lineLimit(1)
                Spacer()
                HStack(spacing: 2) {
                    Text(seller.city)
                    Spacer()
                    Text(seller.phoneNumber)
                       
                        .opacity(0.6)
                }.medicoText(textWeight: .medium,
                              fontSize: 12,
                              color: .grey3,
                              multilineTextAlignment: .leading)
                Spacer()
                HStack(spacing: 5) {
                    HStack(spacing: 2) {
                        LocalizedText(localizationKey: "items:",
                                      textWeight: .medium,
                                      color: .darkBlue)
                        
                        Text("\(seller.items)")
                            .medicoText(textWeight: .bold,
                                        color: .lightBlue)
                    }
                    Spacer()
                    Text(seller.total.formatted)
                        .medicoText(textWeight: .bold,
                                    color: .darkBlue)
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(AppColor.white.color.opacity(1))
    }
}

