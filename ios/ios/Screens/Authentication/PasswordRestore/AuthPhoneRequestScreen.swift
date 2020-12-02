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
    let geometry: GeometryProxy
    
    @State var phone: String
    @State var canSubmitPhone = false
    
    @Binding var isOtpSendFailed: Bool
    
    var body: some View {
        VStack {
            Text(LocalizedStringKey("reset_password_hint"))
                .modifier(MedicoText(textWeight: .medium, color: .textGrey))
                .multilineTextAlignment(.center)
                .padding([.trailing, .leading], geometry.size.width * 0.15)
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                         text: phone,
                                         onTextChange: { newValue in checkPhoneNumber(newValue) },
                                         textFormatter: { value in self.phone },
                                         keyboardType: .phonePad,
                                         isValid: canSubmitPhone)
                .padding([.top, .bottom])
            
            MedicoButton(localizedStringKey: "get_code", isEnabled: canSubmitPhone) {
                let rawPhoneNumber = PhoneNumberUtil.shared.getRawPhoneNumber(phone)
                scope.sendOtp(phoneNumber: rawPhoneNumber)
            }
        }
        .navigationBarTitle(LocalizedStringKey("password_reset"), displayMode: .inline)
        .padding()
        
        .alert($isOtpSendFailed,
               withTitleKey: "otp_error",
               withMessageKey: "something_went_wrong",
               withButtonTextKey: "okay")
    }
    
    init(scope: ForgetPasswordScope.PhoneNumberInput, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        _phone = State(initialValue: scope.phoneNumber)
        
        _isOtpSendFailed = Binding.constant(scope.success.isFalse)
    }
    
    private func checkPhoneNumber(_ phone: String) {
        let possibleNumber = PhoneNumberUtil.shared.isValidNumber(phone)
        
        self.phone = possibleNumber.formattedNumber
        self.canSubmitPhone = possibleNumber.isValid
    }
}
