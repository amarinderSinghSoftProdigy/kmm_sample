//
//  AuthRestoreScreen.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import core

struct OtpFlowScreen: View {
    let scope: OtpScope
    
    var body: some View {
        GeometryReader { geometry in
            getCurrentView(with: geometry)
        }
        .keyboardResponder()
    }
    
    func getCurrentView(with geometry: GeometryProxy) ->  some View {
        switch scope {
        
        case let scope as OtpScope.PhoneNumberInput:
            return AnyView(OtpPhoneRequestScreen(scope: scope, geometry: geometry))
                
        case let scope as OtpScope.AwaitVerification:
            return AnyView(OtpPhoneVerification(scope: scope, geometry: geometry))
            
        default:
            return AnyView(EmptyView())
        }
    }
}
