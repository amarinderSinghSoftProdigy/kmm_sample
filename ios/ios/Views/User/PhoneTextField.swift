//
//  PhoneTextField.swift
//  ios
//
//  Created by Dasha Gurinovich on 3.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct PhoneTextField: View {
    @State private var phone: String
    
    let canSubmitPhone: Binding<Bool>
    let onTextChange: (String) -> Void
    let errorMessageKey: String?
    
    var body: some View {
        FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                     text: phone,
                                     onTextChange: { newValue in
                                        checkPhoneNumber(newValue)
                                        
                                        let rawPhoneNumber = PhoneNumberUtil.shared.getRawPhoneNumber(newValue)
                                        onTextChange(rawPhoneNumber)
                                     },
                                     textFormatter: { value in self.phone },
                                     keyboardType: .phonePad,
                                     isValid: canSubmitPhone.wrappedValue,
                                     errorMessageKey: errorMessageKey)
            .textContentType(.telephoneNumber)
    }
    
    init(phone: String?,
         canSubmitPhone: Binding<Bool>,
         errorMessageKey: String? = nil,
         onTextChange: @escaping (String) -> Void
    ) {
        self._phone = State(initialValue: phone ?? "")
        
        self.canSubmitPhone = canSubmitPhone
        self.errorMessageKey = errorMessageKey
        self.onTextChange = onTextChange
    }
    
    private func checkPhoneNumber(_ phone: String) {
        let possibleNumber = PhoneNumberUtil.shared.isValidNumber(phone)
        
        self.phone = possibleNumber.formattedNumber
        self.canSubmitPhone.wrappedValue = possibleNumber.isValid
    }
}
