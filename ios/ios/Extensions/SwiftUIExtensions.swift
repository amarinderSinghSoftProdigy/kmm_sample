//
//  SwiftUIExtensions.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}

extension String {
    func getText(withBoldSubstring substring: String,
                 withFontSize fontSize: CGFloat) -> Text {
        let tag = "<SPLIT>"
        let split = self
            .replacingOccurrences(of: substring,
                                  with: tag + ">\(substring)" + tag)
            .replacingOccurrences(of: substring.uppercased(),
                                  with: tag + ">\(substring.uppercased())" + tag)
            .replacingOccurrences(of: substring.lowercased(),
                                  with: tag + ">\(substring.lowercased())" + tag)
            .components(separatedBy: tag)
        
        let text = split.reduce(Text("")) { (a, b) -> Text in
            guard !b.hasPrefix(">") else {
                return a + Text(b.dropFirst())
                    .font(.custom(TextWeight.bold.fontName, size: fontSize))
            }
            return a + Text(b)
                .font(.custom(TextWeight.regular.fontName, size: fontSize))
        }
        
        return text
            .foregroundColor(AppColor.darkBlue.color)
    }
    
}
