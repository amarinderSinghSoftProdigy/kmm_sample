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
    @ObservedObject private var textBindingManager: TextBindingManager
    
    let placeholderLocalizedStringKey: String
    let keyboardType: UIKeyboardType
    
    let constText: String?
    
    let height: CGFloat
    
    let isValid: Bool
    let errorMessageKey: String?
    
    @State var fieldSelected = false
    
    var body: some View {
        HStack {
            if let constText = self.constText,
               !constText.isEmpty {
                LocalizedText(localizationKey: constText,
                              fontSize: 15,
                              multilineTextAlignment: .leading)
            }
            
            TextField("", text: $textBindingManager.text, onEditingChanged: { (changed) in
                self.fieldSelected = changed
            }, onCommit: {
                self.fieldSelected = false
            })
            .keyboardType(keyboardType)
        }
        .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                              text: textBindingManager.text,
                                              height: height,
                                              fieldSelected: fieldSelected,
                                              isValid: isValid,
                                              errorMessageKey: errorMessageKey,
                                              alwaysPlaceholderMoved: constText?.isEmpty == false))
    }
    
    init(placeholderLocalizedStringKey: String,
         text: String?,
         onTextChange: @escaping (String) -> Void,
         constText: String? = nil,
         height: CGFloat = 50,
         keyboardType: UIKeyboardType = .default,
         isValid: Bool = true,
         errorMessageKey: String? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        
        self.textBindingManager = TextBindingManager(initialText: text) {
            onTextChange($0)
        }
        
        self.constText = constText
        
        self.height = height
        self.keyboardType = keyboardType
        
        self.isValid = isValid
        self.errorMessageKey = errorMessageKey
    }
}

struct FloatingPlaceholderSecureField: View {
    @ObservedObject private var textBindingManager: TextBindingManager
    
    let placeholderLocalizedStringKey: String
    
    let height: CGFloat
    
    let showPlaceholderWithText: Bool
    
    let isValid: Bool
    let errorMessageKey: String?
    
    @State var showsPassword = false
    @State var fieldSelected = false
    
    var body: some View {
        HStack {
            if showsPassword {
                TextField("", text: $textBindingManager.text)
                    .disableAutocorrection(true)
                    
                    .autocapitalization(.none)
            }
            else {
                SecureField("", text: $textBindingManager.text)
            }
            
            if !textBindingManager.text.isEmpty {
                Button(action: { self.showsPassword.toggle() }) {
                    Image(systemName: self.showsPassword ? "eye" : "eye.slash")
                        .foregroundColor(appColor: .darkBlue)
                }
            }
        }
        .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                              text: textBindingManager.text,
                                              height: height,
                                              fieldSelected: fieldSelected,
                                              isValid: isValid,
                                              errorMessageKey: errorMessageKey,
                                              showPlaceholderWithText: showPlaceholderWithText))
        .autocapitalization(.none)
        .simultaneousGesture(TapGesture().onEnded {
            self.fieldSelected = true
        })
        .onAppear {
            setUpKeyboardHideListener()
        }
        .onDisappear {
            NotificationCenter.default.removeObserver(self)
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
        
        self.textBindingManager = TextBindingManager(initialText: text) {
            onTextChange($0)
        }
        
        self.height = height
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.isValid = isValid
        self.errorMessageKey = errorMessageKey
    }
    
    private func setUpKeyboardHideListener() {
        NotificationCenter.default.addObserver(forName: UIResponder.keyboardWillHideNotification,
                                               object: nil,
                                               queue: .main) { _ in
            DispatchQueue.main.async {
                self.fieldSelected = false
            }
        }
    }
}

struct FloatingPlaceholderModifier: ViewModifier {
    let placeholderLocalizedStringKey: String
    let text: String
    
    let height: CGFloat
    
    let fieldSelected: Bool
    let isValid: Bool
    let showPlaceholderWithText: Bool
    let alwaysPlaceholderMoved: Bool
    
    let errorMessageKey: String?
    
    func body(content: Content) -> some View {
        VStack(alignment: .center, spacing: 0) {
            let padding: CGFloat = 16
            
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: 8)
                    .fill(appColor: .white)
                    .frame(height: height)
                
                let placeholderMoved = alwaysPlaceholderMoved ||
                    showPlaceholderWithText && (fieldSelected ||  !text.isEmpty)
                let fontSize: CGFloat = placeholderMoved ? 11 : 15
                let textOffset: CGFloat = placeholderMoved ? -10 : 0
                let textColor: AppColor = placeholderMoved ?
                    (isValid ? .lightBlue : .red) : .placeholderGrey

                LocalizedText(localizationKey: placeholderLocalizedStringKey,
                              fontSize: fontSize,
                              color: textColor)
                    .padding([.leading, .trailing], padding)
                    .offset(y: textOffset)
                    .isHidden(!placeholderMoved && !text.isEmpty)
                    .animation(.easeOut)
                
                let textFieldHeight = height + (placeholderMoved ? textOffset + 4 : 0)
                let textFieldAlignment: Alignment = placeholderMoved ? .bottomLeading : .leading
                content
                    .medicoText(fontSize: 15,
                                multilineTextAlignment: .leading,
                                testingIdentifier: "\(placeholderLocalizedStringKey)_input")
                    .frame(height: textFieldHeight, alignment: textFieldAlignment)
                    .padding([.leading, .trailing], padding)
            }
            
            VStack(alignment: .leading) {
                Rectangle()
                    .fill(appColor: isValid ? .lightBlue : .red)
                    .frame(height: 1)
                    .isHidden(!fieldSelected && (self.errorMessageKey == nil || isValid))
                
                if !isValid, let errorMessageKey = self.errorMessageKey {
                    LocalizedText(localizationKey: errorMessageKey,
                                  fontSize: 12,
                                  color: .red,
                                  multilineTextAlignment: .leading)
                        .padding(.leading, padding)
                }
            }
            .fixedSize(horizontal: false, vertical: true)
        }
    }
    
    init(placeholderLocalizedStringKey: String,
         text: String,
         height: CGFloat,
         fieldSelected: Bool,
         isValid: Bool = true,
         errorMessageKey: String? = nil,
         showPlaceholderWithText: Bool = true,
         alwaysPlaceholderMoved: Bool = false) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        self.text = text
        
        self.height = height
        
        self.fieldSelected =  fieldSelected
        self.isValid = text.isEmpty || isValid
        self.showPlaceholderWithText = showPlaceholderWithText
        
        self.alwaysPlaceholderMoved = alwaysPlaceholderMoved
        self.errorMessageKey = errorMessageKey
    }
}

private class TextBindingManager: ObservableObject {
    private var previousText: String
    
    @Published var text = "" {
        didSet {
            if self.text != self.previousText {
                onTextChange(text)
                
                self.text = self.previousText
            }
        }
    }
    private let onTextChange: (String) -> ()

    init(initialText: String?,
         onTextChange: @escaping (String) -> ()) {
        self.previousText = initialText ?? ""
        self.text = initialText ?? ""
        
        self.onTextChange = onTextChange
    }
}
