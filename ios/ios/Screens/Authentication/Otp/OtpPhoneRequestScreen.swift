//
//  AuthPhoneRequestScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct OtpPhoneRequestScreen: View {
    let scope: OtpScope.PhoneNumberInput
    let geometry: GeometryProxy
    
    @ObservedObject var phone: SwiftDataSource<NSString>
    
    @State var canSubmitPhone = false
    
    var body: some View {
        self.getView()
            .screenLogger(withScreenName: "OtpPhoneRequestScreen",
                          withScreenClass: OtpPhoneRequestScreen.self)
    }
    
    init(scope: OtpScope.PhoneNumberInput, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        self.phone = SwiftDataSource(dataSource: scope.phoneNumber)
    }
    
    private func getView() -> some View {
        guard let phoneValue = self.phone.value else { return AnyView(EmptyView()) }
        
        let phone = phoneValue as String
        
        return AnyView(
            VStack {
                Spacer()
                
                LocalizedText(localizationKey: "reset_password_hint",
                              textWeight: .medium,
                              color: .textGrey)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, geometry.size.width * 0.15)
                
                PhoneTextField(phone: phone, canSubmitPhone: $canSubmitPhone) { newValue in
                    scope.changePhoneNumber(phoneNumber: newValue)
                }
                .padding(.vertical)
                
                MedicoButton(localizedStringKey: "get_code", isEnabled: canSubmitPhone) {
                    scope.sendOtp(phoneNumber: phone)
                }
                
                Spacer()
            }
            .padding()
        )
    }
}
