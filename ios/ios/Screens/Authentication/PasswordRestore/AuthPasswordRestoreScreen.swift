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
    let scope: Scope
    
    var body: some View {
        switch scope {
        case is Scope.ForgetPassword.ForgetPasswordPhoneNumberInput:
            if let scope = self.scope as? Scope.ForgetPassword.ForgetPasswordPhoneNumberInput {
                AuthPhoneRequestScreen(authViewModel: authViewModel, scope: scope)
            }
                
        case is Scope.ForgetPassword.ForgetPasswordAwaitVerification:
            if let scope = self.scope as? Scope.ForgetPassword.ForgetPasswordAwaitVerification {
                AuthPhoneVerification(authViewModel: authViewModel, scope: scope)
            }
            
        default:
            Group {}
        }
    }
}

//struct AuthRestoreScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        AuthRestoreScreen()
//    }
//}
