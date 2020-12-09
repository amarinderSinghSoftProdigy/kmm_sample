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
        let errorMessageKey = self.passwordValidation.value?.password ?? "something_went_wrong"
        
        VStack(spacing: 12) {
            let arePasswordsValid = confirmationPassword.isEmpty || canSubmitPassword
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: newPassword,
                                           onTextChange: { newValue in
                                            newPassword = newValue
                                            checkPasswordsMatch(newValue)
                                           },
                                           showPlaceholderWithText: true,
                                           isValid: arePasswordsValid,
                                           errorMessageKey: errorMessageKey)
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
        }
        .navigationBarTitle(LocalizedStringKey("new_password"), displayMode: .inline)
        .padding()
        
        .modifier(NotificationAlertHandler(notification: notification))
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
