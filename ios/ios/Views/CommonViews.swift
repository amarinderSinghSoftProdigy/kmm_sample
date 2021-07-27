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
    let fontWeight: TextWeight
    let fontColor: AppColor
    
    let buttonColor: AppColor
    let buttonColorOpacity: Double
    
    var body: some View {
        let width = self.width ?? .infinity
        
        Button(action: action) {
            LocalizedText(localizationKey: localizedStringKey,
                          textWeight: fontWeight,
                          fontSize: fontSize,
                          color: fontColor)
                .frame(maxWidth: width)
        }
        .testingIdentifier("\(localizedStringKey)_button")
        .frame(height: height)
        .frame(maxWidth: width, maxHeight: height)
        .disabled(!isEnabled)
        .background(RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(appColor: isEnabled ? buttonColor : .grey1)
                        .opacity(buttonColorOpacity)
        )
    }
    
    init(localizedStringKey: String,
         isEnabled: Bool = true,
         width: CGFloat? = nil,
         height: CGFloat = 50,
         cornerRadius: CGFloat = 8,
         fontSize: CGFloat = 17,
         fontWeight: TextWeight = .semiBold,
         fontColor: AppColor = .darkBlue,
         buttonColor: AppColor = .yellow,
         buttonColorOpacity: Double = 1,
         action: @escaping () -> ()) {
        self.action = action
        self.localizedStringKey = localizedStringKey
        
        self.isEnabled = isEnabled
        
        self.width = width
        self.height = height
        self.cornerRadius = cornerRadius
        
        self.fontSize = fontSize
        self.fontWeight = fontWeight
        self.fontColor = fontColor
        
        self.buttonColor = buttonColor
        self.buttonColorOpacity = buttonColorOpacity
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
    let localizedStringKey: LocalizedStringKey
    
    let textWeight: TextWeight
    let fontSize: CGFloat
    
    let color: AppColor
    
    let multilineTextAlignment: TextAlignment
    
    let underlined: Bool
    
    var initialText: some View {
        let text = Text(localizedStringKey)
        
        return underlined ? text.underline() : text
    }
    
    var body: some View {
        initialText
            .medicoText(textWeight: textWeight,
                        fontSize: fontSize,
                        color: color,
                        multilineTextAlignment: multilineTextAlignment)
    }
    
    init(localizationKey: String,
         textWeight: TextWeight? = nil,
         fontSize: CGFloat? = nil,
         color: AppColor? = nil,
         multilineTextAlignment: TextAlignment? = nil,
         underlined: Bool? = nil) {
        self.init(localizedStringKey: LocalizedStringKey(localizationKey),
                  textWeight: textWeight,
                  fontSize: fontSize,
                  color: color,
                  multilineTextAlignment: multilineTextAlignment,
                  underlined: underlined)
    }
    
    init(localizedStringKey: LocalizedStringKey,
         textWeight: TextWeight? = nil,
         fontSize: CGFloat? = nil,
         color: AppColor? = nil,
         multilineTextAlignment: TextAlignment? = nil,
         underlined: Bool? = nil) {
        self.localizedStringKey = localizedStringKey

        self.textWeight = textWeight ?? .regular
        self.fontSize = fontSize ?? 14

        self.color = color ?? .darkBlue

        self.multilineTextAlignment = multilineTextAlignment ?? .center

        self.underlined = underlined ?? false
    }
}

struct TableViewCell: View {
    let textLocalizationKey: String?
    
    let imageName: String?
    let imageSize: CGFloat
    
    let style: Style
    
    let extraChipTextLocalizationKey: String?
    
    let onTapAction: () -> ()
    
