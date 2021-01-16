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
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    @ObservedObject var registration: SwiftDataSource<DataUserRegistration2>
    
    @ObservedObject var pincodeValidation: SwiftDataSource<DataPincodeValidation>
    @ObservedObject var userValidation: SwiftDataSource<DataUserValidation2>

    @ObservedObject var locationData: SwiftDataSource<DataLocationData>
    
    var body: some View {
        VStack {
            self.addressDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               action: goToTraderDetails))
        .keyboardResponder()
        .screenLogger(withScreenName: "SignUpAddressScreen",
                      withScreenClass: SignUpAddressScreen.self)
    }
    
    var addressDataFields: some View {
        VStack(spacing: 12) {
            let pincodeError = self.pincodeValidation.value?.pincode
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pincode",
                                         text: self.registration.value?.pincode,
                                         onTextChange: { newValue in
                                            scope.changePincode(pincode: newValue)
                                         },
                                         keyboardType: .numberPad,
                                         isValid: pincodeError == nil,
                                         errorMessageKey: pincodeError)
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
            
            ReadOnlyTextField(placeholder: "district",
                                text: self.registration.value?.district,
                                errorMessageKey: self.userValidation.value?.district)
            
            ReadOnlyTextField(placeholder: "state",
                                text: self.registration.value?.state,
                                errorMessageKey: self.userValidation.value?.state)
        }
        .scrollView()
    }
    
    init(scope: SignUpScope.AddressData) {
        self.scope = scope
        
        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        
        self.registration = SwiftDataSource(dataSource: scope.registration)
        
        self.pincodeValidation = SwiftDataSource(dataSource: scope.pincodeValidation)
        self.userValidation = SwiftDataSource(dataSource: scope.userValidation)
        
        self.locationData = SwiftDataSource(dataSource: scope.locationData)
    }
    
    
    private func goToTraderDetails() {
        guard let registrationValue = registration.value else { return }
        
        _ = scope.validate(userRegistration: registrationValue)
    }
}
