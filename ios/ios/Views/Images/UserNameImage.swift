//
//  UserNameImage.swift
//  Medico
//
//  Created by Dasha Gurinovich on 22.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct UserNameImage: View {
    let username: String
    
    private var usernameAbbreviation: String {
        if username.isEmpty { return "" }
        
        var abbreviation = ""
        
        for index in 0..<username.count {
            if abbreviation.count >= 2 { break }
            
            let letter = username[index]
            if index == 0 || letter.isUppercase && String(username[index - 1]) == " " {
                abbreviation += String(letter)
            }
        }
        
        return abbreviation
    }
    
    var body: some View {
        ZStack {
            Circle().fill(appColor: .primary)
            
            Text(usernameAbbreviation)
                .medicoText(textWeight: .bold,
                            fontSize: 36)
                .minimumScaleFactor(0.01)
                .padding()
        }
    }
}
