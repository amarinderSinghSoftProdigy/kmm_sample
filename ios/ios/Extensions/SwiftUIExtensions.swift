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
