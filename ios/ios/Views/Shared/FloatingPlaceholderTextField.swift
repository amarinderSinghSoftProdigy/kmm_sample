//
//  FloatingPlaceholderTextView.swift
//  ios
//
//  Created by Dasha Gurinovich on 25.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import Combine

struct FloatingPlaceholderTextField: View {
    let placeholderLocalizedStringKey: String
    let keyboardType: UIKeyboardType
    
    @State private var text: String
    
    let onTextChange: (String) -> Void
    let textFormatter: ((String) -> String)?
    
    let height: CGFloat
    
    let isValid: Bool
    let errorMessageKey: String?
    
    @State var fieldSelected = false
    
    var body: some View {
        TextField("", text: $text, onEditingChanged: { (changed) in
            self.fieldSelected = changed
        }, onCommit: {
            self.fieldSelected = false
        })
        .keyboardType(keyboardType)
        .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                              text: text,
                                              height: height,
                                              fieldSelected: fieldSelected,
                                              isValid: isValid,
                                              errorMessageKey: errorMessageKey))
        .onReceive(Just(text)) { text in
            self.onTextChange(text)
            
            if let textFormatter = self.textFormatter {
                self.text = textFormatter(text)
            }
        }
    }
    
    init(placeholderLocalizedStringKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void,
         textFormatter: ((String) -> String)? = nil,
         height: CGFloat = 50,
         keyboardType: UIKeyboardType = .default,
         isValid: Bool = true,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        
        self._text = State(initialValue: text ?? "")
        self.onTextChange = onTextChange
        self.textFormatter = textFormatter
        
        self.height = height
        self.keyboardType = keyboardType
        
        self.isValid = isValid
        self.errorMessageKey = errorMessageKey
    }
}

struct FloatingPlaceholderSecureField: View {
    let placeholderLocalizedStringKey: String
    @State private var text: String
    
    let onTextChange: (String) -> Void
    
    let height: CGFloat
    
    let showPlaceholderWithText: Bool
    
    let isValid: Bool
    let errorMessageKey: String?
    
    var body: some View {
        SecureField("", text: $text)
            .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                                  text: text,
                                                  height: height,
                                                  fieldSelected: false,
                                                  isValid: isValid,
                                                  showPlaceholderWithText: showPlaceholderWithText,
                                                  errorMessageKey: errorMessageKey))
            .onReceive(Just(text)) { text in
                onTextChange(text)
            }
    }
    
    init(placeholderLocalizedStringKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void,
         height: CGFloat = 50,
         showPlaceholderWithText: Bool = false,
         isValid: Bool = true,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        self._text = State(initialValue: text ?? "")
        
        self.onTextChange = onTextChange
        
        self.height = height
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.isValid = isValid
        self.errorMessageKey = errorMessageKey
    }
}

struct FloatingPlaceholderModifier: ViewModifier {
    let placeholderLocalizedStringKey: String
    let text: String
    
    let height: CGFloat
    
    let fieldSelected: Bool
    let isValid: Bool
    let showPlaceholderWithText: Bool
    
    let errorMessageKey: String?
    
    func body(content: Content) -> some View {
        VStack(alignment: .center, spacing: 0) {
            let padding: CGFloat = 16
            
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: 8)
                    .fill(appColor: .white)
                    .frame(height: height)
                
                let placeholderMoved = showPlaceholderWithText && (fieldSelected ||  !text.isEmpty)
                let fontSize: CGFloat = placeholderMoved ? 11 : 15
                let textOffset: CGFloat = placeholderMoved ? -10 : 0
                let textColor: AppColor = placeholderMoved ?
                    (isValid ? .lightBlue : .red) : .placeholderGrey

                Text(LocalizedStringKey(placeholderLocalizedStringKey))
                    .modifier(MedicoText(fontSize: fontSize, color: textColor))
                    .padding([.leading, .trailing], padding)
                    .offset(y: textOffset)
                    .isHidden(!placeholderMoved && !text.isEmpty)
                    .animation(.easeOut)
                
                let textFieldHeight = height - 12
                let textFieldAlignment: Alignment = placeholderMoved ? .bottomLeading : .leading
                content
                    .modifier(MedicoText(fontSize: 15, multilineTextAlignment: .leading))
                    .frame(height: textFieldHeight, alignment: textFieldAlignment)
                    .padding([.leading, .trailing], padding)
            }
            
            VStack(alignment: .leading) {
                Rectangle()
                    .fill(appColor: isValid ? .lightBlue : .red)
                    .frame(height: 1)
                    .isHidden(!fieldSelected && (self.errorMessageKey == nil || isValid))
                
                if !isValid, let errorMessageKey = self.errorMessageKey {
                    Text(LocalizedStringKey(errorMessageKey))
                        .modifier(MedicoText(fontSize: 12, color: .red))
                        .padding(.leading, padding)
                }
            }
        }
    }
    
    init(placeholderLocalizedStringKey: String, text: String,
         height: CGFloat, fieldSelected: Bool, isValid: Bool = true, showPlaceholderWithText: Bool = true,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        self.text = text
        
        self.height = height
        
        self.fieldSelected =  fieldSelected
        self.isValid = isValid
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.errorMessageKey = errorMessageKey
    }
}

//struct FloatingPlaceholderTextField_Previews: PreviewProvider {
//    @State static var result: String = ""
//
//    static var previews: some View {
//        TextField(LocalizedStringKey(""), text: $result)
//        .modifier(FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number"))
//    }
//}
