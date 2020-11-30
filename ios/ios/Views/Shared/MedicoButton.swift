//
//  MedicoButton.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct MedicoButton: View {
    let action: () -> ()
    let localizedStringKey: String
    let isEnabled: Bool
    
    var body: some View {
        Button(action: action) {
            Text(LocalizedStringKey(localizedStringKey))
                .modifier(MedicoText(textWeight: .semiBold, fontSize: 17))
                .frame(maxWidth: .infinity)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .disabled(!isEnabled)
        .background(RoundedRectangle(cornerRadius: 8)
                        .fill(isEnabled ? Color.yellow : Color.gray))
    }
    
    init(action: @escaping () -> (), localizedStringKey: String, isEnabled: Bool = true) {
        self.action = action
        self.localizedStringKey = localizedStringKey
        
        self.isEnabled = isEnabled
    }
}

struct MedicoButton_Previews: PreviewProvider {
    static var previews: some View {
        MedicoButton(action: {}, localizedStringKey: "verification_code", isEnabled: true)
    }
}
