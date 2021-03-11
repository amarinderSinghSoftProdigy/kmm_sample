//
//  CommonViewModifiers.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct ScrollViewModifier: ViewModifier {
    @State private var inputFieldsHeight: CGFloat
    
    func body(content: Content) -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            content
            .background(GeometryReader { gp -> Color in
                let frame = gp.frame(in: .local)
                DispatchQueue.main.async {
                    self.inputFieldsHeight = frame.height
                }
                return Color.clear
            })
        }.frame(maxHeight: inputFieldsHeight)
    }
    
    init(initialInputFieldsHeight: CGFloat = 350) {
        self._inputFieldsHeight = State(initialValue: initialInputFieldsHeight)
    }
}

struct FieldError: ViewModifier {
    let padding: CGFloat
    let errorMessageKey: String?
    
    func body(content: Content) -> some View {
        VStack(spacing: 0) {
            content
            
            if let errorMessageKey = self.errorMessageKey {
                VStack(alignment: .leading) {
                    Rectangle()
                        .fill(appColor: .red)
                        .frame(height: 1)
                
                    LocalizedText(localizationKey: errorMessageKey,
                                  fontSize: 12,
                                  color: .red,
                                  multilineTextAlignment: .leading)
                    .padding(.leading, padding)
                }
            }
        }
    }
    
    init(errorMessageKey: String?, padding: CGFloat = 16) {
        self.errorMessageKey = errorMessageKey
        
        self.padding = padding
    }
}

struct TestingIdentifier: ViewModifier {
    let identifier: String
    
    func body(content: Content) -> some View {
        #if DEBUG
        return AnyView(content.accessibility(identifier: identifier))
        #endif
        
        return AnyView(content)
    }
}

struct ScreenLoggerModifier<T>: ViewModifier {
    @EnvironmentObject var screenLogger: ScreenLogger
    
    let screenName: String
    let screenClass: T.Type
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                screenLogger.logCurrentScreen(screenName,
                                              screenClass: screenClass)
            }
    }
}

struct CustomPlaceholderTextField<Content: View>: View {
    let text: Binding<String>
    
    let placeholder: Content
    
    let textWeight: TextWeight
    let fontSize: CGFloat
    let color: AppColor
    let elementsAlignment: Alignment
    
    let onEditingChanged: ((Bool) -> ())?
    
    var body: some View {
        ZStack(alignment: elementsAlignment) {
            if text.wrappedValue.isEmpty {
                placeholder
            }
            
            TextField("", text: text, onEditingChanged: { onEditingChanged?($0) })
                .medicoText(textWeight: textWeight,
                            fontSize: fontSize,
                            color: color,
                            multilineTextAlignment: elementsAlignment == .center ? .center : .leading)
        }
    }
    
    init(text: Binding<String>,
         textWeight: TextWeight = .regular,
         fontSize: CGFloat = 14,
         color: AppColor = .darkBlue,
         elementsAlignment: Alignment = .leading,
         onEditingChanged: ((Bool) -> ())? = nil,
         @ViewBuilder placeholder: () -> Content) {
        self.text = text
        
        self.textWeight = textWeight
        self.fontSize = fontSize
        self.color = color
        self.elementsAlignment = elementsAlignment
        
        self.onEditingChanged = onEditingChanged
        
        self.placeholder = placeholder()
    }
}
