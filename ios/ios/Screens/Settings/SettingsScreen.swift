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
    
}
