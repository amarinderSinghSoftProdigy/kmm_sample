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
    
    var body: some View {
        Button(action: action) {
            LocalizedText(localizationKey: localizedStringKey,
                          textWeight: .semiBold,
                          fontSize: 17)
                .frame(maxWidth: .infinity)
        }
        .testingIdentifier("\(localizedStringKey)_button")
        .frame(maxWidth: .infinity)
        .padding()
        .disabled(!isEnabled)
        .background(RoundedRectangle(cornerRadius: 8)
                        .fill(isEnabled ? Color.yellow : Color.gray))
    }
    
    init(localizedStringKey: String, isEnabled: Bool = true, action: @escaping () -> ()) {
        self.action = action
        self.localizedStringKey = localizedStringKey
        
        self.isEnabled = isEnabled
    }
}

struct PlaceholderTextView: View {
    let height: CGFloat
    
    let placeholder: String
    let text: String?
    
    let errorMessageKey: String?
    
    var body: some View {
        let padding: CGFloat = 16
            
        ZStack(alignment: .leading) {
            AppColor.white.color
                .cornerRadius(8)
            
            let showsPlaceholder = text?.isEmpty != false
            let currentText = showsPlaceholder ? placeholder : text!
            let color: AppColor = showsPlaceholder ? .placeholderGrey : .darkBlue
            
            LocalizedText(localizationKey: currentText,
                          fontSize: 15,
                          color: color)
                .padding([.leading, .trailing], padding)
        }
        .frame(height: height)
        .fieldError(withLocalizedKey: errorMessageKey, withPadding: padding)
    }
    
    init(placeholder: String, text: String?, errorMessageKey: String? = nil,  height: CGFloat = 50) {
        self.placeholder = placeholder
        self.text = text
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
