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
    
    let constText: String?
    
    let text: Binding<String>
    
    let height: CGFloat
    
    let isValid: Bool
    let errorMessageKey: String?
    
    @State var fieldSelected = false
    
    @State private var cursorPosition: Int?
    
    private let disableAutocorrection: Bool
    private let autocapitalization: UITextAutocapitalizationType
    private let textContentType: UITextContentType?
    
    var body: some View {
        HStack {
            if let constText = self.constText,
               !constText.isEmpty {
                LocalizedText(localizationKey: constText,
                              fontSize: 15,
                              multilineTextAlignment: .leading)
            }
            
            TextFieldContainer("",
                               text: text,
                               cursorPosition: $cursorPosition,
                               fieldSelected: $fieldSelected,
                               keyboardType: keyboardType,
                               disableAutocorrection: disableAutocorrection,
                               autocapitalization: autocapitalization,
                               textContentType: textContentType)
                .frame(maxHeight: height)
        }
        .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                              text: text.wrappedValue,
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
         errorMessageKey: String? = nil,
         disableAutocorrection: Bool = false,
         autocapitalization: UITextAutocapitalizationType = .words,
         textContentType: UITextContentType? = nil) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
    
        self.text = Binding(get: { text ?? "" },
                            set: { onTextChange($0) })
        
        self.constText = constText
        
        self.height = height
        self.keyboardType = keyboardType
        
        self.isValid = isValid
        self.errorMessageKey = errorMessageKey
        
        self.disableAutocorrection = disableAutocorrection
        self.autocapitalization = autocapitalization
        self.textContentType = textContentType
    }
}

struct FloatingPlaceholderSecureField: View {
    let placeholderLocalizedStringKey: String
    
    let text: Binding<String>
    let height: CGFloat
    
    let showPlaceholderWithText: Bool
    
    let isValid: Bool
    let errorMessageKey: String?
    
    @State var showsPassword = false
    @State var fieldSelected = false
    
    @State private var cursorPosition: Int?
    
    var body: some View {
        HStack {
            if showsPassword {
                TextFieldContainer("",
                                   text: text,
                                   cursorPosition: $cursorPosition,
                                   keyboardType: .default,
                                   disableAutocorrection: true,
                                   autocapitalization: .none)
                    .frame(maxHeight: height)
            }
            else {
                SecureField("", text: text)
            }
            
            if !text.wrappedValue.isEmpty {
                Button(action: { self.showsPassword.toggle() }) {
                    Image(systemName: self.showsPassword ? "eye" : "eye.slash")
                        .foregroundColor(appColor: .darkBlue)
                }
            }
        }
        .modifier(FloatingPlaceholderModifier(placeholderLocalizedStringKey: placeholderLocalizedStringKey,
                                              text: text.wrappedValue,
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
        
        self.text = Binding(get: { text ?? "" },
                            set: { onTextChange($0) })
        
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
                
                content
                    .medicoText(fontSize: 15,
                                multilineTextAlignment: .leading,
                                testingIdentifier: "\(placeholderLocalizedStringKey)_input")
                    .padding([.leading, .trailing], padding)
                    .padding(.top, placeholderMoved ? 15 : 0)
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

struct TextFieldContainer: UIViewRepresentable {
    private let placeholder: String
    private let text: Binding<String>
    
    private let cursorPosition: Binding<Int>
    private let constTrailingCursor: Bool
    private let fieldSelected: Binding<Bool>?
    
    private let keyboardType: UIKeyboardType
    private let disableAutocorrection: Bool
    private let autocapitalization: UITextAutocapitalizationType
    private let textContentType: UITextContentType?
    
    private let textProperties: TextProperties
    
    init(_ placeholder: String,
         text: Binding<String>,
         cursorPosition: Binding<Int?>,
         constTrailingCursor: Bool = false,
         fieldSelected: Binding<Bool>? = nil,
         keyboardType: UIKeyboardType,
         disableAutocorrection: Bool,
         autocapitalization: UITextAutocapitalizationType,
         textContentType: UITextContentType? = nil,
         textProperties: TextProperties = .init()) {
        self.placeholder = placeholder
        self.text = text
        
        self.cursorPosition = Binding(get: { cursorPosition.wrappedValue ?? text.wrappedValue.count },
                                      set: { cursorPosition.wrappedValue = $0 })
        self.constTrailingCursor = constTrailingCursor
        self.fieldSelected = fieldSelected
        
        self.keyboardType = keyboardType
        self.disableAutocorrection = disableAutocorrection
        self.autocapitalization = autocapitalization
        self.textContentType = textContentType
        
        self.textProperties = textProperties
    }

    func makeCoordinator() -> TextFieldContainer.Coordinator {
        Coordinator(self)
    }

    func makeUIView(context: UIViewRepresentableContext<TextFieldContainer>) -> CustomCursorTextField {
        let textField = CustomCursorTextField(frame: .zero)
        textField.constTrailingCursor = constTrailingCursor
        
        textField.placeholder = placeholder
        textField.text = text.wrappedValue
        textField.delegate = context.coordinator
        
        textField.font = textProperties.font
        textField.textColor = UIColor(named: textProperties.textColorName)
        textField.textAlignment = textProperties.textAlignment
        
        textField.keyboardType = keyboardType
        textField.autocorrectionType = disableAutocorrection ? .no : .yes
        textField.autocapitalizationType = autocapitalization
        
        if let textContentType = self.textContentType {
            textField.textContentType = textContentType
        }

        context.coordinator.setup(textField)

        return textField
    }

    func updateUIView(_ uiView: CustomCursorTextField, context: UIViewRepresentableContext<TextFieldContainer>) {
        let text = self.text.wrappedValue
        uiView.text = text
        
        uiView.textColor = UIColor(named: textProperties.textColorName)
        
        if let newPosition = constTrailingCursor ? uiView.endOfDocument :
            uiView.position(from: uiView.beginningOfDocument, offset: cursorPosition.wrappedValue) {
            uiView.selectedTextRange = uiView.textRange(from: newPosition, to: newPosition)
        }
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: TextFieldContainer

        private var selectedTextRange: UITextRange?
        
        init(_ textFieldContainer: TextFieldContainer) {
            self.parent = textFieldContainer
        }

        func setup(_ textField: UITextField) {
            textField.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
        }

        func textFieldDidBeginEditing(_ textField: UITextField) {
            parent.fieldSelected?.wrappedValue = true
        }
        
        func textFieldDidEndEditing(_ textField: UITextField) {
            parent.fieldSelected?.wrappedValue = false
        }
        
        @objc func textFieldDidChange(_ textField: UITextField) {
            if let selectedRange = textField.selectedTextRange {
                parent.cursorPosition.wrappedValue = textField.offset(from: textField.beginningOfDocument, to: selectedRange.start)
            }
            
            parent.text.wrappedValue = textField.text ?? ""
        }
    }
    
    struct TextProperties {
        let font: UIFont?
        let textColorName: String
        let textAlignment: NSTextAlignment
        
        init() {
            font = UIFont(name: "Barlow-Regular", size: 15)
            textColorName = "DarkBlue"
            textAlignment = .left
        }
        
        init(font: UIFont?,
             textColorName: String,
             textAlignment: NSTextAlignment) {
            self.font = font
            self.textColorName = textColorName
            self.textAlignment = textAlignment
        }
    }
}

class CustomCursorTextField: UITextField {
    var constTrailingCursor: Bool = false
    
    override func closestPosition(to point: CGPoint) -> UITextPosition? {
        constTrailingCursor ? endOfDocument : super.closestPosition(to: point)
    }
}
