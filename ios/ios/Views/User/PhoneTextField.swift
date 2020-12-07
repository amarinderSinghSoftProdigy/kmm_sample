//
//  PhoneTextField.swift
//  ios
//
//  Created by Dasha Gurinovich on 3.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct PhoneTextField: View {
    private var phone: Binding<String>
    
    let canSubmitPhone: Binding<Bool>
    let onTextChange: (String) -> Void
    let errorMessageKey: String?
    
    var body: some View {
        FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                     text: phone.wrappedValue,
                                     onTextChange: { newValue in
                                        phone.wrappedValue = newValue
                                     },
                                     keyboardType: .phonePad,
                                     isValid: canSubmitPhone.wrappedValue,
                                     errorMessageKey: errorMessageKey)
            .textContentType(.telephoneNumber)
            .onAppear {
                let isValid = PhoneNumberUtil.shared.isValidNumber(phone.wrappedValue).isValid
                canSubmitPhone.wrappedValue = isValid
            }
    }
    
    init(phone: String?,
         canSubmitPhone: Binding<Bool>,
         errorMessageKey: String? = nil,
         onTextChange: @escaping (String) -> Void
    ) {
        self.canSubmitPhone = canSubmitPhone
            
        self.errorMessageKey = errorMessageKey
        self.onTextChange = onTextChange
        
        self.phone = Binding(get: {
            PhoneNumberUtil.shared.getFormattedPhoneNumber(phone ?? "")
        },
        set: {
            let isValid = PhoneNumberUtil.shared.isValidNumber($0).isValid
            canSubmitPhone.wrappedValue = isValid
                                
            let rawPhoneNumber = isValid ? PhoneNumberUtil.shared.getRawPhoneNumber($0) : $0
            onTextChange(rawPhoneNumber)
        })
    }
}
