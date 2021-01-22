//
//  CommonViews.swift
//  Medico
//
//  Created by Dasha Gurinovich on 17.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct MedicoButton: View {
    let action: () -> ()
    let localizedStringKey: String
    let isEnabled: Bool
    
    let width: CGFloat?
    let height: CGFloat
    let cornerRadius: CGFloat
    let fontSize: CGFloat
    
    var body: some View {
        Button(action: action) {
            LocalizedText(localizationKey: localizedStringKey,
                          textWeight: .semiBold,
                          fontSize: fontSize)
                .frame(maxWidth:  width ?? .infinity)
        }
        .testingIdentifier("\(localizedStringKey)_button")
        .frame(maxWidth: width ?? .infinity, maxHeight: height)
        .disabled(!isEnabled)
        .background(RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(isEnabled ? Color.yellow : Color.gray))
    }
    
    init(localizedStringKey: String,
         isEnabled: Bool = true,
         width: CGFloat? = nil,
         height: CGFloat = 50,
         cornerRadius: CGFloat = 8,
         fontSize: CGFloat = 17,
         action: @escaping () -> ()) {
        self.action = action
        self.localizedStringKey = localizedStringKey
        
        self.isEnabled = isEnabled
        
        self.width = width
        self.height = height
        self.cornerRadius = cornerRadius
        self.fontSize = fontSize
    }
}

struct ReadOnlyTextField: View {
    let height: CGFloat
    
    let placeholder: String
    let text: String?
    
    let showPlaceholderWithText: Bool
    
    let errorMessageKey: String?
    
    var body: some View {
        let padding: CGFloat = 16
            
        ZStack(alignment: .leading) {
            AppColor.white.color
                .cornerRadius(8)
            
            let hasText = text?.isEmpty == false
            
            VStack(alignment: .leading, spacing: 4) {
                if !hasText || showPlaceholderWithText {
                    let fontSize: CGFloat = hasText ? 11 : 15
                    let color: AppColor = hasText ? .lightBlue : .placeholderGrey
                    
                    LocalizedText(localizationKey: placeholder,
                                  fontSize: fontSize,
                                  color: color,
                                  multilineTextAlignment: .leading)
                }
                
                if hasText {
                    LocalizedText(localizationKey: text ?? "",
                                  fontSize: 15)
                }
            }
            .padding([.leading, .trailing], padding)
        }
        .frame(height: height)
        .fieldError(withLocalizedKey: errorMessageKey, withPadding: padding)
    }
    
    init(placeholder: String,
         text: String?,
         showPlaceholderWithText: Bool = true,
         errorMessageKey: String? = nil,
         height: CGFloat = 50) {
        self.placeholder = placeholder
        self.text = text
        
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.errorMessageKey = errorMessageKey
        
        self.height = height
    }
}

struct LocalizedText: View {
    let localizationKey: String
    
    let textWeight: TextWeight
    let fontSize: CGFloat
    
    let color: AppColor
    
    let multilineTextAlignment: TextAlignment
    
    let underlined: Bool
    
    let localizedStringKey: LocalizedStringKey?
    
    var initialText: some View {
        let text = Text(localizedStringKey ?? LocalizedStringKey(localizationKey))
        
        return underlined ? text.underline() : text
    }
    
    var body: some View {
        initialText
            .medicoText(textWeight: textWeight,
                        fontSize: fontSize,
                        color: color,
                        multilineTextAlignment: multilineTextAlignment,
                        testingIdentifier: localizationKey)
    }
    
    init(localizationKey: String,
         textWeight: TextWeight? = nil,
         fontSize: CGFloat? = nil,
         color: AppColor? = nil,
         multilineTextAlignment: TextAlignment? = nil,
         underlined: Bool? = nil) {
        
        self.init(localizationKey: localizationKey,
                  textWeight: textWeight,
                  fontSize: fontSize,
                  color: color,
                  multilineTextAlignment: multilineTextAlignment,
                  underlined: underlined,
                  localizedStringKey: nil)
    }
    
    init(localizedStringKey: LocalizedStringKey,
         testingIdentifier: String,
         textWeight: TextWeight? = nil,
         fontSize: CGFloat? = nil,
         color: AppColor? = nil,
         multilineTextAlignment: TextAlignment? = nil,
         underlined: Bool? = nil) {
        
        self.init(localizationKey: testingIdentifier,
                  textWeight: textWeight,
                  fontSize: fontSize,
                  color: color,
                  multilineTextAlignment: multilineTextAlignment,
                  underlined: underlined,
                  localizedStringKey: localizedStringKey)
    }
    
    private init(localizationKey: String,
                 textWeight: TextWeight?,
                 fontSize: CGFloat?,
                 color: AppColor?,
                 multilineTextAlignment: TextAlignment?,
                 underlined: Bool?,
                 localizedStringKey: LocalizedStringKey?) {
        self.localizationKey = localizationKey
        
        self.textWeight = textWeight ?? .regular
        self.fontSize = fontSize ?? 14
        
        self.color = color ?? .darkBlue
        
        self.multilineTextAlignment = multilineTextAlignment ?? .center
        
        self.underlined = underlined ?? false
        
        self.localizedStringKey = localizedStringKey
    }
}

struct TableViewCell: View {
    let textLocalizationKey: String?
    
    let imageName: String?
    let imageColor: AppColor?
    let imageSize: CGFloat
    
    let style: Style
    
    let onTapAction: () -> ()
    
    var body: some View {
        guard let localizationKey = self.textLocalizationKey else {
            return AnyView(EmptyView())
        }
        
        return AnyView(
            Button(action: { self.onTapAction() }) {
                HStack(spacing: 24) {
                    if let imageName = self.imageName {
                        Image(imageName)
                            .renderingMode(imageColor == nil ? .original : .template)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: imageSize, height: imageSize)
                            .foregroundColor(appColor: imageColor ?? .white)
                    }
                    
                    LocalizedText(localizationKey: localizationKey,
                                  textWeight: style.textWeight,
                                  fontSize: 15,
                                  color: style.foregroundColor)
                    
                    if style.hasNavigationArrow {
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .foregroundColor(appColor: style.foregroundColor)
                    }
                }
            }
            .testingIdentifier("\(localizationKey)_button")
        )
    }
    
    init(textLocalizationKey: String?,
         imageName: String?,
         imageColor: AppColor? = nil,
         imageSize: CGFloat = 18,
         style: Style,
         onTapAction: @escaping () -> ()) {
        self.textLocalizationKey = textLocalizationKey
        
        self.imageName = imageName
        self.imageColor = imageColor
        self.imageSize = imageSize
       
        self.style = style
        self.onTapAction = onTapAction
    }
    
    enum Style {
        case navigation
        case plain
        
        var hasNavigationArrow: Bool {
            return self == .navigation
        }
        
        var textWeight: TextWeight {
            switch self {
            case .navigation:
                return .medium
            case .plain:
                return .semiBold
            }
        }
        
        var foregroundColor: AppColor {
            switch self {
            case .navigation:
                return .darkBlue
            case .plain:
                return .grey1
            }
        }
    }
}
