//
//  PlaceholderTextView.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

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
            
            Text(LocalizedStringKey(currentText))
                .modifier(MedicoText(fontSize: 15, color: color))
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
