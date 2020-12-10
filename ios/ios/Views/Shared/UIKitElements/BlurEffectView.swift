//
//  BlurEffectView.swift
//  ios
//
//  Created by Dasha Gurinovich on 10.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct BlurEffectView: UIViewRepresentable {
    let effect: UIBlurEffect.Style
    
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView {
        UIVisualEffectView(effect: UIBlurEffect(style: effect))
    }
    
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) { }
    
    init(effect: UIBlurEffect.Style = .dark) {
        self.effect = effect
    }
}
