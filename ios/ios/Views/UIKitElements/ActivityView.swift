//
//  ActivityIndicator.swift
//  ios
//
//  Created by Dasha Gurinovich on 27.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct ActivityView: View {
    var body: some View {
        ZStack {
            BlurEffectView()
                .edgesIgnoringSafeArea(.all)
                .onAppear {
                    UIApplication.shared.endEditing()
                }
            
            ActivityIndicator(isAnimating: true) {
                $0.color = .white
            }
        }
        .testingIdentifier("ActivityView")
    }
}

struct ActivityIndicator: UIViewRepresentable {
    typealias UIView = UIActivityIndicatorView
    
    var isAnimating: Bool
    fileprivate var configuration = { (indicator: UIView) in }

    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIView { UIView() }
    
    func updateUIView(_ uiView: UIView, context: UIViewRepresentableContext<Self>) {
        isAnimating ? uiView.startAnimating() : uiView.stopAnimating()
        configuration(uiView)
    }
}

