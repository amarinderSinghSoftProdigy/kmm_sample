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
    case navigationBar
    
    case white
    case yellow
    case red
    case green
    case orange
    
    case lightPink
    case lightGreen
    case lightRed
    
    case blue
    case lightBlue
    case darkBlue
    case blueWhite
    
    case grey1
    case grey2
    case grey3
    case placeholderGrey
    case textGrey
    case lightGrey
    
    case greyBlue
    
    case black
    
    case clear
    
    case hex(String)
    
    var color: Color {
        switch self {
        case .white:
            return Color.white
            
        case .clear:
            return Color.clear
            
        case .hex(let hex):
            return Color(hex: hex)
            
        default:
            return Color(String(describing: self).capitalizeFirstLetter())
        }
    }
}
