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
    
    @ObservedObject var phone: SwiftDatasource<NSString>
    @ObservedObject var error: SwiftDatasource<DataErrorCode>
    
    @State var canSubmitPhone = false
    
    var body: some View {
        self.getView()
    }
    
    init(scope: OtpScope.PhoneNumberInput, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        self.phone = SwiftDatasource(dataSource: scope.phoneNumber)
        self.error = SwiftDatasource(dataSource: scope.errors)
    }
    
    private func getView() -> some View {
        guard let phoneValue = self.phone.value else { return AnyView(EmptyView()) }
        
        let phone = phoneValue as String
        
        return AnyView(
            VStack {
                Spacer()
                
                Text(LocalizedStringKey("reset_password_hint"))
                    .modifier(MedicoText(textWeight: .medium, color: .textGrey))
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
