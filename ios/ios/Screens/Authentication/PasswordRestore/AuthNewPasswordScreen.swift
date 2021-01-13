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
    let scope: EnterNewPasswordScope
    
    @State var newPassword: String = ""
    @State var confirmationPassword: String = ""
    
    @State var canSubmitPassword: Bool = false
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    @ObservedObject var passwordValidation: SwiftDataSource<DataPasswordValidation>
    
    var body: some View {
        let errorMessageKey = self.passwordValidation.value?.password
        
        VStack(spacing: 12) {
            Spacer()
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: newPassword,
                                           onTextChange: { newValue in
                                            newPassword = newValue
                                            checkPasswordsMatch(newValue)
                                           },
                                           showPlaceholderWithText: true,
                                           isValid: errorMessageKey == nil && canSubmitPassword,
                                           errorMessageKey: errorMessageKey ?? "something_went_wrong")
                .textContentType(.newPassword)
        
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                           text: confirmationPassword,
                                           onTextChange: { newValue in
                                            confirmationPassword = newValue
                                            checkPasswordsMatch(newValue)
                                           },
                                           showPlaceholderWithText: true,
                                           isValid: canSubmitPassword,
                                           errorMessageKey: "password_doesnt_match")
            
            MedicoButton(localizedStringKey: "confirm", isEnabled: canSubmitPassword) {
                scope.changePassword(newPassword: newPassword)
            }
            
            Spacer()
        }
        .keyboardResponder()
        .padding()
        .notificationAlert(withHandler: scope) { _ = scope.finishResetPasswordFlow() }
        .screenLogger(withScreenName: "AuthNewPasswordScreen",
                          withScreenClass: AuthNewPasswordScreen.self)
    }
    
    init(scope: EnterNewPasswordScope) {
        self.scope = scope
        
        self.notification = SwiftDataSource(dataSource: scope.notifications)
        self.passwordValidation = SwiftDataSource(dataSource: scope.passwordValidation)
    }
    
    private func checkPasswordsMatch(_ password: String) {
        if confirmationPassword.isEmpty {
            self.canSubmitPassword = false
            return
        }
        
        let areEqual = confirmationPassword == newPassword
        
        self.canSubmitPassword = areEqual
    }
}
