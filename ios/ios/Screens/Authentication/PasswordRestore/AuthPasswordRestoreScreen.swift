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
        Background {
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                    
                    getCurrentView(with: geometry)
                }
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
