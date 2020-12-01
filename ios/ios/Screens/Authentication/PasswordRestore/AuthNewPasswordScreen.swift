//
//  AuthNewPasswordScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 27.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct AuthNewPasswordScreen: View {
    let scope: ForgetPasswordScope.EnterNewPassword
    
    @State var newPassword: String = ""
    @State var confirmationPassword: String = ""
    
    @State var canSubmitPassword: Bool = false
    
    @Binding var passwordUpdateFailed: Bool
    
    var body: some View {
        let errorMessageKey = scope.passwordValidation?.password ?? "something_went_wrong"
        
        VStack(spacing: 12) {
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: $newPassword,
                                           textValidator: checkPasswordsMatch,
                                           showPlaceholderWithText: true)
        
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                           text: $confirmationPassword,
                                           textValidator: checkPasswordsMatch,
                                           showPlaceholderWithText: true,
                                           errorMessageKey: "password_doesnt_match")
            
            MedicoButton(localizedStringKey: "confirm", isEnabled: canSubmitPassword) {
                scope.changePassword(newPassword: newPassword)
            }
        }
        .navigationBarTitle(LocalizedStringKey("new_password"), displayMode: .inline)
        .padding()
        
        .alert($passwordUpdateFailed,
               withTitleKey: "otp_error",
               withMessageKey: errorMessageKey,
               withButtonTextKey: "okay")
    }
    
    init(scope: ForgetPasswordScope.EnterNewPassword) {
        self.scope = scope
        
        self._passwordUpdateFailed = Binding.constant(scope.success.isFalse)
    }
    
    private func checkPasswordsMatch(_ password: String) -> Bool {
        if confirmationPassword.isEmpty {
            self.canSubmitPassword = false
            return true
        }
        
        let areEqual = confirmationPassword == newPassword
        
        self.canSubmitPassword = areEqual
        
        return areEqual
    }
}
