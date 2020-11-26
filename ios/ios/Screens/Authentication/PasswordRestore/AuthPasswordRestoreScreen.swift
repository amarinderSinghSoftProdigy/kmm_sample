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
    let scope: ForgetPasswordScope
    
    var body: some View {
        switch scope {
        case is ForgetPasswordScope.PhoneNumberInput:
            if let scope = self.scope as? ForgetPasswordScope.PhoneNumberInput {
                AuthPhoneRequestScreen(scope: scope)
            }
                
        case is ForgetPasswordScope.AwaitVerification:
            if let scope = self.scope as? ForgetPasswordScope.AwaitVerification {
                AuthPhoneVerification(scope: scope)
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
