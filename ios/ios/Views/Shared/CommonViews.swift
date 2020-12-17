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
            LocalizedText(localizedStringKey: localizedStringKey,
                          textWeight: .semiBold,
                          fontSize: 17)
                .frame(maxWidth: .infinity)
        }
        .accessibility(identifier: "\(localizedStringKey)_button")
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
            
            LocalizedText(localizedStringKey: currentText,
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
    let localizedStringKey: String
    
    let textWeight: TextWeight
    let fontSize: CGFloat
    
    let color: AppColor
    
    let multilineTextAlignment: TextAlignment
    
    let underlined: Bool
    
    var initialText: some View {
        let text = Text(LocalizedStringKey(localizedStringKey))
        
        return underlined ? text.underline() : text
    }
    
    var body: some View {
        initialText
            .modifier(MedicoText(textWeight: textWeight,
                                 fontSize: fontSize,
                                 color: color,
                                 multilineTextAlignment: multilineTextAlignment))
            .accessibility(identifier: localizedStringKey)
    }
    
    init(localizedStringKey: String,
         textWeight: TextWeight = .regular,
         fontSize: CGFloat = 14,
         color: AppColor = .darkBlue,
         multilineTextAlignment: TextAlignment = .center,
         underlined: Bool = false) {
        self.localizedStringKey = localizedStringKey
        
        self.textWeight = textWeight
        self.fontSize = fontSize
        
        self.color = color
        
        self.multilineTextAlignment = multilineTextAlignment
        
        self.underlined = underlined
    }
}
