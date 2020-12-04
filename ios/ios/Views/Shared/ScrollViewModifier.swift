//
//  ScrollViewModifier.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct ScrollViewModifier: ViewModifier {
    @State private var inputFieldsHeight: CGFloat
    
    func body(content: Content) -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            content
            .background(GeometryReader { gp -> Color in
                let frame = gp.frame(in: .local)
                DispatchQueue.main.async {
                    self.inputFieldsHeight = frame.height
                }
                return Color.clear
            })
        }.frame(maxHeight: inputFieldsHeight)
    }
    
    init(initialInputFieldsHeight: CGFloat = 350) {
        self._inputFieldsHeight = State(initialValue: initialInputFieldsHeight)
    }
}
