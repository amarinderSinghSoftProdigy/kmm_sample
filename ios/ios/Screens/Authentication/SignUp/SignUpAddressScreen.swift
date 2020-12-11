//
//  SignUpAddressScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpAddressScreen: View {
    let scope: SignUpScope.AddressData
    
    @ObservedObject var canGoNext: SwiftDatasource<KotlinBoolean>
    
    @ObservedObject var registration: SwiftDatasource<DataUserRegistration2>
    
    @ObservedObject var pincodeValidation: SwiftDatasource<DataPincodeValidation>
    @ObservedObject var userValidation: SwiftDatasource<DataUserValidation2>

    @ObservedObject var locationData: SwiftDatasource<DataLocationData>
    
    var body: some View {
        VStack {
            self.addressDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               action: goToTraderDetails))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("address"), displayMode: .inline)
    }
    
    var addressDataFields: some View {
            VStack(spacing: 12) {
                
                let pincode = self.pincodeValidation.value?.pincode
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pincode",
                                             text: pincode,
                                             onTextChange: { newValue in
                                                scope.changePincode(pincode: newValue)
                                             },
                                             keyboardType: .numberPad,
                                             isValid: pincode == nil,
                                             errorMessageKey: pincode)
                    .textContentType(.postalCode)
                
                let addressLineErrorMessageKey = self.userValidation.value?.addressLine1
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "address_line",
                                             text: self.registration.value?.addressLine1,
                                             onTextChange: { newValue in
                                                scope.changeAddressLine(address: newValue)
                                             },
                                             isValid: addressLineErrorMessageKey == nil,
                                             errorMessageKey: addressLineErrorMessageKey)
                    .disableAutocorrection(true)
                    .textContentType(.fullStreetAddress)
                    .autocapitalization(.words)
                
                let locations = locationData.value?.locations ?? [String]()
                PickerSelector(placeholder: "location",
                               chosenElement: self.registration.value?.location,
                               data: locations,
                               onChange: { newValue in scope.changeLocation(location: newValue) })
                    .fieldError(withLocalizedKey: self.userValidation.value?.location)
                
                let cities = locationData.value?.cities ?? [String]()
                PickerSelector(placeholder: "city",
                               chosenElement: self.registration.value?.city,
                               data: cities,
                               onChange: { newValue in scope.changeCity(city: newValue) })
                    .fieldError(withLocalizedKey: self.userValidation.value?.city)
                
                PlaceholderTextView(placeholder: "district",
                                    text: self.registration.value?.district,
                                    errorMessageKey: self.userValidation.value?.district)
                
                PlaceholderTextView(placeholder: "state",
                                    text: self.registration.value?.state,
                                    errorMessageKey: self.userValidation.value?.state)
            }
            .scrollView()
    }
    
    init(scope: SignUpScope.AddressData) {
        self.scope = scope
        
        self.canGoNext = SwiftDatasource(dataSource: scope.canGoNext)
        
        self.registration = SwiftDatasource(dataSource: scope.registration)
        
        self.pincodeValidation = SwiftDatasource(dataSource: scope.pincodeValidation)
        self.userValidation = SwiftDatasource(dataSource: scope.userValidation)
        
        self.locationData = SwiftDatasource(dataSource: scope.locationData)
    }
    
    
    private func goToTraderDetails() {
        guard let registrationValue = registration.value else { return }
        
        _ = scope.validate(userRegistration: registrationValue)
    }
}
