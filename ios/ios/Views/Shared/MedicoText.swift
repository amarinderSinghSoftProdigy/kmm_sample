//
//  MedicoText.swift
//  ios
//
//  Created by Dasha Gurinovich on 27.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct MedicoText: ViewModifier {
    let textWeight: TextWeight
    let fontSize: CGFloat
    let color: AppColor
    
    func body(content: Content) -> some View {
        content
            .font(.custom(textWeight.fontName, size: fontSize))
            .foregroundColor(appColor: color)
            .multilineTextAlignment(.center)
    }
    
    init(textWeight: TextWeight = .regular, fontSize: CGFloat = 14, color: AppColor = .darkBlue) {
        self.textWeight = textWeight
        self.fontSize = fontSize
        self.color = color
    }
}

enum TextWeight: String {
    case regular
    case medium
    case semiBold
    
    fileprivate var fontName: String {
        return "Barlow-\(self.rawValue.capitalizeFirstLetter())"
    }
}
