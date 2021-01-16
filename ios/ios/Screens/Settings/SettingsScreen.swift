//
//  SettingsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 14.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct SettingsScreen: View {
    let scope: SettingsScope
    
    var body: some View {
        let view: AnyView
        
        switch self.scope {
        case let scope as SettingsScope.List:
            view = AnyView(SectionsList(scope: scope))
            
        case let scope as SettingsScope.Profile:
            view = AnyView(PersonalProfile(scope: scope))
                
        default:
            view = AnyView(EmptyView())
        }
        
        return AnyView(
            view
                .padding()
        )
    }
    
    private struct SectionsList: View {
        let scope: SettingsScope.List
        
        var body: some View {
            VStack(spacing: 22) {
                ForEach(scope.sections, id: \.self) { section in
                    TableViewCell(textLocalizationKey: section.getTextLocalizationKey(),
                                  imageName: section.getImageName(),
                                  style: .navigation,
                                  onTapAction: { section.select() })
                }
    
                Spacer()
            }
            .screenLogger(withScreenName: "Settings.SectionsList",
                          withScreenClass: SectionsList.self)
        }
    }
    
    private struct PersonalProfile: View {
        let scope: SettingsScope.Profile
        
        @State private var canSubmitPhone = false
        
        var body: some View {
            VStack(spacing: 12) {
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "first_name",
                                             text: scope.user.firstName,
                                             onTextChange: { newValue in })
                    .disableAutocorrection(true)
                    .textContentType(.givenName)
                    .autocapitalization(.words)
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "last_name",
                                             text: scope.user.lastName,
                                             onTextChange: { newValue in })
                    .disableAutocorrection(true)
                    .textContentType(.familyName)
                    .autocapitalization(.words)

                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "email_address",
                                             text: scope.user.email,
                                             onTextChange: { newValue in },
                                             keyboardType: .emailAddress)
                    .textContentType(.emailAddress)
                    .autocapitalization(.none)
                
                PhoneTextField(phone: scope.user.phoneNumber,
                               canSubmitPhone: $canSubmitPhone,
                               errorMessageKey: nil) { newValue in }
                
                Spacer()
            }
            .disabled(true)
            .screenLogger(withScreenName: "Settings.PersonalProfile",
                          withScreenClass: PersonalProfile.self)
        }
    }
    
    private struct ChangePasswordCurrentPassword: View {
        var body: some View {
            VStack(spacing: 32) {
                LocalizedText(localizationKey: "enter_current_password",
                              textWeight: .medium,
                              color: .textGrey)
                
                VStack(spacing: 12) {
                    FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "current_password",
                                                   text: nil,
                                                   onTextChange: { newValue in })
                    
                    MedicoButton(localizedStringKey: "confirm",
                                 isEnabled: true) {
                        
                    }
                }
            }
            .padding()
            .screenLogger(withScreenName: "ChangePassword.CurrentPassword",
                          withScreenClass: ChangePasswordCurrentPassword.self)
        }
    }
    
    private struct ChangePasswordNewPassword: View {
        var body: some View {
            VStack(spacing: 32) {
                LocalizedText(localizationKey: "enter_current_password",
                              textWeight: .medium,
                              color: .textGrey)
                
                VStack(spacing: 12) {
                    FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password",
                                                   text: nil,
                                                   onTextChange: { newValue in })
                    
                    FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "new_password_repeat",
                                                   text: nil,
                                                   onTextChange: { newValue in })
                    
                    MedicoButton(localizedStringKey: "confirm",
                                 isEnabled: true) {
                        
                    }
                }
            }
            .padding()
            .screenLogger(withScreenName: "ChangePassword.NewPassword",
                          withScreenClass: ChangePasswordNewPassword.self)
        }
    }
}

extension SettingsScope.ListSection {
    func getTextLocalizationKey() -> String? {
        switch self {
        case .profile:
            return "personal_profile"
            
        case .changePassword:
            return "change_password"
            
        case .address:
            return "address"
            
        case .gstinDetails:
            return "gstin_details"
            
        default:
            return nil
        }
    }

    func getImageName() -> String? {
        switch self {
        case .profile:
            return "PersonalProfile"
            
        case .changePassword:
            return "Lock"
            
        case .address:
            return "MapPin"
            
        case .gstinDetails:
            return "Folder"
            
        default:
            return nil
        }
    }
}
