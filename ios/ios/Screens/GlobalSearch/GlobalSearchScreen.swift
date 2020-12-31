//
//  GlobalSearch.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

extension SearchScope {
    override var navigationBarTintColor: UIColor? { return nil }
}

struct GlobalSearchScreen: View {
    let scope: SearchScope
    
    var body: some View {
        Text("Hello!")
        .keyboardResponder()
        .navigationBar(withNavigationBarContent: AnyView(searchBarPanel))
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
}
