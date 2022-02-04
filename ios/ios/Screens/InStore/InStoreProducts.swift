//
//  InStoreOrder.swift
//  Medico
//
//  Created by user on 03/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

//item.stockInfo.status != StockStatus.OUT_OF_STOCK,

struct InStoreProducts: View {
        
    let scope: InStoreProductsScope
    @ObservedObject var cart: SwiftDataSource<DataInStoreCart>
    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var searchText: SwiftDataSource<NSString>
    @ObservedObject var totalItems: SwiftDataSource<KotlinInt>

    var body: some View {
        VStack {
            
            ViewOrder
            
            AppColor.black.color
                .opacity(0.27)
                .frame(height: 1)
            
            SearchBar(placeholderLocalizationKey: "search_by_product",
                      searchText: searchText.value,
                      style: .custom(fontWeight: .medium, placeholderOpacity: 0.5),
                      leadingButton: nil,
                      trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                enteredTextButton: .clear),
                      onTextChange: { newValue, _ in scope.search(value: newValue) })
                .padding(15)
            
            if (items.value?.count ?? 0) > 0 {
                
                TransparentList(data: items,
                                dataType: DataInStoreProduct.self,
                                listName: .instoreProducts,
                                pagination: scope.pagination,
                                elementsSpacing: 8,
                                onTapGesture: { _ in },
                                loadItems: { scope.loadItems() }) { _, item in
                    ProductView(product:  item) {
                        scope.selectItem(item: item)
                    }
                }
                
            } else {
                InStoreEmptyListView(imageName: "EmptyInstoreOrders",
                                     titleLocalizationKey: "empty_instore_products")
            }
        }
        .onAppear {
            scope.firstLoad()
        }
        .screenLogger(withScreenName: "InStoreProducts",
                      withScreenClass: InStoreProducts.self)
    }
    
    init(scope: InStoreProductsScope) {
        self.scope = scope
        self.cart = SwiftDataSource(dataSource: scope.cart)
        self.items = SwiftDataSource(dataSource: scope.items)
        self.searchText = SwiftDataSource(dataSource: scope.searchText)
        self.totalItems = SwiftDataSource(dataSource: scope.totalItems)
    }
    
    var ViewOrder: some View {
        Group {
            GeometryReader { geometry in
                HStack(spacing: 0) {
                    HStack {
                        countStack(title: "items", value: "\(cart.value?.entries.count ?? 0)")
                        Spacer()
                        countStack(title: "qty", value: "\(cart.value?.totalQty.value ?? 0)")
                        Spacer()
                        countStack(title: "free", value: "\(cart.value?.totalFreeQty.value ?? 0)")
                    }
                    .frame(width: geometry.size.width * 0.32)
                    countStack(title: "amount", value: "\(cart.value?.total.formattedPrice ?? "")")
                    .frame(width: geometry.size.width * 0.30)
                    MedicoButton(localizedStringKey: "order_request_button",
                                 isEnabled: cart.value?.id != nil,
                                 width: geometry.size.width * 0.38,
                                 height: 40,
                                 cornerRadius: 24,
                                 fontSize: 15,
                                 fontWeight: .bold,
                                 fontColor: .darkBlue) {
                       // scope.goToInStoreCart()
                    }
                }
                .frame(height: 40)
                .padding(12)
            }
        }
        .frame(height: 64)
    }
    
    func countStack(title: String, value: String) -> some View {
        VStack(alignment: .center, spacing: 5) {
            LocalizedText(localizationKey: title, textWeight: .regular, fontSize: 12, color: .darkBlue)
            Text(value).medicoText(textWeight: .semiBold, fontSize: 12, color: .darkBlue)
        }
    }
    
    private struct ProductView: View {
        
        let product: DataInStoreProduct
        
        let onSelectItem: ()->()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 0) {
                
                VStack(alignment: .leading, spacing: 15) {
                    
                    ProductTitleView(productName: product.name, color: product.stockInfo.statusColor.color)
                    
                    VStack(spacing: 10) {
                        
                        HStack(spacing: 5) {
                            
                            HStack(spacing: 2) {
                                LocalizedText(localizationKey: "ptr:",
                                              textWeight: .semiBold,
                                              color: .greyBlue)
                                Text("\(product.priceInfo.price.formattedPrice)")
                                    .medicoText(textWeight: .bold,
                                                fontSize: 15,
                                                color: .darkBlue)
                            }
                            
                            Spacer()
                            
                            HStack(spacing: 2) {
                                LocalizedText(localizationKey: "stocks:",
                                              textWeight: .regular,
                                              fontSize: 12,
                                              color: .greyBlue)
                                Text("\(product.stockInfo.availableQty)")
                                    .medicoText(textWeight: .bold,
                                                fontSize: 12,
                                                color: .greyBlue)
                            }
                        }
                        
                        HStack {
                            ExpiryView(date: product.stockInfo.expiry.formattedDate,
                                       color: .hex(product.stockInfo.expiry.color))
                            Spacer()
                        }
                        
                        AppColor.grey1.color
                            .opacity(0.2)
                            .frame(height: 1)
                        
                        GeometryReader { geometry in
                        
                            MedicoButton(localizedStringKey: "add_to_cart",
                                         isEnabled: true,
                                         width: geometry.size.width,
                                         height: 40,
                                         cornerRadius: 24,
                                         fontSize: 15,
                                         fontWeight: .bold,
                                         fontColor: .darkBlue) {
                               self.onSelectItem()
                            }
                            .buttonStyle(BorderlessButtonStyle())
                        }
                        .frame(height: 40)
                    }
                }
                .padding(15)
                
            }
            .background(appColor: .white)
        }
    }
    
    struct ProductTitleView: View {
        let productName: String
        let color: Color
        var body: some View {
            return AnyView(
                HStack(spacing: 4) {
                    color
                        .cornerRadius(3)
                        .frame(width: 10, height: 10)
                    Text(productName)
                        .medicoText(textWeight: .semiBold, fontSize: 15)
                }
            )
        }
    }
    
    private struct ExpiryView: View {
        let date: String
        let color: AppColor
        var body: some View {
            HStack {
                HStack(spacing: 2) {
                    LocalizedText(localizationKey: "expiry:", textWeight: .regular, fontSize: 12, color: .grey1)
                    Text(date)
                        .medicoText(textWeight: .bold, fontSize: 12, color: color)
                        .autocapitalization(.words)
                }.padding(6)
            }
            .background(RoundedRectangle(cornerRadius: 6)
                            .fill(appColor: color)
                            .opacity(0.3))
        }
    }
    
    private struct DistanceView: View {
        let distance: String
        var body: some View {
            HStack {
                HStack {
                    Image("MapPin")
                        .renderingMode(.template)
                        .foregroundColor(AppColor.darkBlue.color)
                        .frame(width: 10, height: 10)
                        .aspectRatio(contentMode: .fit)
                    Text(distance)
                        .medicoText(textWeight: .semiBold, fontSize: 12, color: .orange)
                        .autocapitalization(.words)
                }.padding(6)
            }
            .background(RoundedRectangle(cornerRadius: 6)
                            .fill(appColor: .orange)
                            .opacity(0.3))
        }
    }
}



/*
 countStack(title: "items", value: "999")
 Spacer()
 countStack(title: "qty", value: "999.09")
 Spacer()
 countStack(title: "free", value: "999.09")
 countStack(title: "amount", value: "₹ 1,14,674.75")
 */
