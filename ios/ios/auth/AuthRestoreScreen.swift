//
//  AuthRestoreScreen.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import core

struct AuthPhoneNumberInputScreen: View {
    let authViewModel: AuthViewModel
    let scope: Scope.ForgetPassword.ForgetPasswordPhoneNumberInput
    @State var phone: String = ""
    
    init(authViewModel: AuthViewModel, scope: Scope.ForgetPassword.ForgetPasswordPhoneNumberInput) {
        self.authViewModel = authViewModel
        self.scope = scope
        UINavigationBar.appearance().barTintColor = UIColor(named: "Primary2")
        UINavigationBar.appearance().titleTextAttributes = [.foregroundColor : UIColor(named: "Secondary")]
    }
    
    var body: some View {
        ZStack {
            Color.primary.edgesIgnoringSafeArea(.all)
            VStack {
                Text(LocalizedStringKey("reset_password_hint"))
                    .font(Font.system(size: 14))
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                TextField(LocalizedStringKey("phone_number"), text: $phone)
                    .authInputField()
                    .padding([.top, .bottom])
                
                Button(action: {
                    authViewModel.sendOtp(phoneNumber: phone)
                }) {
                    Text(LocalizedStringKey("get_code"))
                        .fontWeight(Font.Weight.semibold)
                        .frame(maxWidth: .infinity)
                }.medicoButton(isEnabled: true)
            }.padding()
        }
    }
}

//struct AuthRestoreScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        AuthRestoreScreen()
//    }
//}
