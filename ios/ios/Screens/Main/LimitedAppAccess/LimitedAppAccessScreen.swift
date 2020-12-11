//
//  LimitedAppAccessScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 11.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct LimitedAppAccessScreen: View {
    let name = "John Smith"
    let isDocumentUploaded = true
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 50) {
                Spacer()
                
                Text(LocalizedStringKey("welcome \(name)"))
                
                if isDocumentUploaded {
                    self.uploadedDocumentView
                }
                else {
                    
                }
                
                Spacer()
            }
            .padding([.leading, .trailing], geometry.size.width * 0.19)
        }
    }
    
    var uploadedDocumentView: some View {
        VStack(spacing: 30) {
            Image("Welcome")
            
            VStack(spacing: 19) {
                Text(LocalizedStringKey("thank_you_for_registration"))
                    .modifier(MedicoText(fontSize: 16))
                
                Text(LocalizedStringKey("documents_under_review"))
                    .modifier(MedicoText(fontSize: 16))
            }
        }
    }
}
