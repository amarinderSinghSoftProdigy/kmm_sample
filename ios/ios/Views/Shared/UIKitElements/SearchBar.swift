//
//  SearchBar.swift
//  Medico
//
//  Created by Dasha Gurinovich on 30.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct SearchBar: UIViewRepresentable {
    let placeholder: String
    @Binding var text: String
    let onEditingChanged: (Bool) -> Void
    
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        
        searchBar.placeholder = placeholder
        searchBar.delegate = context.coordinator
        
        setUpUserInterface(for: searchBar)
        
        return searchBar
    }
    
    func updateUIView(_ searchBar: UISearchBar, context: UIViewRepresentableContext<Self>) {
        searchBar.text = text
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self, text: $text)
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
        private let parent: SearchBar
        @Binding var text: String

        init(parent: SearchBar, text: Binding<String>) {
            self.parent = parent
            _text = text
        }

        func searchBar(
            _ searchBar: UISearchBar,
            textDidChange searchText: String
        ) {
            text = searchText
        }

        func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
            parent.onEditingChanged(true)
        }

        func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
            parent.onEditingChanged(false)
        }
    }
}
