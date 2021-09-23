//
//  PhoneTextField.swift
//  ios
//
//  Created by Dasha Gurinovich on 3.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import Combine

struct PhoneTextField: View {
    private var phone: Binding<String>
    
    let canSubmitPhone: Binding<Bool>
    let onTextChange: (String) -> Void
    let errorMessageKey: String?
    
    var body: some View {
        if #available(iOS 14.0, *) {
            textField
                .onAppear {
                    updatePhoneValidationState(for: phone.wrappedValue)
                }
                .onChange(of: phone.wrappedValue) { value in
                    updatePhoneValidationState(for: value)
                }
        } else {
            textField
                .onReceive(Just(phone.wrappedValue)) { value in
                    updatePhoneValidationState(for: value)
                }
        }
    }
    
    var textField: some View {
        FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number",
                                     text: phone.wrappedValue,
                                     onTextChange: { newValue in
                                        phone.wrappedValue = newValue
                                     },
                                     constText: "+91",
                                     keyboardType: .phonePad,
                                     isValid: canSubmitPhone.wrappedValue,
                                     errorMessageKey: errorMessageKey,
                                     textContentType: .telephoneNumber)
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
            phone ?? ""
        },
        set: {
            onTextChange($0)
        })
    }
    
    private func updatePhoneValidationState(for phone: String) {
        canSubmitPhone.wrappedValue = !phone.isEmpty
    }
}
