//
//  PasswordScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 27.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct PasswordScreen: View {
    let scope: PasswordScope
    
    var body: some View {
        let scopeSpecificView: AnyView
        let textKey: String
        
        switch self.scope {
        case let scope as PasswordScope.VerifyCurrent:
            textKey = "enter_current_password"
            scopeSpecificView = AnyView(VerifyCurrentPasswordScreen(scope: scope))
            
        case let scope as PasswordScope.EnterNew:
            textKey = "enter_new_password"
            scopeSpecificView = AnyView(EnterNewPasswordScreen(scope: scope))
            
        default:
            textKey = ""
            scopeSpecificView = AnyView(EmptyView())
        }
        
        return AnyView(
            VStack(spacing: 32) {
                Spacer()
                
                LocalizedText(localizationKey: textKey,
                              textWeight: .medium,
                              color: .textGrey)
                
                VStack(spacing: 12) {
                    scopeSpecificView
                }
                
                Spacer()
            }
            .padding()
            .textFieldsModifiers()
        )
    }
}

private struct VerifyCurrentPasswordScreen: View {
    let scope: PasswordScope.VerifyCurrent
    
    @ObservedObject var currentPassword: SwiftDataSource<NSString>
    @ObservedObject var passwordValidation: SwiftDataSource<DataPasswordValidation>
    
    var body: some View {
        let errorMessageKey = self.passwordValidation.value?.password
        let currentPassword = self.currentPassword.value as String?
                
        Group {
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "current_password",
                                           text: currentPassword,
                                           onTextChange: { newValue in scope.changePassword(password: newValue) },
                                           isValid: errorMessageKey == nil,
                                           errorMessageKey: errorMessageKey ?? "something_went_wrong")
            
            MedicoButton(localizedStringKey: "confirm",
                         isEnabled: currentPassword?.isEmpty == false) {
                scope.submit()
            }
        }
        .screenLogger(withScreenName: "PasswordScreen.VerifyCurrent",
                      withScreenClass: VerifyCurrentPasswordScreen.self)
    }
    
    init(scope: PasswordScope.VerifyCurrent) {
        self.scope = scope
        
        self.currentPassword = SwiftDataSource(dataSource: scope.password)
        self.passwordValidation = SwiftDataSource(dataSource: scope.passwordValidation)
    }
}

private struct EnterNewPasswordScreen: View {
    let scope: PasswordScope.EnterNew
    
    @ObservedObject var newPassword: SwiftDataSource<NSString>
    @ObservedObject var confirmationPassword: SwiftDataSource<NSString>
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    @ObservedObject var passwordValidation: SwiftDataSource<DataPasswordValidation>
    
    var body: some View {
        let errorMessageKey = self.passwordValidation.value?.password
        
        let newPassword = self.newPassword.value as String?
        let confirmationPassword = self.confirmationPassword.value as String?
        
        let canSubmitPassword = newPassword != nil &&
            confirmationPassword != nil &&
            newPassword?.isEmpty == false &&
            confirmationPassword?.isEmpty == false &&
            newPassword == confirmationPassword
        
        Group {
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                           text: newPassword,
                                           onTextChange: { newValue in scope.changePassword(password: newValue) },
                                           showPlaceholderWithText: true,
                                           isValid: errorMessageKey == nil,
                                           errorMessageKey: errorMessageKey ?? "something_went_wrong")
                .textContentType(.newPassword)
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                           text: confirmationPassword,
                                           onTextChange: { newValue in scope.changeConfirmPassword(password: newValue) },
                                           showPlaceholderWithText: true,
                                           isValid: canSubmitPassword,
                                           errorMessageKey: "password_doesnt_match")
            
            MedicoButton(localizedStringKey: "confirm", isEnabled: canSubmitPassword) {
                scope.submit()
            }
        }
        .notificationAlertSender(withHandler: scope) { _ = scope.finishPasswordFlow() }
        .screenLogger(withScreenName: "PasswordScreen.EnterNew",
                      withScreenClass: EnterNewPasswordScreen.self)
    }
    
    init(scope: PasswordScope.EnterNew) {
        self.scope = scope
        
        self.newPassword = SwiftDataSource(dataSource: scope.password)
        self.confirmationPassword = SwiftDataSource(dataSource: scope.confirmPassword)
        
        self.notification = SwiftDataSource(dataSource: scope.notifications)
        self.passwordValidation = SwiftDataSource(dataSource: scope.passwordValidation)
    }
}
