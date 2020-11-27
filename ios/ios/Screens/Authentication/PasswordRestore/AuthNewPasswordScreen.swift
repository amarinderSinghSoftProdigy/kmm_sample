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
        VStack(spacing: 12) {
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: $newPassword,
                                           showPlaceholderWithText: true)
        
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                           text: $confirmationPassword,
                                           textValidator: checkPasswordsMatch,
                                           showPlaceholderWithText: true)
            
            MedicoButton(action: {
                scope.changePassword(newPassword: newPassword)
            }, localizedStringKey: "confirm", isEnabled: canSubmitPassword)
        }
        .navigationBarTitle(LocalizedStringKey("new_password"), displayMode: .inline)
        .padding()
        
        .alert($passwordUpdateFailed,
               withTitleKey: "otp_error",
               withMessageKey: "something_went_wrong",
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
