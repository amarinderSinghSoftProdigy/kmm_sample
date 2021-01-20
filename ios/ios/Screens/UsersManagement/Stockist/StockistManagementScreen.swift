//
//  StockistManagementScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct StockistManagementScreen: View {
    @State private var stockistText: NSString = ""
    
    var body: some View {
        let stockistImage = Image("Stockist").resizable()
        
        VStack(spacing: 16) {
            SearchBar(placeholderLocalizationKey: "stockists",
                      searchText: stockistText,
                      leadingButton: SearchBar.SearchBarButton(emptyTextButton: .custom(AnyView(stockistImage)),
                                                               enteredTextButton: .smallMagnifyingGlass),
                      trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                enteredTextButton: .clear),
                      onTextChange: { newValue in stockistText = newValue as NSString })
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 32)
        .screenLogger(withScreenName: "StockistManagement",
                      withScreenClass: StockistManagementScreen.self)
    }
}
