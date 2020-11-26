//
//  AuthPhoneRequestScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct AuthPhoneRequestScreen: View {
    let scope: ForgetPasswordScope.PhoneNumberInput
    
    @State var phone: String = ""
    
    var body: some View {
        Background {
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                    VStack {
                        Text(LocalizedStringKey("reset_password_hint"))
                            .font(.custom("Barlow-Regular", size: 14))
                            .foregroundColor(appColor: .textGrey)
                            .multilineTextAlignment(.center)
                            .padding([.trailing, .leading], geometry.size.width * 0.15)
                        
                        FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                                     text: $phone,
                                                     textValidator: checkPhoneNumber,
                                                     keyboardType: .phonePad)
                            .padding([.top, .bottom])
                        
                        Button(action: {
                            scope.sendOtp(phoneNumber: phone)
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
    }
    
    init(scope: ForgetPasswordScope.PhoneNumberInput) {
        self.scope = scope
    }
    
    private func checkPhoneNumber(_ phone: String) -> Bool {
        let possibleNumber = PhoneNumberUtil.shared.isValidNumber(phone)
        
        self.phone = possibleNumber.formattedNumber
        
        return possibleNumber.isValid
    }
}