    var body: some View {
        guard let localizationKey = self.textLocalizationKey else {
            return AnyView(EmptyView())
        }
        
        return AnyView(
            Button(action: { self.onTapAction() }) {
                HStack(spacing: 20) {
                    if let imageName = self.imageName {
                        Image(imageName)
                            .renderingMode(.template)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: imageSize, height: imageSize)
                            .foregroundColor(appColor: style.foregroundColor)
                    }
                    
                    HStack(spacing: 12) {
                        LocalizedText(localizationKey: localizationKey,
                                      textWeight: style.textWeight,
                                      fontSize: 15,
                                      color: style.foregroundColor)
                            .lineLimit(1)
                    
                        if let extraChipTextLocalizationKey = self.extraChipTextLocalizationKey {
                            LocalizedText(localizationKey: extraChipTextLocalizationKey,
                                          textWeight: .bold,
                                          fontSize: 10,
                                          color: .white)
                                .frame(width: 40, height: 20)
                                .background(
                                    RoundedRectangle(cornerRadius: 100)
                                        .fill(appColor: .red)
                                )
                        }
                    }
                    
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
         imageSize: CGFloat = 18,
         style: Style,
         extraChipTextLocalizationKey: String? = nil,
         onTapAction: @escaping () -> ()) {
        self.textLocalizationKey = textLocalizationKey
        
        self.imageName = imageName
        self.imageSize = imageSize
       
        self.style = style
        
        self.extraChipTextLocalizationKey = extraChipTextLocalizationKey
        
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

struct CheckBoxView: View {
    @Binding var checked: Bool

    var body: some View {
        Button(action: { self.checked.toggle() }) {
            Image(systemName: checked ? "checkmark.circle.fill" : "circle")
                .foregroundColor(appColor: checked ? .lightBlue : .grey1)
                .frame(width: 25, height: 25)
        }
    }
}

struct TabOptionView: View {
    let localizationKey: String
    
    let isSelected: Bool
    let selectedColor: AppColor
    
    let itemsNumber: Int
    
    var body: some View {
        let tabBackgroundColor: AppColor = isSelected ? selectedColor : .clear
        
        HStack(spacing: 7) {
            LocalizedText(localizationKey: localizationKey,
                          textWeight: isSelected ? .semiBold : .medium,
                          color: isSelected ? .white : .darkBlue)

            if isSelected && itemsNumber > 0 {
                Text(String(itemsNumber))
                    .medicoText(textWeight: .bold,
                                color: AppColor.yellow)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 7)
        .background(tabBackgroundColor.color.cornerRadius(7))
    }
    
    init(localizationKey: String,
         isSelected: Bool,
         selectedColor: AppColor = .lightBlue,
         itemsNumber: Int) {
        self.localizationKey = localizationKey
        
        self.isSelected = isSelected
        self.selectedColor = selectedColor
        
        self.itemsNumber = itemsNumber
    }
}

struct CheckBox: View {
    var selected: Binding<Bool>
    
    let backgroundColor: AppColor
    
    var body: some View {
        Group {
            if selected.wrappedValue {
                Image(systemName: "checkmark.circle.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(appColor: .lightBlue)
            }
            else {
                Circle()
                    .fill(appColor: backgroundColor)
                    .background(
                        Circle()
                            .stroke(lineWidth: 2)
                            .foregroundColor(appColor: .darkBlue)
                    )
            }
        }
        .onTapGesture {
            selected.wrappedValue.toggle()
        }
    }
    
    init(selected: Binding<Bool>, backgroundColor: AppColor = .primary) {
        self.selected = selected
        
        self.backgroundColor = backgroundColor
    }
}

struct EmptyListView: View {
    let imageName: String
    
    let titleLocalizationKey: String
    let subtitleLocalizationKey: String?
    
    let handleHomeTap: () -> Void
    
    var body: some View {
        VStack(spacing: 24) {
            VStack(spacing: 15) {
                Image(imageName)
                
                VStack(spacing: 4) {
                    LocalizedText(localizationKey: titleLocalizationKey,
                                  textWeight: .bold,
                                  fontSize: 15)
                    
                    if let subtitleLocalizationKey = self.subtitleLocalizationKey {
                        LocalizedText(localizationKey: subtitleLocalizationKey,
                                      fontSize: 12)
                            .opacity(0.6)
                    }
                }
            }
            
            MedicoButton(localizedStringKey: "home",
                         isEnabled: true,
                         width: 105,
                         height: 43,
                         action: handleHomeTap)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(50)
    }
    
    init(imageName: String,
         titleLocalizationKey: String,
         subtitleLocalizationKey: String? = nil,
         handleHomeTap: @escaping () -> Void) {
        self.imageName = imageName
        
        self.titleLocalizationKey = titleLocalizationKey
        self.subtitleLocalizationKey = subtitleLocalizationKey
        
        self.handleHomeTap = handleHomeTap
    }
}
