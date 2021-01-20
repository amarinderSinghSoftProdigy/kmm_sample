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
    
    let style: Style
    
    let leadingButton: SearchBarButton?
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
            
            HStack(spacing: style.spacing) {
                let buttonSize: CGFloat = 24
                
                self.getButtonView(for: self.leadingButton)
                    .frame(width: buttonSize, height: buttonSize)
                
                TextField(LocalizedStringKey(placeholderLocalizationKey), text: $text)
                    .medicoText(fontSize: 17, color: .darkBlue, multilineTextAlignment: .leading)
                    .textFieldStyle(PlainTextFieldStyle())
                    .disableAutocorrection(true)
                    .disabled(isDisabled)
                    .introspectTextField { uiTextField in
                        uiTextField.attributedPlaceholder =
                            NSAttributedString(string: placeholderLocalizationKey.localized,
                                               attributes: [NSAttributedString.Key.foregroundColor: style.fontColor,
                                                            NSAttributedString.Key.font: style.font])
                    }
                
                self.getButtonView(for: trailingButton)
                    .frame(width: buttonSize, height: buttonSize)
            }
            .padding(.horizontal, 8)
        }
        .frame(height: style.height)
    }
    
    private func getButtonView(for searchBarButton: SearchBarButton?) -> some View {
        
        guard let button = !text.isEmpty ?
                searchBarButton?.enteredTextButton : searchBarButton?.emptyTextButton
            else { return AnyView(EmptyView()) }
        
        let action: () -> ()
        
        switch button {
        case let .filter(filterAction):
            action = filterAction
            
        case .clear:
            action = { self.text = "" }
            
        default:
            return AnyView(button.buttonImageView)
        }
        
        return AnyView(
            Button(action: action) {
                button.buttonImageView
            }
        )
    }
    
    init(placeholderLocalizationKey: String = "search",
         searchText: NSString? = nil,
         style: Style = .standart,
         leadingButton: SearchBarButton? = SearchBarButton(button: .smallMagnifyingGlass),
         trailingButton: SearchBarButton? = SearchBarButton(emptyTextButton: nil,
                                                            enteredTextButton: .clear),
         onTextChange: ((String) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.style = style
        
        self.leadingButton = leadingButton
        self.trailingButton = trailingButton
        
        self.isDisabled = onTextChange == nil
        
        self._text = Binding.init(get: { (searchText ?? "") as String },
                                  set: { value in onTextChange?(value) })
    }
    
    enum Style {
        case small
        case standart
        
        var spacing: CGFloat {
            switch self {
            case .small:
                return 6
                
            case .standart:
                return 15
            }
        }
        
        var height: CGFloat {
            switch self {
            case .small:
                return 36
                
            case .standart:
                return 48
            }
        }
        
        var fontColor: UIColor {
            switch self {
            case .small:
                return UIColor(named: "Grey2") ?? UIColor.gray
                
            case .standart:
                return UIColor(named: "DarkBlue") ?? UIColor.darkGray
            }
        }
        
        var font: UIFont {
            switch self {
            case .small:
                return UIFont(name: "Barlow-Regular", size: 17) ?? .systemFont(ofSize: 17)
                
            case .standart:
                return UIFont(name: "Barlow-Bold", size: 15) ?? .systemFont(ofSize: 15)
            }
        }
    }
    
    class SearchBarButton {
        let emptyTextButton: SearchBarButtonType?
        let enteredTextButton: SearchBarButtonType?
        
        init(button: SearchBarButtonType?) {
            self.emptyTextButton = button
            self.enteredTextButton = button
        }
        
        init(emptyTextButton: SearchBarButtonType?,
             enteredTextButton: SearchBarButtonType?) {
            self.emptyTextButton = emptyTextButton
            self.enteredTextButton = enteredTextButton
        }
    }
    
    enum SearchBarButtonType: Equatable {
        case clear
        
        case custom(AnyView)
        
        case filter(() -> ())
        
        case smallMagnifyingGlass
        case magnifyingGlass
        
        var buttonImageView: some View {
            let imageName: String
            
            switch self {
            case let .custom(imageView):
                return imageView
                
            case .clear:
                imageName = "Clear"
                
            case .filter:
                imageName = "Filter"
                
            case .smallMagnifyingGlass:
                return AnyView(
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(appColor: .placeholderGrey)
                )
                
            case .magnifyingGlass:
                return AnyView(
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(appColor: .grey3)
                        .font(Font.system(size: 18, weight: .medium))
                )
            }
            
            return AnyView(Image(imageName))
        }
        
        static func == (lhs: SearchBarButtonType, rhs: SearchBarButtonType) -> Bool {
            switch (lhs, rhs) {
            case (.clear, .clear),
                 (.filter, .filter),
                 (.magnifyingGlass, .magnifyingGlass):
                return true
                
            default:
                return false
            }
        }
    }
}
