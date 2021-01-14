//
//  SettingsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 14.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct SettingsScreen: View {
    private let options = ["PersonalProfile", "ChangePassword", "Address", "GSTIN Details"]
    
    var body: some View {
        VStack(spacing: 22) {
            ForEach(options, id: \.self) { option in
                TableViewCell(textLocalizationKey: self.getTextLocalizationKey(forOption: option),
                              imageName: self.getImageName(forOption: option),
                              style: .navigation,
                              onTapAction: { print(option) })
            }
            
            Spacer()
        }
        .padding()
    }
        
    private func getTextLocalizationKey(forOption option: String) -> String? {
        switch option {
        case "PersonalProfile":
            return "personal_profile"
            
        case "ChangePassword":
            return "change_password"
            
        case "Address":
            return "address"
            
        case "GSTIN Details":
            return "gstin_details"
            
        default:
            return nil
        }
    }
    
    private func getImageName(forOption option: String) -> String? {
        switch option {
        case "PersonalProfile":
            return "PersonalProfile"
            
        case "ChangePassword":
            return "Lock"
            
        case "Address":
            return "MapPin"
            
        case "GSTIN Details":
            return "Folder"
            
        default:
            return nil
        }
    }
    
    private struct PersonalProfile: View {
        @State private var canSubmitPhone = false
        
        var body: some View {
            VStack(spacing: 12) {
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name",
                                             text: "firstName",
                                             onTextChange: { newValue in })
                    .disableAutocorrection(true)
                    .textContentType(.givenName)
                    .autocapitalization(.words)
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name",
                                             text: "lastName",
                                             onTextChange: { newValue in })
                    .disableAutocorrection(true)
                    .textContentType(.familyName)
                    .autocapitalization(.words)

                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "email_address",
                                             text: "email",
                                             onTextChange: { newValue in },
                                             keyboardType: .emailAddress)//,
//                                             isValid: emailErrorMessageKey == nil,
//                                             errorMessageKey: emailErrorMessageKey)
                    .textContentType(.emailAddress)
                    .autocapitalization(.none)
                
                PhoneTextField(phone: "1234",
                               canSubmitPhone: $canSubmitPhone,
                               errorMessageKey: nil) { newValue in
//                    scope.changePhoneNumber(phoneNumber: newValue)
                }
                
                Spacer()
            }
            .padding()
            .screenLogger(withScreenName: "SettingsPersonalProfile",
                          withScreenClass: PersonalProfile.self)
        }
    }
}
