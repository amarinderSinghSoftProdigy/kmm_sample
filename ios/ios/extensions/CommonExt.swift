//
//  CommonExt.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import core
import SwiftUI

var navigator: UiNavigator {
    (UIApplication.shared.delegate as! AppDelegate).navigator
}

extension String {
    func capitalizeFirstLetter() -> String {
        return prefix(1).capitalized + dropFirst()
    }
}
