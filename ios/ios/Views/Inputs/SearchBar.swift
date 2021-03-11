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
    
    let showsCancelButton: Bool
    
    @State private var isSelected: Bool = false
    @Binding private var text: String
    
    // TECHNICAL DEBT
    //
    // Had to use the HStack instead the Search bar
    // because searchBar.searchingTextField.rightView works only
    // after the application was minimized and reopened
    //
    var body: some View {
        HStack {
            ZStack {
                AppColor.white.color
                    .cornerRadius(10)
                
                HStack(spacing: style.spacing) {
                    let buttonSize: CGFloat = 24
                    
                    self.getButtonView(for: self.leadingButton)
                        .frame(width: buttonSize, height: buttonSize)
                    
                    CustomPlaceholderTextField(text: $text,
                                               fontSize: 17,
                                               onEditingChanged: { self.isSelected = $0 }) {
                        LocalizedText(localizationKey: placeholderLocalizationKey,
                                      textWeight: style.fontWeight,
                                      fontSize: style.fontSize,
                                      color: style.fontColor,
                                      multilineTextAlignment: .leading)
                    }
                    .disableAutocorrection(true)
                    .disabled(isDisabled)
                    
                    self.getButtonView(for: trailingButton)
                        .frame(width: buttonSize, height: buttonSize)
                }
                .padding(.horizontal, 8)
            }
            .frame(height: style.height)
            
            if showsCancelButton && isSelected {
                Button(action: {
                    self.text = ""
                    self.hideKeyboard()
                }) {
                    LocalizedText(localizationKey: "cancel",
                                  fontSize: 17,
                                  color: .blue)
                }
            }
        }
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
         showsCancelButton: Bool = true,
         leadingButton: SearchBarButton? = SearchBarButton(button: .smallMagnifyingGlass),
         trailingButton: SearchBarButton? = SearchBarButton(emptyTextButton: nil,
                                                            enteredTextButton: .clear),
         onTextChange: ((String) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.style = style
        
        self.showsCancelButton = showsCancelButton
        
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
        
        var fontColor: AppColor {
            switch self {
            case .small:
                return .grey2
                
            case .standart:
                return .darkBlue
            }
        }
        
        var fontSize: CGFloat {
            switch self {
            case .small:
                return 17
                
            case .standart:
                return 15
            }
        }
        
        var fontWeight: TextWeight {
            switch self {
            case .small:
                return .regular
                
            case .standart:
                return .bold
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
