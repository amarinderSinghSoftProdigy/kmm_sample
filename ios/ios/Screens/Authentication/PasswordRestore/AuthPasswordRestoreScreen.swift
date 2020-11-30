//
//  AuthRestoreScreen.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import core_arm64

struct AuthPasswordRestoreScreen: View {
    let scope: ForgetPasswordScope
    
    var body: some View {
        Background {
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                    
                    getCurrentView(with: geometry)
                }
                .backButton { scope.goBack() }
            }
        }
    }
    
    func getCurrentView(with geometry: GeometryProxy) ->  some View {
        switch scope {
        case is ForgetPasswordScope.PhoneNumberInput:
            if let scope = self.scope as? ForgetPasswordScope.PhoneNumberInput {
                return AnyView(AuthPhoneRequestScreen(scope: scope, geometry: geometry))
            }
                
        case is ForgetPasswordScope.AwaitVerification:
            if let scope = self.scope as? ForgetPasswordScope.AwaitVerification {
                return AnyView(AuthPhoneVerification(scope: scope, geometry: geometry))
            }
            
        case is ForgetPasswordScope.EnterNewPassword:
            if let scope = self.scope as? ForgetPasswordScope.EnterNewPassword {
                return AnyView(AuthNewPasswordScreen(scope: scope))
            }
            
        default:
            break
        }
        
        return AnyView(Group {})
    }
}

//struct AuthRestoreScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        AuthRestoreScreen()
//    }
//}
