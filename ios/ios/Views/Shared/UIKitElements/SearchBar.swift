//
//  SearchBar.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct SearchBar: UIViewRepresentable {
    let placeholderLocalizationKey: String
    
    let onTextChange: ((String) -> Void)?
    let onEditingChanged: ((Bool) -> Void)?
    
    init(placeholderLocalizationKey: String = "search",
         onTextChange: ((String) -> Void)? = nil,
         onEditingChanged: ((Bool) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.onTextChange = onTextChange
        self.onEditingChanged = onEditingChanged
    }
    
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        
        searchBar.searchTextField.isEnabled = onTextChange != nil
        
        searchBar.placeholder = placeholderLocalizationKey.localized
        searchBar.delegate = context.coordinator
        
        setUpUserInterface(for: searchBar)
        
        return searchBar
    }
    
    func updateUIView(_ searchBar: UISearchBar, context: UIViewRepresentableContext<Self>) { }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(onTextChange: onTextChange,
                    onEditingChanged: onEditingChanged)
    }
    
    private func setUpUserInterface(for searchBar: UISearchBar) {
        clearBackgroundColor(for: searchBar)
        searchBar.searchTextField.backgroundColor = .white
        
        let textFieldColor = UIColor(named: "Grey2")
        searchBar.searchTextField.leftView?.tintColor = textFieldColor
        
        searchBar.searchTextField.clearButtonMode = .never
        
        searchBar.searchTextField.textColor = textFieldColor
        searchBar.searchTextField.font = UIFont(name: TextWeight.regular.fontName,
                                                size: 17)
    }
    
    private func clearBackgroundColor(for searchBar: UISearchBar) {
        guard let UISearchBarBackground: AnyClass = NSClassFromString("UISearchBarBackground") else { return }

        for view in searchBar.subviews {
            for subview in view.subviews where subview.isKind(of: UISearchBarBackground) {
                subview.alpha = 0
            }
        }
    }
    
    final class Coordinator: NSObject, UISearchBarDelegate {
        private let onTextChange: (String) -> Void
        private let onEditingChanged: ((Bool) -> Void)?

        init(onTextChange: ((String) -> Void)?,
             onEditingChanged: ((Bool) -> Void)?) {
            self.onTextChange = onTextChange ?? { _ in }
            
            self.onEditingChanged = onEditingChanged
        }

        func searchBar(
            _ searchBar: UISearchBar,
            textDidChange searchText: String
        ) {
            onTextChange(searchText)
        }

        func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
            guard let onEditingChanged = self.onEditingChanged else { return }
            
            onEditingChanged(true)
        }

        func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
            guard let onEditingChanged = self.onEditingChanged else { return }
            
            onEditingChanged(false)
        }
    }
}
