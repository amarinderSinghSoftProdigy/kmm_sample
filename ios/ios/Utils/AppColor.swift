//
//  AppColor.swift
//  ios
//
//  Created by Dasha Gurinovich on 25.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import SwiftUI

enum AppColor {
    case primary
    case secondary
    
    case white
    case yellow
    case lightBlue
    case red
    
    case placeholderGray
    
    var color: Color {
        switch self {
        case .primary:
            return Color("Primary")
        case .secondary:
            return Color("Secondary")
            
        case .white:
            return Color.white
        case .yellow:
            return Color("Yellow")
        case .lightBlue:
            return Color("LightBlue")
        case .red:
            return Color(hex: "FF1744")
            
        case .placeholderGray:
            return Color(hex: "8E8E93")
            
        }
    }
}
