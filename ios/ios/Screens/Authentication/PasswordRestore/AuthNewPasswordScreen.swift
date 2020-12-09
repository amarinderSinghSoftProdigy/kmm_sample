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
    
    @ObservedObject var notification: SwiftDatasource<ScopeNotification>
    @ObservedObject var passwordValidation: SwiftDatasource<DataPasswordValidation>
    
    var body: some View {
        let errorMessageKey = self.passwordValidation.value?.password
        
        VStack(spacing: 12) {
            Spacer()
            
            let arePasswordsValid = confirmationPassword.isEmpty || canSubmitPassword
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: newPassword,
                                           onTextChange: { newValue in
                                            newPassword = newValue
                                            checkPasswordsMatch(newValue)
                                           },
                                           showPlaceholderWithText: true,
                                           isValid: errorMessageKey == nil && arePasswordsValid,
                                           errorMessageKey: errorMessageKey ?? "something_went_wrong")
                .textContentType(.newPassword)
        
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                           text: confirmationPassword,
                                           onTextChange: { newValue in
                                            confirmationPassword = newValue
                                            checkPasswordsMatch(newValue)
                                           },
                                           showPlaceholderWithText: true,
                                           isValid: arePasswordsValid,
                                           errorMessageKey: "password_doesnt_match")
            
            MedicoButton(localizedStringKey: "confirm", isEnabled: canSubmitPassword) {
                scope.changePassword(newPassword: newPassword)
            }
            
            Spacer()
        }
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("new_password"), displayMode: .inline)
        .padding()
        .notificationAlert(withHandler: scope) { _ = scope.finishResetPasswordFlow() }
    }
    
    init(scope: EnterNewPasswordScope) {
        self.scope = scope
        
        self.notification = SwiftDatasource(dataSource: scope.notifications)
        self.passwordValidation = SwiftDatasource(dataSource: scope.passwordValidation)
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
