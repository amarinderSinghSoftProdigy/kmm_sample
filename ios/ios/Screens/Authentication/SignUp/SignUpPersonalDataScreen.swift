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
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var registration: SwiftDataSource<DataUserRegistration1>
    @ObservedObject var validation: SwiftDataSource<DataUserValidation1>
    
    @State var isPhoneValid: Bool = true
    
    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            self.personalDataFields
            
            self.termsOfConditionsAndPrivacyPolicyLink
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               action: goToAddress))
        .keyboardResponder()
        .screenLogger(withScreenName: "SignUpPersonalDataScreen",
                      withScreenClass: SignUpPersonalDataScreen.self)
    }
    
    var personalDataFields: some View {
        VStack(spacing: 12) {
            let firstName = self.registration.value?.firstName
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name",
                                         text: firstName,
                                         onTextChange: { newValue in scope.changeFirstName(firstName: newValue) },
                                         isValid: self.validation.value == nil ||
                                            firstName?.isEmpty == false,
                                         errorMessageKey: "required_field")
                .disableAutocorrection(true)
                .textContentType(.givenName)
                .autocapitalization(.words)
            
            let lastName = self.registration.value?.lastName
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name",
                                         text: lastName,
                                         onTextChange: { newValue in scope.changeLastName(lastName: newValue) },
                                         isValid: self.validation.value == nil ||
                                            lastName?.isEmpty == false,
                                         errorMessageKey: "required_field")
                .disableAutocorrection(true)
                .textContentType(.familyName)
                .autocapitalization(.words)

            let emailErrorMessageKey = self.validation.value?.email
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "email_address",
                                         text: self.registration.value?.email,
                                         onTextChange: { newValue in scope.changeEmail(email: newValue) },
                                         keyboardType: .emailAddress,
                                         isValid: emailErrorMessageKey == nil,
                                         errorMessageKey: emailErrorMessageKey)
                .textContentType(.emailAddress)
                .autocapitalization(.none)
            
            PhoneTextField(phone: self.registration.value?.phoneNumber,
                           canSubmitPhone: $isPhoneValid,
                           errorMessageKey: self.validation.value?.phoneNumber) { newValue in
                scope.changePhoneNumber(phoneNumber: newValue)
            }
            
            let passwordErrorMessageKey = self.validation.value?.password
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password",
                                           text: self.registration.value?.password,
                                           onTextChange: { newValue in scope.changePassword(password: newValue) },
                                           isValid: passwordErrorMessageKey == nil,
                                           errorMessageKey: passwordErrorMessageKey)
                .textContentType(.newPassword)
            
            let isRepeatPasswordValid = registration.value?.password == registration.value?.verifyPassword
            let errorMessageKey: String? = !isRepeatPasswordValid ? "password_doesnt_match" : nil
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "repeat_password",
                                           text: self.registration.value?.verifyPassword,
                                           onTextChange: { newValue in scope.changeRepeatPassword(repeatPassword: newValue) },
                                           isValid: isRepeatPasswordValid,
                                           errorMessageKey: errorMessageKey)
        }
        .scrollView()
    }
    
    var termsOfConditionsAndPrivacyPolicyLink: some View {
        VStack(alignment: .leading, spacing: 3) {
            LocalizedText(localizationKey: "continueing_i_accept",
                          fontSize: 12,
                          color: .textGrey)
            
            LocalizedText(localizationKey: "terms_of_conditions_and_privacy_policy",
                          textWeight: .semiBold,
                          fontSize: 12,
                          color: .lightBlue,
                          underlined: true)
                .onTapGesture {
                    showTermsOfConditionsAndPrivacyPolicy()
                }
        }
    }
    
    init(scope: SignUpScope.PersonalData) {
        self.scope = scope
        
        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        
        self.registration = SwiftDataSource(dataSource: scope.registration)
        self.validation = SwiftDataSource(dataSource: scope.validation)
        
        self._isPhoneValid = State(initialValue: self.validation.value?.phoneNumber == nil)
    }
    
    private func goToAddress() {
        guard let userRegistration = self.registration.value else { return }
        
        _ = scope.validate(userRegistration: userRegistration)
    }
    
    private func showTermsOfConditionsAndPrivacyPolicy() {
        if let link = Bundle.main.object(forInfoDictionaryKey: "AppTermsOfConditionsAndPrivacyPolicyLink") as? String,
            let url = URL(string: link) {
            UIApplication.shared.open(url)
        }
    }
}
