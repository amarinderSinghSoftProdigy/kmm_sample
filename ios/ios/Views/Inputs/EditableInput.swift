//
//  EditableTextField.swift
//  Medico
//
//  Created by Dasha Gurinovich on 6.09.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct EditableInput: View {
    
    @State private var fieldSelected = false
    
    let titleLocalizationKey: String
    let text: Binding<String>
    
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
                .frame(maxWidth: .infinity)
            }
            
            (fieldSelected ? AppColor.lightBlue : AppColor.greyBlue).color
                .frame(height: 1.5)
        }
    }
    
    init(titleLocalizationKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void) {
        self.titleLocalizationKey = titleLocalizationKey
        
        self.text = Binding(get: { text ?? "" },
                            set: { onTextChange($0) })
    }
}
