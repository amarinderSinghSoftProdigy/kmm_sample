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
    
    let height: CGFloat
    let trailingButton: SearchBarButton?
    
    let isDisabled: Bool
    
    @Binding private var text: String
    
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
            
            let hasTrailingButton = trailingButton != nil &&
                !(trailingButton == .clear && text.isEmpty)
            
            HStack() {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(AppColor.placeholderGrey.color)
                
                TextField("", text: $text)
                    .medicoText(fontSize: 17, color: .grey2, multilineTextAlignment: .leading)
                    .textFieldStyle(PlainTextFieldStyle())
                    .disableAutocorrection(true)
                    .disabled(isDisabled)
                    .introspectTextField { uiTextField in
                        uiTextField.attributedPlaceholder =
                            NSAttributedString(string: placeholderLocalizationKey.localized,
                                               attributes: [NSAttributedString.Key.foregroundColor: UIColor(named: "Grey2") ?? UIColor.gray])
                    }
                
                if hasTrailingButton {
                    self.trailingButtonView
                }
            }
            .padding(.horizontal, 8)
        }
        .frame(height: height)
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
         searchText: NSString? = nil,
         height: CGFloat = 36,
         trailingButton: SearchBarButton? = nil,
         onTextChange: ((String) -> Void)? = nil,
         onEditingChanged: ((Bool) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.height = height
        self.trailingButton = trailingButton
        
        self.isDisabled = onTextChange == nil
        
        self._text = Binding.init(get: { (searchText ?? "") as String },
                                  set: { value in onTextChange?(value) })
    }
}

enum SearchBarButton: Equatable {
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
    
    static func == (lhs: SearchBarButton, rhs: SearchBarButton) -> Bool {
        switch (lhs, rhs) {
        case (.clear, .clear),
             (.filter, .filter):
            return true
            
        default:
            return false
        }
    }
}
