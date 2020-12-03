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
        GeometryReader { geometry in
            ZStack {
                AppColor.primary.color.edgesIgnoringSafeArea(.all)
                
                getCurrentView(with: geometry)
            }
            .backButton { scope.goBack() }
        }
        .hideKeyboardOnTap()
        .keyboardResponder()
    }
    
    func getCurrentView(with geometry: GeometryProxy) ->  some View {
        switch scope {
        
        case let scope as ForgetPasswordScope.PhoneNumberInput:
            return AnyView(AuthPhoneRequestScreen(scope: scope, geometry: geometry))
                
        case let scope as ForgetPasswordScope.AwaitVerification:
            return AnyView(AuthPhoneVerification(scope: scope, geometry: geometry))
            
        case let scope as ForgetPasswordScope.EnterNewPassword:
            return AnyView(AuthNewPasswordScreen(scope: scope))
            
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
