//
//  AuthPhoneVerification.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct AuthPhoneVerification: View {
    let scope: ForgetPasswordScope.AwaitVerification
    let geometry: GeometryProxy
    
    @State var code: String = ""
    
    var body: some View {
        VStack {
            Text("verification_code_sent_hint \(scope.phoneNumber)")
                .font(.custom("Barlow-Regular", size: 14))
                .foregroundColor(appColor: .textGrey)
                .multilineTextAlignment(.center)
                .padding([.trailing, .leading], geometry.size.width * 0.15)
            
            Text("\(scope.resendTimer.value ?? 0)")
                .font(.custom("Barlow-Medium", size: 15))
                .foregroundColor(appColor: .darkBlue)
                .padding([.top, .bottom])
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                         text: $code,
                                         keyboardType: .phonePad)
                .padding([.leading, .trailing], 12)
                .padding([.top, .bottom])
            
            Button(action: {
                scope.submitOtp(otp: code)
            }) {
                Text(LocalizedStringKey("submit"))
                    .font(.custom("Barlow-Medium", size: 17))
                    .frame(maxWidth: .infinity)
            }.medicoButton(isEnabled: true)
        }
        .navigationBarTitle(LocalizedStringKey("phone_verification"), displayMode: .inline)
    }
    
    init(scope: ForgetPasswordScope.AwaitVerification, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
    }
}
