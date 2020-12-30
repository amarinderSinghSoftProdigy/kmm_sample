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
        VStack(spacing: 0) {
            searchBarPanel
            
            Text("Hello!")
        }
        .keyboardResponder()
        .navigationBarTitle("", displayMode: .inline)
    }
    
    private var searchBarPanel: some View {
        VStack(spacing: 0) {
            ZStack {
                AppColor.navigationBar.color
                    .edgesIgnoringSafeArea(.all)
                
                HStack {
                    SearchBar(onTextChange: { value in scope.searchProduct(input: value) })
                    
                    Button(LocalizedStringKey("cancel")) {
                        
                    }
                    .medicoText(fontSize: 17,
                                color: .blue)
                }
                .padding([.leading, .trailing], 20)
                .padding([.bottom], 10)
            }
            .frame(height: 66)
            
            AppColor.lightGrey.color.frame(height: 1)
        }
    }
}
