//
//  SelectUserTypeScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 1.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SelectUserTypeScreen: View {
    let scope: SignUpScope.SelectUserType
    
    var body: some View {
        VStack {
            AppColor.lightBlue.color
        }
        .modifier(SignUpButton(action: { }))
        .navigationBarTitle(LocalizedStringKey("user_type"), displayMode: .inline)
    }
}

