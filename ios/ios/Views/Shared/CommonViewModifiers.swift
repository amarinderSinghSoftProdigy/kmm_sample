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

struct ErrorAlert: ViewModifier {
    let errorsHandler: Scope.Host
    
    @ObservedObject var error: SwiftDataSource<DataErrorCode>
    private var showsAlert: Binding<Bool>
    
    init(errorsHandler: Scope.Host) {
        self.errorsHandler = errorsHandler
        
        let error = SwiftDataSource(dataSource: errorsHandler.alertError)
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
                                        withButtonAction: { errorsHandler.dismissAlertError() })
            }
    }
}

struct NotificationAlert: ViewModifier {
    let notificationsHandler: CommonScopeWithNotifications
    let onDismiss: (() -> ())?
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    private var showsAlert: Binding<Bool>
    
    init(notificationsHandler: CommonScopeWithNotifications,
         onDismiss: (() -> ())? = nil) {
        self.notificationsHandler = notificationsHandler
        self.onDismiss = onDismiss
        
        let notification = SwiftDataSource(dataSource: notificationsHandler.notifications)
        self.notification = notification
        
        showsAlert = Binding(get: { notification.value != nil }, set: { _ in })
    }
    
    func body(content: Content) -> some View {
        content
            .alert(isPresented: showsAlert) {
                let title = notification.value?.title ?? ""
                let body = notification.value?.body ?? ""
                
                return content.getAlert(withTitleKey: title,
                                        withMessageKey: body,
                                        withButtonAction: {
                                            notificationsHandler.dismissNotification()
                                            onDismiss?()
                                        })
            }
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
