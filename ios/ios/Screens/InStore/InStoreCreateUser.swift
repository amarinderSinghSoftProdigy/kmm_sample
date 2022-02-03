//
//  AddNewCustomer.swift
//  Medico
//
//  Created by user on 03/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct InStoreCreateUser: View {
    
    let scope: InStoreAddUserScope
    @ObservedObject var registration: SwiftDataSource<DataInStoreUserRegistration>
    @ObservedObject var locationData: SwiftDataSource<DataLocationData>
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>

    @State var isPhoneValid: Bool = true

    var body: some View {
        
        VStack(alignment: .leading, spacing: 20) {
            
            Text("customer_details")
                .medicoText(textWeight: .semiBold,
                            fontSize: 20,
                            multilineTextAlignment: .leading)
            VStack {
                createUserForm
                Spacer()
            }
            
            BottomActionView(enableAddCustomer: canGoNext.value != false, onClickReset: reset, onClickAddCustomer: createUser)
        }
        .padding(20)
        .textFieldsModifiers()
        .notificationAlertSender(withHandler: scope)
        .screenLogger(withScreenName: "InStoreCreateUser",
                      withScreenClass: InStoreCreateUser.self)
    }
    
    init(scope: InStoreAddUserScope) {
        self.scope = scope
       // self.notification = SwiftDataSource(dataSource: scope.notifications)
        self.registration = SwiftDataSource(dataSource: scope.registration)
        self.locationData = SwiftDataSource(dataSource: scope.locationData)
        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        self._isPhoneValid = State(initialValue: self.registration.value?.phoneNumber == nil)
    }
    
    private func createUser() {
        scope.createUser()
    }
    
    private func reset() {
        scope.reset()
    }
    
    var createUserForm: some View {
        VStack(spacing: 20) {
            personalInfoFields()
            drugLicenseFields()
            locationFields()
        }.scrollView()
    }
    
    //MARK: Personal Info View
    private func personalInfoFields() -> some View {
        VStack {
            PickerSelector(placeholder: "payment_type",
                           chosenElement: self.registration.value?.paymentMethod.serverValue ?? "",
                           data: getPaymentMethodOptions(),
                           optionsHeight: 30,
                           onChange: { newValue in scope.changePaymentMethod(paymentMethod: newValue) })
            
            let tradeName = self.registration.value?.tradeName
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "trade_name",
                                         text: tradeName,
                                         onTextChange: { newValue in scope.changeTradeName(tradeName: newValue) },
                                         isValid: tradeName?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .words,
                                         textContentType: .givenName)
            
            let canSubmitPhone = Binding(get: { isPhoneValid },
                                         set: { isPhoneValid = $0 })
            PhoneTextField(phone: self.registration.value?.phoneNumber,
                           canSubmitPhone: canSubmitPhone,
                           errorMessageKey: self.registration.value?.phoneNumber) { newValue in
                scope.changePhoneNumber(phoneNumber: newValue)
            }
            
            let gstin = self.registration.value?.gstin
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "gstin",
                                         text: gstin,
                                         onTextChange: { newValue in scope.changeGstin(gstin: newValue) },
                                         isValid: gstin?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .words,
                                         textContentType: .givenName)
            
            let panNumber = self.registration.value?.panNumber
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pan_number",
                                         text: panNumber,
                                         onTextChange: { newValue in scope.changePan(panNumber: newValue) },
                                         isValid: panNumber?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .none)
            
        }
    }
    
    //MARK:  DrugLicense Info View
    private func drugLicenseFields() -> some View {
        VStack {
            let drugLicenseNo1 = self.registration.value?.drugLicenseNo1
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No1",
                                         text: drugLicenseNo1,
                                         onTextChange: { newValue in scope.changeDrugLicense1(drugLicenseNo: newValue) },
                                         isValid: drugLicenseNo1?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .none)
            
            let drugLicenseNo2 = self.registration.value?.drugLicenseNo2
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "drug_license_No2",
                                         text: drugLicenseNo2,
                                         onTextChange: { newValue in scope.changeDrugLicense2(drugLicenseNo: newValue) },
                                         isValid: drugLicenseNo2?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .none)
        }
    }
    
    //MARK: Location Info View
    private func locationFields() -> some View {
        VStack {
            let pincode = self.registration.value?.pincode
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "pincode",
                                         text: pincode,
                                         onTextChange: { newValue in
                                            scope.changePincode(pincode: newValue)
                                         },
                                         keyboardType: .numberPad,
                                         isValid:  pincode?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         textContentType: .postalCode)
            
            let addressLine1 = self.registration.value?.addressLine1
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "address_line",
                                         text: self.registration.value?.addressLine1,
                                         onTextChange: { newValue in
                                            scope.changeAddressLine(address: newValue)
                                         },
                                         isValid: addressLine1?.isEmpty == false,
                                         errorMessageKey: "required_field",
                                         disableAutocorrection: true,
                                         autocapitalization: .words,
                                         textContentType: .fullStreetAddress)
            
            let landmark = self.registration.value?.landmark
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "landmark",
                                         text: landmark,
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
    }
    
    //MARK: Get Payment Methods
    private func getPaymentMethodOptions() -> [String] {
        var options = [String]()
        let iterator = DataPaymentMethod.values().iterator()
        while iterator.hasNext() {
            guard let paymentMethod = iterator.next_() as? DataPaymentMethod else { continue }
            options.append(paymentMethod.serverValue)
        }
        return options
    }
    
    //MARK: Bottom Action View
    private struct BottomActionView: View {
                
        var enableAddCustomer: Bool
        var onClickReset: ()->Void
        var onClickAddCustomer: ()->Void
        
        var body: some View {
            GeometryReader { geometry in
                HStack(spacing: geometry.size.width * 0.05) {
                    
                    MedicoButton(localizedStringKey: "reset",
                                 isEnabled: true,
                                 cornerRadius: 24,
                                 fontSize: 15,
                                 fontWeight: .bold,
                                 fontColor: .lightBlue,
                                 buttonColor: .clear) {
                        self.onClickReset()
                    }
                    .strokeBorder(.lightBlue,
                                  borderOpacity: 0.5,
                                  fill: .clear,
                                  lineWidth: 2,
                                  cornerRadius: 24)
                    .frame(width: geometry.size.width * 0.35)
                    
                    MedicoButton(localizedStringKey: "add_customer",
                                 isEnabled: enableAddCustomer,
                                 cornerRadius: 24,
                                 fontSize: 15,
                                 fontWeight: .bold,
                                 fontColor: .white,
                                 buttonColor: .lightBlue) {
                        self.onClickAddCustomer()
                    }
                    .frame(width: geometry.size.width * 0.60)
                }
            }.frame( height: 50, alignment: .bottom)
        }
    }
}


