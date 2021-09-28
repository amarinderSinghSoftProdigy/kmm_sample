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
    let text: String?
    let onTextChange: (String) -> Void
    
    let constTrailingCursor: Bool
    
    let keyboardType: UIKeyboardType
    let disableAutocorrection: Bool
    let autocapitalization: UITextAutocapitalizationType
    
    let height: CGFloat
    
    var body: some View {
        VStack(spacing: 6) {
            HStack(spacing: 7) {
                LocalizedText(localizationKey: titleLocalizationKey,
                              textWeight: .bold,
                              fontSize: 12,
                              color: .greyBlue,
                              multilineTextAlignment: .leading)
                    .lineLimit(1)
                
                let textBinding = Binding(get: { text ?? "" },
                                          set: { onTextChange($0) })
                
                let textProperties = TextFieldContainer.TextProperties(
                    font: UIFont(name: "Barlow-Bold", size: 20),
                    textColorName: fieldSelected ? "DarkBlue" : "GreyBlue",
                    textAlignment: .right
                )
                
                TextFieldContainer("",
                                   text: textBinding,
                                   cursorPosition: $cursorPosition,
                                   constTrailingCursor: constTrailingCursor,
                                   fieldSelected: $fieldSelected,
                                   keyboardType: keyboardType,
                                   disableAutocorrection: disableAutocorrection,
                                   autocapitalization: autocapitalization,
                                   textProperties: textProperties)
                    .frame(maxHeight: height)
                
//                TextField("", text: text) { editingChanged in
//                    self.fieldSelected = editingChanged
//                }
//                .medicoText(textWeight: .bold,
//                            fontSize: 20,
//                            color: fieldSelected ? .darkBlue : .greyBlue,
//                            multilineTextAlignment: .trailing)
            }
            
            (fieldSelected ? AppColor.lightBlue : AppColor.greyBlue).color
                .frame(height: 1.5)
        }
    }
    
    init(titleLocalizationKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void,
         keyboardType: UIKeyboardType = .default,
         constTrailingCursor: Bool = false,
         disableAutocorrection: Bool = true,
         autocapitalization: UITextAutocapitalizationType = .none,
         height: CGFloat = 28) {
        self.titleLocalizationKey = titleLocalizationKey
        
        self.text = text
        self.onTextChange = onTextChange
        
        self.constTrailingCursor = constTrailingCursor
        
        self.keyboardType = keyboardType
        self.disableAutocorrection = disableAutocorrection
        self.autocapitalization = autocapitalization
        
        self.height = height
    }
}
