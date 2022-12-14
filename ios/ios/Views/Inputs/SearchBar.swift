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
    let backgroundColor: AppColor

    let showsCancelButton: Bool
    
    @State private var isSelected: Bool = false
    @Binding private var text: String
    
    let onTextChange: ((String, Bool) -> Void)?
    
    // TECHNICAL DEBT
    //
    // Had to use the HStack instead the Search bar
    // because searchBar.searchingTextField.rightView works only
    // after the application was minimized and reopened
    //
    var body: some View {
        HStack {
            ZStack {
                backgroundColor.color
                    .cornerRadius(10)
                
                HStack(spacing: style.spacing) {
                    let buttonSize: CGFloat = 24
                    
                    self.getButtonView(for: self.leadingButton)
                        .frame(width: buttonSize, height: buttonSize)
                    
                    CustomPlaceholderTextField(text: $text,
                                               fontSize: 17,
                                               onEditingChanged: { startedEditing in
                                                if !startedEditing {
                                                    onTextChange?(text, true)
                                                }
                                                
                                                self.isSelected = startedEditing
                                               }) {
                        LocalizedText(localizationKey: placeholderLocalizationKey,
                                      textWeight: style.fontWeight,
                                      fontSize: style.fontSize,
                                      color: style.fontColor,
                                      multilineTextAlignment: .leading)
                            .opacity(style.placeholderOpacity)
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
        case let .filter(_, filterAction):
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
         backgroundColor: AppColor = .white,
         showsCancelButton: Bool = true,
         leadingButton: SearchBarButton? = SearchBarButton(button: .smallMagnifyingGlass),
         trailingButton: SearchBarButton? = SearchBarButton(emptyTextButton: nil,
                                                            enteredTextButton: .clear),
         onTextChange: ((String, Bool) -> Void)? = nil) {
        self.placeholderLocalizationKey = placeholderLocalizationKey
        
        self.style = style
        
        self.showsCancelButton = showsCancelButton
        
        self.leadingButton = leadingButton
        self.trailingButton = trailingButton
        
        self.isDisabled = onTextChange == nil
        
        self._text = Binding.init(get: { (searchText ?? "") as String },
                                  set: { value in onTextChange?(value, false) })
        
        self.onTextChange = onTextChange
        
        self.backgroundColor = backgroundColor
    }
    
    enum Style {
        case small
        case standart
        
        case custom(spacing: CGFloat = 15,
                    height: CGFloat = 48,
                    fontColor: AppColor = .darkBlue,
                    fontSize: CGFloat = 15,
                    fontWeight: TextWeight = .bold,
                    placeholderOpacity: Double = 1)
        
        var spacing: CGFloat {
            switch self {
            case .small:
                return 6
                
            case .standart:
                return 15
                
            case .custom(let spacing, _, _, _, _, _):
                return spacing
            }
        }
        
        var height: CGFloat {
            switch self {
            case .small:
                return 36
                
            case .standart:
                return 48
                
            case .custom(_, let height, _, _, _, _):
                return height
            }
        }
        
        var fontColor: AppColor {
            switch self {
            case .small:
                return .grey2
                
            case .standart:
                return .darkBlue
                
            case .custom(_, _, let fontColor, _, _, _):
                return fontColor
            }
        }
        
        var fontSize: CGFloat {
            switch self {
            case .small:
                return 17
                
            case .standart:
                return 15
                
            case .custom(_, _, _, let fontSize, _, _):
                return fontSize
            }
        }
        
        var fontWeight: TextWeight {
            switch self {
            case .small:
                return .regular
                
            case .standart:
                return .bold
                
            case .custom(_, _, _, _, let fontWeight, _):
                return fontWeight
            }
        }
        
        var placeholderOpacity: Double {
            switch self {
            case .small,
                 .standart:
                return 1
                
            case .custom(_, _, _, _, _, let placeholderOpacity):
                return placeholderOpacity
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
        
        case filter(isHighlighted: Bool = false, () -> ())
        
        case smallMagnifyingGlass
        case magnifyingGlass
        
        var buttonImageView: some View {
            let imageName: String
            
            switch self {
            case let .custom(imageView):
                return imageView
                
            case .clear:
                imageName = "Clear"
                
            case .filter(let isHighlighted, _):
                return AnyView(
                    ZStack(alignment: .topTrailing) {
                        Image("Filter")
                            .padding(2)
                            .background(
                                RoundedRectangle(cornerRadius: 8)
                                    .fill(appColor: isHighlighted ? .yellow : .clear)
                            )
                            .padding(-2)
                        
                        if isHighlighted {
                            Circle()
                                .fill(appColor: .red)
                                .frame(width: 6, height: 6)
                                .padding(2)
                                .background(
                                    Circle()
                                        .fill(appColor: .white)
                                )
                                .offset(x: 6, y: -6)
                        }
                    }
                )
                
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