struct InStoreBottomButtonsModifier: ViewModifier {
    
    var enableAddUser: Bool
    var onClickReset: ()->()
    var onClickAddUser: ()->()
    
    init(enableAddUser: Bool,
         addUserAction: @escaping (() -> ()),
         resetAction: @escaping (() -> ())) {
        self.enableAddUser = enableAddUser
        self.onClickReset = resetAction
        self.onClickAddUser = addUserAction
    }
    
    func body(content: Content) -> some View {
        VStack {
            content
            GeometryReader { geometry in
                HStack(spacing: geometry.size.width * 0.05) {
                    
                    MedicoButton(localizedStringKey: "reset",
                                 isEnabled: true,
                                 cornerRadius: 24,
                                 fontSize: 15,
                                 fontWeight: .bold,
                                 fontColor: .lightBlue,
                                 buttonColor: .clear) {
                        self.onClickReset()
                    }
                    .strokeBorder(.lightBlue,
                                  borderOpacity: 0.5,
                                  fill: .clear,
                                  lineWidth: 2,
                                  cornerRadius: 24)
                    .frame(width: geometry.size.width * 0.35)
                    
                    MedicoButton(localizedStringKey: "add_customer",
                                 isEnabled: enableAddUser,
                                 cornerRadius: 24,
                                 fontSize: 15,
                                 fontWeight: .bold,
                                 fontColor: .white,
                                 buttonColor: .lightBlue) {
                        self.onClickAddUser()
                    }
                    .frame(width: geometry.size.width * 0.60)
                }
            }.frame( height: 50, alignment: .bottom)
        }
    }
}
