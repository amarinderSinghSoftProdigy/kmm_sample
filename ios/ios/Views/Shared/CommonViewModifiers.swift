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
                
                Text(LocalizedStringKey(errorMessageKey))
                    .modifier(MedicoText(fontSize: 12, color: .red, multilineTextAlignment: .leading))
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

struct ErrorAlert: ViewModifier {
    let errorsHandler: WithErrors
    
    @ObservedObject var error: SwiftDatasource<DataErrorCode>
    private var showsAlert: Binding<Bool>
    
    init(errorsHandler: WithErrors) {
        self.errorsHandler = errorsHandler
        
        let error = SwiftDatasource(dataSource: errorsHandler.errors)
        self.error = error
        
        showsAlert = Binding(get: { error.value != nil }, set: { _ in })
    }
    
    func body(content: Content) -> some View {
        content
            .alert(isPresented: showsAlert) {
                let title = self.error.value?.title ?? "error"
                let body = self.error.value?.body ?? "something_went_wrong"
                
                return content.getAlert(withTitleKey: title,
                                        withMessageKey: body,
                                        withButtonAction: { errorsHandler.dismissError() })
            }
    }
}

struct NotificationAlertHandler: ViewModifier {
    private var notification: SwiftDatasource<ScopeNotification>
    private var showsAlert: Binding<Bool>
    
    init(notification: SwiftDatasource<ScopeNotification>) {
        self.notification = notification
        
        showsAlert = Binding(get: { notification.value != nil }, set: { _ in })
    }
    
    func body(content: Content) -> some View {
        content
            .alert(isPresented: showsAlert) {
                let title = "error"
                let body = "something_went_wrong"
                
                return content.getAlert(withTitleKey: title,
                                        withMessageKey: body)
            }
    }
}
