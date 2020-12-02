//
//  SignUpPersonalDataScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 2.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpPersonalDataScreen: View {
    let scope: SignUpScope.PersonalData
    
    @ObservedObject var registration: SwiftDatasource<DataUserRegistration1>
    @ObservedObject var validation: SwiftDatasource<DataUserValidation1>
    
//    @State private var firstName: String
//    @State private var lastName: String
//
//    @State private var email: String
//    @State private var phone: String
//
//    @State private var password: String = ""
//    @State private var repeatPassword: String = ""
    
    var body: some View {
        VStack(spacing: 32) {
            self.personalDataFields
            
            self.termsOfConditionsAndPrivacyPolicyLink
        }
        .modifier(SignUpButton(isEnabled: true, action: goToAddress))
        .navigationBarTitle(LocalizedStringKey("personal_data"), displayMode: .inline)
    }
    
    var personalDataFields: some View {
        VStack(spacing: 12) {
//            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name", text: self.registration.$value?.firstName)
//            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name", text: self.registration.$value?.lastName)
//
//            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "email_address", text: self.registration.$value?.email)
//            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number", text: self.registration.$value?.phone)
//
//            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password", text: self.registration.$value?.password)
//            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "repeat_password", text: self.registration.$value?.repeatPassword)
        }
    }
    
    var termsOfConditionsAndPrivacyPolicyLink: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(LocalizedStringKey("continueing_i_accept"))
                .modifier(MedicoText(fontSize: 12, color: .textGrey))
            
            Text(LocalizedStringKey("terms_of_conditions_and_privacy_policy"))
                .underline()
                .modifier(MedicoText(textWeight: .semiBold, fontSize: 12, color: .lightBlue))
                .onTapGesture {
                    print("Go to the Terms of Conditions and Privacy Policy")
                }
                
        }
    }
    
    init(scope: SignUpScope.PersonalData) {
        self.scope = scope
        
        self.registration = SwiftDatasource(dataSource: scope.registration)
        self.validation = SwiftDatasource(dataSource: scope.validation)
    }
    
    private func goToAddress() {
        guard let userRegistration = self.registration.value else { return }
        
        scope.tryToSignUp(userRegistration: userRegistration)
    }
}
