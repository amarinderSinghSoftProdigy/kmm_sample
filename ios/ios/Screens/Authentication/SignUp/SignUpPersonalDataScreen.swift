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
    
    var body: some View {
        VStack(alignment: .leading, spacing: 32) {
            self.personalDataFields
            
            self.termsOfConditionsAndPrivacyPolicyLink
        }
        .modifier(SignUpButton(isEnabled: true, action: goToAddress))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("personal_data"), displayMode: .inline)
    }
    
    var personalDataFields: some View {
        VStack(spacing: 12) {
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name",
                                         text: self.registration.value?.firstName,
                                         onTextChange: { newValue in scope.changeFirstName(firstName: newValue) })
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name",
                                         text: self.registration.value?.lastName,
                                         onTextChange: { newValue in scope.changeLastName(lastName: newValue) })

            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "email_address",
                                         text: self.registration.value?.email,
                                         onTextChange: { newValue in scope.changeEmail(email: newValue) },
                                         errorMessageKey: self.validation.value?.email)
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                         text: self.registration.value?.phoneNumber,
                                         onTextChange: { newValue in scope.changePhoneNumber(phoneNumber: newValue) },
                                         errorMessageKey: self.validation.value?.phoneNumber)

            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password",
                                           text: self.registration.value?.password,
                                           onTextChange: { newValue in scope.changePassword(password: newValue) },
                                           errorMessageKey: self.validation.value?.password)
            
            let isRepeatPasswordValid = registration.value?.password.isEmpty == true ||
                registration.value?.password == registration.value?.verifyPassword
            let errorMessageKey: String? = !isRepeatPasswordValid ? "password_doesnt_match" : nil
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "repeat_password",
                                           text: self.registration.value?.verifyPassword,
                                           onTextChange: { newValue in scope.changeRepeatPassword(repeatPassword: newValue) },
                                           errorMessageKey: errorMessageKey)
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
                    showTermsOfConditionsAndPrivacyPolicy()
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
    
    private func showTermsOfConditionsAndPrivacyPolicy() {
        if let link = Bundle.main.object(forInfoDictionaryKey: "AppTermsOfConditionsAndPrivacyPolicyLink") as? String,
            let url = URL(string: link) {
            UIApplication.shared.open(url)
        }
    }
}
