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
    let authViewModel: AuthViewModel
    let scope: Scope.ForgetPassword.ForgetPasswordAwaitVerification
    
    @State var code: String = ""
    
    var body: some View {
        Background {
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                    VStack {
                        Text("reset_password_hint \(authViewModel.credentials.value?.getPhoneNumber() ?? "")")
                            .font(.custom("Barlow-Regular", size: 14))
                            .foregroundColor(appColor: .textGrey)
                            .multilineTextAlignment(.center)
                            .padding([.trailing, .leading], geometry.size.width * 0.15)
                        
                        Text("2:54")
                            .font(.custom("Barlow-Medium", size: 15))
                            .foregroundColor(appColor: .darkBlue)
                            .padding([.top, .bottom])
                        
                        FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                                     text: $code,
                                                     keyboardType: .phonePad)
                            .padding([.leading, .trailing], 12)
                            .padding([.top, .bottom])
                        
                        Button(action: {
                            authViewModel.submitOtp(otp: code)
                        }) {
                            Text(LocalizedStringKey("submit"))
                                .font(.custom("Barlow-Medium", size: 17))
                                .frame(maxWidth: .infinity)
                        }.medicoButton(isEnabled: true)
                    }.padding()
                    
                    
                }
            }
            .navigationBarTitle(LocalizedStringKey("phone_verification"), displayMode: .inline)
            .navigationBarBackButtonHidden(false)
        }
    }
    
    init(authViewModel: AuthViewModel, scope: Scope.ForgetPassword.ForgetPasswordAwaitVerification) {
        self.authViewModel = authViewModel
        self.scope = scope
    }
}
