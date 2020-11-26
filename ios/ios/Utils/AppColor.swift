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
    
    case white
    case yellow
    case red
    
    case lightBlue
    case darkBlue
    
    case placeholderGrey
    case textGrey
    
    var color: Color {
        switch self {
        case .white:
            return Color.white
            
        default:
            return Color(self.rawValue.capitalizeFirstLetter())
        }
    }
}
