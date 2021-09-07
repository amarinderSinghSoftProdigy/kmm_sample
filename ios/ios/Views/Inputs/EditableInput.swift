//
//  EditableTextField.swift
//  Medico
//
//  Created by Dasha Gurinovich on 6.09.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct EditableInput: View {
    @State private var cursorPosition: Int?
    @State private var fieldSelected = false
    
    let titleLocalizationKey: String
    let text: Binding<String>
    
    let keyboardType: UIKeyboardType
    let disableAutocorrection: Bool
    let autocapitalization: UITextAutocapitalizationType
    
    var body: some View {
        VStack(spacing: 6) {
            HStack(spacing: 7) {
                LocalizedText(localizationKey: titleLocalizationKey,
                              textWeight: .bold,
                              fontSize: 12,
                              color: .greyBlue,
                              multilineTextAlignment: .leading)
                    .lineLimit(1)
                    .minimumScaleFactor(0.3)
                
                TextField("", text: text) { editingChanged in
                    self.fieldSelected = editingChanged
                }
                .medicoText(textWeight: .bold,
                            fontSize: 20,
                            color: fieldSelected ? .darkBlue : .greyBlue,
                            multilineTextAlignment: .trailing)
                .keyboardType(keyboardType)
                .disableAutocorrection(disableAutocorrection)
                .autocapitalization(autocapitalization)
            }
            
            (fieldSelected ? AppColor.lightBlue : AppColor.greyBlue).color
                .frame(height: 1.5)
        }
    }
    
    init(titleLocalizationKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void,
         keyboardType: UIKeyboardType = .default,
         disableAutocorrection: Bool = true,
         autocapitalization: UITextAutocapitalizationType = .none) {
        self.titleLocalizationKey = titleLocalizationKey
        
        self.text = Binding(get: { text ?? "" },
                            set: { onTextChange($0) })
        
        self.keyboardType = keyboardType
        self.disableAutocorrection = disableAutocorrection
        self.autocapitalization = autocapitalization
    }
}
