//
//  ViewExt.swift
//  ios
//
//  Created by Arnis on 18.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

extension View {
    func authInputField() -> some View {
        self.modifier(AuthInputField())
    }
    
    func medicoButton(isEnabled: Bool) -> some View {
        self.modifier(MedicoButton(isEnabled: isEnabled))
    }
    
    @ViewBuilder func isHidden(_ hidden: Bool, remove: Bool = false) -> some View {
        if hidden {
            if !remove {
                self.hidden()
            }
        } else {
            self
        }
    }
}
