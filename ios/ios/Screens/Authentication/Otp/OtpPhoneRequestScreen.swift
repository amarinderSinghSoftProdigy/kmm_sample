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
    @ObservedObject var error: SwiftDataSource<DataErrorCode>
    
    @State var canSubmitPhone = false
    
    var body: some View {
        self.getView()
    }
    
    init(scope: OtpScope.PhoneNumberInput, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        self.phone = SwiftDataSource(dataSource: scope.phoneNumber)
        self.error = SwiftDataSource(dataSource: scope.errors)
    }
    
    private func getView() -> some View {
        guard let phoneValue = self.phone.value else { return AnyView(EmptyView()) }
        
        let phone = phoneValue as String
        
        return AnyView(
            VStack {
                Spacer()
                
                LocalizedText(localizedStringKey: "reset_password_hint",
                              textWeight: .medium,
                              color: .textGrey)
                    .multilineTextAlignment(.center)
                    .padding([.trailing, .leading], geometry.size.width * 0.15)
                
                PhoneTextField(phone: phone, canSubmitPhone: $canSubmitPhone) { newValue in
                    scope.changePhoneNumber(phoneNumber: newValue)
                }
                .padding([.top, .bottom])
                
                MedicoButton(localizedStringKey: "get_code", isEnabled: canSubmitPhone) {
                    scope.sendOtp(phoneNumber: phone)
                }
                
                Spacer()
            }
            .navigationBarTitle(LocalizedStringKey("password_reset"), displayMode: .inline)
            .padding()
        )
    }
}
