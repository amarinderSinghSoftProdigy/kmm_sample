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
    let scope: SignUpScope.Details.DetailsTraderData
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var registration: SwiftDataSource<DataUserRegistration3>
    @ObservedObject var validation: SwiftDataSource<DataUserValidation3>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            self.traderDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               action: tryToSignUp))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("trader_details"), displayMode: .inline)
    }
    
    var traderDataFields: some View {
        VStack(spacing: 12) {
            ForEach(scope.inputFields, id: \.self) {
                getInputField(forType: $0)
            }
        }
        .scrollView()
    }
    
    init(scope: SignUpScope.Details.DetailsTraderData) {
        self.scope = scope
        
        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        
        self.registration = SwiftDataSource(dataSource: scope.registration)
        self.validation = SwiftDataSource(dataSource: scope.validation)
    }
    
    private func getInputField(forType type: SignUpScope.Details.DetailsFields) -> some View {
        switch type {
        
        case SignUpScope.Details.DetailsFields.tradeName:
            let tradeNameErrorMessageKey = self.validation.value?.tradeName
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "trade_name",
                                             text: self.registration.value?.tradeName,
                                             onTextChange: { newValue in scope.changeTradeName(tradeName: newValue) },
                                             isValid: tradeNameErrorMessageKey == nil,
                                             errorMessageKey: tradeNameErrorMessageKey)
                .disableAutocorrection(true)
                .autocapitalization(.words))
            
        case SignUpScope.Details.DetailsFields.pan:
            let panNumberErrorMessageKey = self.validation.value?.panNumber
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pan_number",
                                             text: self.registration.value?.panNumber,
                                             onTextChange: { newValue in scope.changePan(panNumber: newValue) },
                                             isValid: panNumberErrorMessageKey == nil,
                                             errorMessageKey: panNumberErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.Details.DetailsFields.gstin:
            let gstinErrorMessageKey = self.validation.value?.gstin
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "gstin",
                                             text: self.registration.value?.gstin,
                                             onTextChange: { newValue in scope.changeGstin(gstin: newValue) },
                                             isValid: gstinErrorMessageKey == nil,
                                             errorMessageKey: gstinErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.Details.DetailsFields.license1:
            let drugLicenseNo1ErrorMessageKey = self.validation.value?.drugLicenseNo1
            return AnyView(
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No1",
                                             text: self.registration.value?.drugLicenseNo1,
                                             onTextChange: { newValue in scope.changeDrugLicense1(drugLicenseNo: newValue) },
                                             isValid: drugLicenseNo1ErrorMessageKey == nil,
                                             errorMessageKey: drugLicenseNo1ErrorMessageKey)
                    .disableAutocorrection(true)
                    .autocapitalization(.none))
            
        case SignUpScope.Details.DetailsFields.license2:
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
        
        _ = scope.validate(userRegistration: registration)
    }
}
