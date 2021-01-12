//
//  AppColor.swift
//  ios
//
//  Created by Dasha Gurinovich on 25.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import SwiftUI

enum AppColor: String {
    case primary
    case navigationBar
    
    case white
    case yellow
    case red
    
    case blue
    case lightBlue
    case darkBlue
    
    case grey1
    case grey2
    case grey3
    case placeholderGrey
    case textGrey
    case lightGrey
    
    case black
    
    var color: Color {
        switch self {
        case .white:
            return Color.white
            
        default:
            return Color(self.rawValue.capitalizeFirstLetter())
        }
    }
}
