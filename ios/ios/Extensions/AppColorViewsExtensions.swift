//
//  AppColorExtensions.swift
//  ios
//
//  Created by Dasha Gurinovich on 25.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

extension View {
    func background(appColor: AppColor) -> some View {
        self.background(appColor.color)
    }
    
    func foregroundColor(appColor: AppColor) -> some View {
        self.foregroundColor(appColor.color)
    }
}

extension Shape {
    func fill(appColor: AppColor) -> some View {
        self.fill(appColor.color)
    }
}
