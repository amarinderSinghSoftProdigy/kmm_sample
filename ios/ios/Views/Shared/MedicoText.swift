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
    
    let multilineTextAlignment: TextAlignment
    
    func body(content: Content) -> some View {
        content
            .font(.custom(textWeight.fontName, size: fontSize))
            .foregroundColor(appColor: color)
            .multilineTextAlignment(multilineTextAlignment)
    }
    
    init(textWeight: TextWeight = .regular,
         fontSize: CGFloat = 14,
         color: AppColor = .darkBlue,
         multilineTextAlignment: TextAlignment = .center) {
        self.textWeight = textWeight
        self.fontSize = fontSize
        
        self.color = color
        
        self.multilineTextAlignment = multilineTextAlignment
    }
}

enum TextWeight: String {
    case regular // 400
    case medium // 500
    case semiBold // 600
    case bold // 700
    
    var fontName: String {
        return "Barlow-\(self.rawValue.capitalizeFirstLetter())"
    }
}
