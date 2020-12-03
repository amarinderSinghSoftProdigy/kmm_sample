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
    
    @State var isPhoneValid: Bool = true
    @State var inputFieldsHeight: CGFloat = 350
    
    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            self.personalDataFields
            
            self.termsOfConditionsAndPrivacyPolicyLink
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: true, action: goToAddress))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("personal_data"), displayMode: .inline)
    }
    
    var personalDataFields: some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack(spacing: 12) {
                let firstName = self.registration.value?.firstName
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name",
                                             text: firstName,
                                             onTextChange: { newValue in scope.changeFirstName(firstName: newValue) },
                                             isValid: firstName?.isEmpty == false,
                                             errorMessageKey: "required_field")
                    .disableAutocorrection(true)
                    .textContentType(.givenName)
                    .autocapitalization(.words)
                
                let lastName = self.registration.value?.lastName
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name",
                                             text: lastName,
                                             onTextChange: { newValue in scope.changeLastName(lastName: newValue) },
                                             isValid: lastName?.isEmpty == false,
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
                
                PhoneTextField(phone: self.validation.value?.phoneNumber,
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
                
                let isRepeatPasswordValid = registration.value?.password.isEmpty == true ||
                    registration.value?.password == registration.value?.verifyPassword
                let errorMessageKey: String? = !isRepeatPasswordValid ? "password_doesnt_match" : nil
                FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "repeat_password",
                                               text: self.registration.value?.verifyPassword,
                                               onTextChange: { newValue in scope.changeRepeatPassword(repeatPassword: newValue) },
                                               isValid: isRepeatPasswordValid,
                                               errorMessageKey: errorMessageKey)
            }
            .background(GeometryReader { gp -> Color in
                let frame = gp.frame(in: .local)
                DispatchQueue.main.async {
                    self.inputFieldsHeight = frame.height
                }
                return Color.clear
            })
        }.frame(maxHeight: inputFieldsHeight)
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
        
        self._isPhoneValid = State(initialValue: self.validation.value?.phoneNumber == nil)
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
