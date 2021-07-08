//
//  AddRetailerScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 10.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct AddRetailerScreen: View {
    let scope: ManagementScope.AddRetailer
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        let currentView: AnyView
        let titleKey: String
        let buttonTextKey: String
        let screenName: String
        
        switch self.scope {
        
        case let scope as ManagementScope.AddRetailerTraderDetails:
            currentView = AnyView(TraderDetailsScreen(scope: scope))
            titleKey = "add_retailer"
            buttonTextKey = "next"
            screenName = "TraderDetails"
            
        case let scope as ManagementScope.AddRetailerAddress:
            currentView = AnyView(AddressScreen(scope: scope))
            titleKey = "add_retailer_address"
            buttonTextKey = "add_retailer"
            screenName = "Address"
            
        default:
            currentView = AnyView(EmptyView())
            titleKey = ""
            buttonTextKey = ""
            screenName = ""
            
        }
        
        return AnyView(
            VStack(alignment: .leading, spacing: 16) {
                LocalizedText(localizationKey: titleKey,
                              textWeight: .semiBold,
                              fontSize: 16,
                              multilineTextAlignment: .leading)
                
                currentView
            }
            .scrollView()
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
            .padding(.top, 16)
            .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                                       buttonTextKey: buttonTextKey,
                                       action: { scope.next() }))
            .textFieldsModifiers()
            .screenLogger(withScreenName: "ManagementScope.AddRetailer.\(screenName)",
                          withScreenClass: AddRetailerScreen.self)
        )
    }
    
    var currentScreen: some View {
        Group {
            switch self.scope {
            
            case let scope as ManagementScope.AddRetailerTraderDetails:
                TraderDetailsScreen(scope: scope)
                
            default:
                EmptyView()
                
            }
        }
    }
    
    init(scope: ManagementScope.AddRetailer) {
        self.scope = scope
        
        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
    }
    
    private struct TraderDetailsScreen: View {
        let scope: ManagementScope.AddRetailerTraderDetails
        
        @ObservedObject var isTermsAccepted: SwiftDataSource<KotlinBoolean>
        
        @ObservedObject var registration: SwiftDataSource<DataUserRegistration3>
        @ObservedObject var validation: SwiftDataSource<DataUserValidation3>
        
        var body: some View {
            Group {
                VStack(spacing: 12) {
                    let tradeNameErrorMessageKey = self.validation.value?.tradeName
                    FloatingPlaceholderTextField(placeholderLocalizedStringKey: "trade_name",
                                                 text: registration.value?.tradeName,
                                                 onTextChange: { scope.changeTradeName(tradeName: $0) },
                                                 isValid: tradeNameErrorMessageKey == nil,
                                                 errorMessageKey: tradeNameErrorMessageKey,
                                                 disableAutocorrection: true,
                                                 autocapitalization: .words)
                    
                    GstinOrPanRequiredWarningView()
                    
                    let gstinErrorMessageKey = self.validation.value?.gstin
                    let isGstinValid = Validator.TraderDetails().isGstinValid(gstin: self.registration.value?.gstin ?? "")
                    FloatingPlaceholderTextField(placeholderLocalizedStringKey: "gstin",
                                                 text: self.registration.value?.gstin,
                                                 onTextChange: { scope.changeGstin(gstin: $0) },
                                                 isValid: gstinErrorMessageKey == nil && isGstinValid,
                                                 errorMessageKey: gstinErrorMessageKey,
                                                 disableAutocorrection: true,
                                                 autocapitalization: .none)
                    
                    let panErrorMessageKey = self.validation.value?.panNumber
                    FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pan_number",
                                                 text: self.registration.value?.panNumber,
                                                 onTextChange: { scope.changePan(panNumber: $0) },
                                                 isValid: panErrorMessageKey == nil,
                                                 errorMessageKey: panErrorMessageKey,
                                                 disableAutocorrection: true,
                                                 autocapitalization: .none)
                    
                    let drugLicenseNo1ErrorMessageKey = self.validation.value?.drugLicenseNo1
                    FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No1",
                                                 text: self.registration.value?.drugLicenseNo1,
                                                 onTextChange: { scope.changeDrugLicense1(drugLicenseNo: $0) },
                                                 constText: "20B",
                                                 isValid: drugLicenseNo1ErrorMessageKey == nil,
                                                 errorMessageKey: drugLicenseNo1ErrorMessageKey,
                                                 disableAutocorrection: true,
                                                 autocapitalization: .none)
                    
                    let drugLicenseNo2ErrorMessageKey = self.validation.value?.drugLicenseNo2
                    FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No2",
                                                 text: self.registration.value?.drugLicenseNo2,
                                                 onTextChange: { scope.changeDrugLicense2(drugLicenseNo: $0) },
                                                 constText: "21B",
                                                 isValid: drugLicenseNo2ErrorMessageKey == nil,
                                                 errorMessageKey: drugLicenseNo2ErrorMessageKey,
                                                 disableAutocorrection: true,
                                                 autocapitalization: .none)
                }
                
                HStack {
                    let isChecked = Binding(get: { self.isTermsAccepted.value == true },
                                            set: { scope.changeTerms(isAccepted: $0) })
                    
                    CheckBoxView(checked: isChecked)
                    
                    LocalizedText(localizationKey: "i_consent_for_terms_and_conditions",
                                  color: .grey3,
                                  multilineTextAlignment: .leading)
                }
            }
        }
        
        init(scope: ManagementScope.AddRetailerTraderDetails) {
            self.scope = scope
            
            self.registration = SwiftDataSource(dataSource: scope.registration)
            self.validation = SwiftDataSource(dataSource: scope.validation)
            
            self.isTermsAccepted = SwiftDataSource(dataSource: scope.isTermsAccepted)
        }
    }
    
    private struct AddressScreen: View {
        let scope: ManagementScope.AddRetailerAddress
        
        @ObservedObject var registration: SwiftDataSource<DataUserRegistration2>
        @ObservedObject var pincodeValidation: SwiftDataSource<DataPincodeValidation>
        
        @ObservedObject var locationData: SwiftDataSource<DataLocationData>
        
        var body: some View {
            Group {
                let pincodeError = self.pincodeValidation.value?.pincode
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pincode",
                                             text: self.registration.value?.pincode,
                                             onTextChange: { scope.changePincode(pincode: $0) },
                                             keyboardType: .numberPad,
                                             isValid: pincodeError == nil,
                                             errorMessageKey: pincodeError,
                                             textContentType: .postalCode)
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "address_line",
                                             text: self.registration.value?.addressLine1,
                                             onTextChange: { scope.changeAddressLine(address: $0) },
                                             disableAutocorrection: true,
                                             autocapitalization: .words,
                                             textContentType: .fullStreetAddress)
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "landmark",
                                             text: self.registration.value?.landmark,
                                             onTextChange: { scope.changeLandmark(landmark: $0) },
                                             disableAutocorrection: true,
                                             autocapitalization: .words,
                                             textContentType: .sublocality)
                
                let locations = locationData.value?.locations ?? [String]()
                PickerSelector(placeholder: "location",
                               chosenElement: self.registration.value?.location,
                               data: locations,
                               optionsHeight: 30,
                               onChange: { newValue in scope.changeLocation(location: newValue) })
                
                let cities = locationData.value?.cities ?? [String]()
                PickerSelector(placeholder: "city",
                               chosenElement: self.registration.value?.city,
                               data: cities,
                               optionsHeight: 30,
                               onChange: { newValue in scope.changeCity(city: newValue) })
                
                ReadOnlyTextField(placeholder: "district",
                                    text: self.registration.value?.district)
                
                ReadOnlyTextField(placeholder: "state",
                                    text: self.registration.value?.state)
            }
            .notificationAlertSender(withHandler: scope)
        }
        
        init(scope: ManagementScope.AddRetailerAddress) {
            self.scope = scope
            
            self.registration = SwiftDataSource(dataSource: scope.registration)
            self.pincodeValidation = SwiftDataSource(dataSource: scope.pincodeValidation)
            
            self.locationData = SwiftDataSource(dataSource: scope.locationData)
        }
    }
}
