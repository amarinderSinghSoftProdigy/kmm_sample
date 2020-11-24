//
//  AuthRestoreScreen.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import core

struct AuthPasswordRestoreScreen: View {
    let authViewModel: AuthViewModel
    let scope: Scope.ForgetPassword.ForgetPasswordPhoneNumberInput
    
    @State var phone: String = ""
    
    var body: some View {
        GeometryReader { geometry in
            ZStack {
                Color.primary.edgesIgnoringSafeArea(.all)
                VStack {
                    Text(LocalizedStringKey("reset_password_hint"))
                        .font(.custom("Barlow-Regular", size: 14))
                        .foregroundColor(.init(hex: "6E7882"))
                        .multilineTextAlignment(.center)
                        .padding([.trailing, .leading], geometry.size.width * 0.15)
                    TextField(LocalizedStringKey("phone_number"), text: $phone)
                        .authInputField()
                        .padding([.top, .bottom])
                    
                    Button(action: {
                        authViewModel.sendOtp(phoneNumber: phone)
                    }) {
                        Text(LocalizedStringKey("get_code"))
                            .font(.custom("Barlow-Medium", size: 17))
                            .frame(maxWidth: .infinity)
                    }.medicoButton(isEnabled: true)
                }.padding()
            }
        }
        .navigationBarTitle(LocalizedStringKey("password_reset"), displayMode: .inline)
        .navigationBarBackButtonHidden(false)
    }
    
    init(authViewModel: AuthViewModel, scope: Scope.ForgetPassword.ForgetPasswordPhoneNumberInput) {
        self.authViewModel = authViewModel
        self.scope = scope
    }
}

//struct AuthRestoreScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        AuthRestoreScreen()
//    }
//}
