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
    
    @ObservedObject var registration: SwiftDatasource<DataUserRegistration2>
    @ObservedObject var validation: SwiftDatasource<DataUserValidation2>

    @ObservedObject var locationData: SwiftDatasource<DataLocation>
    
    var body: some View {
        VStack {
            self.addressDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: true, action: goToTraderDetails))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("address"), displayMode: .inline)
    }
    
    var addressDataFields: some View {
            VStack(spacing: 12) {
                
                let pincode = self.registration.value?.pincode
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pincode",
                                             text: pincode,
                                             onTextChange: { newValue in
                                                scope.changePincode(pincode: newValue)
                                             },
                                             keyboardType: .numberPad,
                                             isValid: pincode?.isEmpty == false,
                                             errorMessageKey: "required_field")
                    .textContentType(.postalCode)
                
                let addressLineErrorMessageKey = self.validation.value?.addressLine1
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
                
                let locations = (locationData.value as? DataLocation.Data)?.locations ?? [String]()
                PickerSelector(placeholder: "location",
                               chosenElement: self.registration.value?.location,
                               data: locations,
                               onChange: { newValue in scope.changeLocation(location: newValue) })
                    .modifier(FieldError(errorMessageKey: self.validation.value?.location))
                
                let cities = (locationData.value as? DataLocation.Data)?.cities ?? [String]()
                PickerSelector(placeholder: "city",
                               chosenElement: self.registration.value?.city,
                               data: cities,
                               onChange: { newValue in scope.changeCity(city: newValue) })
                    .modifier(FieldError(errorMessageKey: self.validation.value?.city))
                
                PlaceholderTextView(placeholder: "district",
                                    text: self.registration.value?.district,
                                    errorMessageKey: self.validation.value?.district)
                
                PlaceholderTextView(placeholder: "state",
                                    text: self.registration.value?.state,
                                    errorMessageKey: self.validation.value?.state)
            }
            .modifier(ScrollViewModifier())
    }
    
    init(scope: SignUpScope.AddressData) {
        self.scope = scope
        
        self.registration = SwiftDatasource(dataSource: scope.registration)
        self.validation = SwiftDatasource(dataSource: scope.validation)
        self.locationData = SwiftDatasource(dataSource: scope.locationData)
    }
    
    
    private func goToTraderDetails() {
        guard let registrationValue = registration.value else { return }
        
        scope.tryToSignUp(userRegistration: registrationValue)
    }
}
