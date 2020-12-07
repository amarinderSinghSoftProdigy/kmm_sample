//
//  SignUpTraderDetails.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpTraderDetails: View {
    let scope: SignUpScope.TraderData
    
    @ObservedObject var registration: SwiftDatasource<DataUserRegistration3>
    @ObservedObject var validation: SwiftDatasource<DataUserValidation3>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            self.traderDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: true, action: tryToSignUp))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("trader_details"), displayMode: .inline)
    }
    
    var traderDataFields: some View {
        VStack(spacing: 12) {
            ForEach(scope.inputFields, id: \.self) {
                getInputField(forType: $0)
            }
        }
        .modifier(ScrollViewModifier())
    }
    
    init(scope: SignUpScope.TraderData) {
        self.scope = scope
        
        self.registration = SwiftDatasource(dataSource: scope.registration)
        self.validation = SwiftDatasource(dataSource: scope.validation)
    }
    
    private func getInputField(forType type: SignUpScope.TraderDataFields) -> some View {
        switch type {
        
        case SignUpScope.TraderDataFields.tradeName:
            let tradeNameErrorMessageKey = self.validation.value?.tradeName
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "trade_name",
                                             text: self.registration.value?.tradeName,
                                             onTextChange: { newValue in scope.changeTradeName(tradeName: newValue) },
                                             isValid: tradeNameErrorMessageKey == nil,
                                             errorMessageKey: tradeNameErrorMessageKey)
                .disableAutocorrection(true)
                .autocapitalization(.words))
            
        case SignUpScope.TraderDataFields.pan:
            let panNumberErrorMessageKey = self.validation.value?.panNumber
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pan_number",
                                             text: self.registration.value?.panNumber,
                                             onTextChange: { newValue in scope.changePan(panNumber: newValue) },
                                             isValid: panNumberErrorMessageKey == nil,
                                             errorMessageKey: panNumberErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.TraderDataFields.gstin:
            let gstinErrorMessageKey = self.validation.value?.gstin
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "gstin",
                                             text: self.registration.value?.gstin,
                                             onTextChange: { newValue in scope.changeGstin(gstin: newValue) },
                                             isValid: gstinErrorMessageKey == nil,
                                             errorMessageKey: gstinErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.TraderDataFields.license1:
            let drugLicenseNo1ErrorMessageKey = self.validation.value?.drugLicenseNo1
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No1",
                                             text: self.registration.value?.drugLicenseNo1,
                                             onTextChange: { newValue in scope.changeDrugLicense1(drugLicenseNo: newValue) },
                                             isValid: drugLicenseNo1ErrorMessageKey == nil,
                                             errorMessageKey: drugLicenseNo1ErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.TraderDataFields.license2:
            let drugLicenseNo2ErrorMessageKey = self.validation.value?.drugLicenseNo2
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No2",
                                             text: self.registration.value?.drugLicenseNo2,
                                             onTextChange: { newValue in scope.changeDrugLicense2(drugLicenseNo: newValue) },
                                             isValid: drugLicenseNo2ErrorMessageKey == nil,
                                             errorMessageKey: drugLicenseNo2ErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        default:
            return AnyView(EmptyView())
        }
    }
    
    private func tryToSignUp() {
        guard let registration = self.registration.value else { return }
        
        scope.tryToSignUp(userRegistration: registration)
    }
}
