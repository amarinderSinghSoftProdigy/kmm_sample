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
    
    let text: Binding<String>
    
    let height: CGFloat
    
    let textValidator: ((String) -> Bool)?
    let errorMessageKey: String?
    
    @State var fieldSelected = false
    @State var isValid = true
    
    var body: some View {
        TextField("", text: text, onEditingChanged: { (changed) in
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
        .onReceive(Just(text.wrappedValue)) { text in
            if let textValidator = self.textValidator {
                self.isValid = textValidator(text)
            }
        }
    }
    
    init(placeholderLocalizedStringKey: String,
         text: Binding<String>,
         textValidator: ((String) -> Bool)? = nil,
         height: CGFloat = 50,
         keyboardType: UIKeyboardType = .default,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        
        self.text = text
        self.textValidator = textValidator
        
        self.height = height
        self.keyboardType = keyboardType
        
        self.errorMessageKey = errorMessageKey
    }
}

struct FloatingPlaceholderSecureField: View {
    let placeholderLocalizedStringKey: String
    let text: Binding<String>
    
    let height: CGFloat
    
    let showPlaceholderWithText: Bool
    
    let textValidator: ((String) -> Bool)?
    @State var isValid = true
    let errorMessageKey: String?
    
    var body: some View {
        SecureField("", text: text)
            .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                                  text: text,
                                                  height: height,
                                                  fieldSelected: false,
                                                  isValid: isValid,
                                                  showPlaceholderWithText: showPlaceholderWithText,
                                                  errorMessageKey: errorMessageKey))
            .onReceive(Just(text.wrappedValue)) { text in
                if let textValidator = self.textValidator {
                    self.isValid = textValidator(text)
                }
            }
    }
    
    init(placeholderLocalizedStringKey: String,
         text: Binding<String>,
         textValidator: ((String) -> Bool)? = nil,
         height: CGFloat = 50,
         showPlaceholderWithText: Bool = false,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        self.text = text
        
        self.textValidator = textValidator
        
        self.height = height
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.errorMessageKey = errorMessageKey
    }
}

struct PlaceholderText: ViewModifier {
    let fontSize: CGFloat
    let textColor: AppColor
    let padding: CGFloat
    
    func body(content: Content) -> some View {
        content
            .font(.custom("Barlow-Regular", size: fontSize))
            .foregroundColor(appColor: textColor)
            .padding([.leading, .trailing], padding)
            .animation(.easeOut)
        
    }
    
    init(fontSize: CGFloat = 15, textColor: AppColor = .placeholderGrey, padding: CGFloat = 16) {
        self.fontSize = fontSize
        self.textColor = textColor
        self.padding = padding
    }
}

struct FloatingPlaceholderModifier: ViewModifier {
    let placeholderLocalizedStringKey: String
    let text: Binding<String>
    
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
                
                let placeholderMoved = showPlaceholderWithText && (fieldSelected ||  !text.wrappedValue.isEmpty)
                let fontSize: CGFloat = placeholderMoved ? 11 : 15
                let textOffset: CGFloat = placeholderMoved ? -10 : 0
                let textColor: AppColor = placeholderMoved ?
                    (isValid ? .lightBlue : .red) : .placeholderGrey

                Text(LocalizedStringKey(placeholderLocalizedStringKey))
                    .modifier(PlaceholderText(fontSize: fontSize, textColor: textColor, padding: padding))
                    .offset(y: textOffset)
                    .isHidden(!placeholderMoved && !text.wrappedValue.isEmpty)
                    .animation(.easeOut)
                
                let textFieldHeight = height - 12
                let textFieldAlignment: Alignment = placeholderMoved ? .bottomLeading : .leading
                content
                    .font(.custom("Barlow-Regular", size: 15))
                    .frame(height: textFieldHeight, alignment: textFieldAlignment)
                    .foregroundColor(appColor: .darkBlue)
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
    
    init(placeholderLocalizedStringKey: String, text: Binding<String>,
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