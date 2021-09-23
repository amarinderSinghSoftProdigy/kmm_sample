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
            
        case let scope as SettingsScope.Address:
            view = AnyView(Address(scope: scope))
            
        case let scope as SettingsScope.GstinDetails:
            view = AnyView(GstinDetails(scope: scope))
                
        default:
            view = AnyView(EmptyView())
        }
        
        return AnyView(
            VStack {
                view
                
                Spacer()
            }
            .padding()
        )
    }
    
    private struct SectionsList: View {
        let scope: SettingsScope.List
        
        var body: some View {
            VStack(alignment: .leading, spacing: 22) {
                ForEach(scope.sections, id: \.self) { section in
                    TableViewCell(textLocalizationKey: section.getTextLocalizationKey(),
                                  imageName: section.getImageName(),
                                  imageSize: 20,
                                  style: .navigation,
                                  onTapAction: { section.select() })
                }
            }
            .screenLogger(withScreenName: "Settings.SectionsList",
                          withScreenClass: SectionsList.self)
        }
    }
    
    private struct PersonalProfile: View {
        let scope: SettingsScope.Profile
        
        var body: some View {
            VStack(spacing: 12) {
                ReadOnlyTextField(placeholder: "first_name",
                                  text: scope.user.firstName)
                
                ReadOnlyTextField(placeholder: "last_name",
                                  text: scope.user.lastName)
            
                ReadOnlyTextField(placeholder: "email_address",
                                  text: scope.user.email)
                
                ReadOnlyTextField(placeholder: "phone_number",
                                  text: PhoneNumberUtil.shared.getFormattedPhoneNumber(scope.user.phoneNumber))
            }
            .screenLogger(withScreenName: "Settings.PersonalProfile",
                          withScreenClass: PersonalProfile.self)
        }
    }
    
    private struct Address: View {
        let scope: SettingsScope.Address
        
        var body: some View {
            VStack(spacing: 12) {
                ReadOnlyTextField(placeholder: "pincode",
                                  text: String(scope.addressData.pincode))
                
                ReadOnlyTextField(placeholder: "address_line",
                                  text: scope.addressData.address)
                
                ReadOnlyTextField(placeholder: "landmark",
                                  text: scope.addressData.landmark)
            
                ReadOnlyTextField(placeholder: "location",
                                  text: scope.addressData.location)

                ReadOnlyTextField(placeholder: "city",
                                  text: scope.addressData.city)

                ReadOnlyTextField(placeholder: "district",
                                    text: scope.addressData.district)

                ReadOnlyTextField(placeholder: "state",
                                    text: scope.addressData.state)
            }
            .screenLogger(withScreenName: "Settings.Address",
                          withScreenClass: Address.self)
        }
    }
    
    private struct GstinDetails: View {
        let scope: SettingsScope.GstinDetails
        
        var body: some View {
            VStack(spacing: 12) {
                ReadOnlyTextField(placeholder: "trade_name",
                                  text: scope.details.tradeName)
                
                ReadOnlyTextField(placeholder: "gstin",
                                  text: scope.details.gstin)
            
                ReadOnlyTextField(placeholder: "drug_license_No1",
                                  text: scope.details.license1)
            
                ReadOnlyTextField(placeholder: "drug_license_No2",
                                  text: scope.details.license2)
            }
            .screenLogger(withScreenName: "Settings.GstinDetails",
                          withScreenClass: GstinDetails.self)
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
