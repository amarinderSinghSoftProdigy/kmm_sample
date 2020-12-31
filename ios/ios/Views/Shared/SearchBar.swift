//
//  SearchBar.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import Combine
import SwiftUI

struct SearchBar: View {
    let placeholderLocalizationKey: String
    
    let trailingButton: SearchBarButton?
    
    let onTextChange: ((String) -> Void)?
    let onEditingChanged: ((Bool) -> Void)?
    
    @State private var text: String = ""
    
    // TECHNICAL DEBT
    //
    // Had to use the HStack instead the Search bar
    // because searchBar.searchingTextField.rightView works only
    // after the application was minimized and reopened
    //
    var body: some View {
        ZStack {
            AppColor.white.color
                .cornerRadius(10)
            
            let hasTrailingButton = trailingButton != nil
            
            HStack() {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(AppColor.placeholderGrey.color)
                
                TextField(LocalizedStringKey(placeholderLocalizationKey),
                          text: $text,
                          onEditingChanged: { (changed) in onEditingChanged?(changed) },
                          onCommit: { onEditingChanged?(false) })
                    .medicoText(fontSize: 17, color: .grey2, multilineTextAlignment: .leading)
                    .textFieldStyle(PlainTextFieldStyle())
                    .disableAutocorrection(true)
                    .disabled(onTextChange == nil)
                    .onReceive(Just(text), perform: { searchText in onTextChange?(searchText) })
                
                if hasTrailingButton {
                    self.trailingButtonView
                }
            }
            .padding(.horizontal, 8)
        }
        .padding(.vertical, 5)
    }
    
    private var trailingButtonView: some View {
        guard let trailingButton = self.trailingButton else { return AnyView(EmptyView()) }
        
        let action: () -> ()
        
        switch trailingButton {
        case let .filter(filterAction):
            action = filterAction
            
        case .clear:
            action = { self.text = "" }
        }
        
        return AnyView(
            Button(action: action) {
                Image(trailingButton.buttonImageName)
            }
        )
    }
    
    init(placeholderLocalizationKey: String = "search",
         trailingButton: SearchBarButton? = nil,
         onTextChange: ((String) -> Void)? = nil,
         onEditingChanged: ((Bool) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.trailingButton = trailingButton
        
        self.onTextChange = onTextChange
        self.onEditingChanged = onEditingChanged
    }
}

enum SearchBarButton {
    case clear
    
    case filter(() -> ())
    
    var buttonImageName: String {
        switch self {
        case .clear:
            return "Clear"
        case .filter:
            return "Filter"
        }
    }
}
