//
//  MedicoButton.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct MedicoButton: ViewModifier {
    let isEnabled: Bool
    
    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity)
            .padding()
            .disabled(!isEnabled)
            .background(RoundedRectangle(cornerRadius: 8)
                            .fill(isEnabled ? Color.yellow : Color.gray))
            .foregroundColor(appColor: .darkBlue)
    }
}

struct MedicoButton_Previews: PreviewProvider {
    static var previews: some View {
        Button("Button") {  }
            .modifier(MedicoButton(isEnabled: true))
    }
}
