//
//  Background.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct Background<Content: View>: View {
    private var content: Content

    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content()
    }

    var body: some View {
        Color.white
            .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
            .overlay(content)
            .onTapGesture {
                UIApplication.shared.endEditing()
            }
    }
}
