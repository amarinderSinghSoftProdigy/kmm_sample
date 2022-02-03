//
//  InStoreOrder.swift
//  Medico
//
//  Created by user on 03/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct InStoreProducts: View {
    
    let scope: InStoreProductsScope
    @ObservedObject var cart: SwiftDataSource<DataInStoreCart>
    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var searchText: SwiftDataSource<NSString>
    @ObservedObject var totalItems: SwiftDataSource<KotlinInt>

    var body: some View {
        VStack {
            HStack(spacing: 5) {
                countStack(title: "items", value: "\(cart.value?.entries.count ?? 0)")
                countStack(title: "qty", value: "\(cart.value?.totalQty.value ?? 0)")
                countStack(title: "free", value: "\(cart.value?.totalFreeQty.value ?? 0)")
                countStack(title: "amount", value: "\(cart.value?.totalFreeQty.value ?? 0)")
            }
            .padding(10)
        }
    }
    
    init(scope: InStoreProductsScope) {
        self.scope = scope
        self.cart = SwiftDataSource(dataSource: scope.cart)
        self.items = SwiftDataSource(dataSource: scope.items)
        self.searchText = SwiftDataSource(dataSource: scope.searchText)
        self.totalItems = SwiftDataSource(dataSource: scope.totalItems)
    }
    
    func countStack(title: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 5) {
            LocalizedText(localizationKey: title, textWeight: .regular, fontSize: 12, color: .darkBlue)
            Text(value).medicoText(textWeight: .semiBold, fontSize: 12, color: .darkBlue)
        }
    }
}
