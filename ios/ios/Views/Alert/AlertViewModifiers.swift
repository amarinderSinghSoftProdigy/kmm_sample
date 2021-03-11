//
//  AlertViewModifiers.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

// MARK: Errors
struct ErrorAlert: View {
    let errorsHandler: Scope.Host
    
    @ObservedObject var error: SwiftDataSource<DataErrorCode>
    
    init(errorsHandler: Scope.Host) {
        self.errorsHandler = errorsHandler
        
        self.error = SwiftDataSource(dataSource: errorsHandler.alertError)
    }
    
    var body: some View {
        ZStack {
            if let error = self.error.value {
                let dismissAction = { errorsHandler.dismissAlertError() }
                
                CustomAlert<AnyView>(titleKey: error.title,
                                     descriptionKey: error.body,
                                     button: .standard(action: dismissAction),
                                     dismissAction: dismissAction)
            }
        }
        .animation(.default)
    }
}

// MARK: Notifications
class NotificationObservable: ObservableObject {
    @Published var data: Data?
    
    class Data {
        let notificationsHandler: CommonScopeWithNotifications
        
        init(notificationsHandler: CommonScopeWithNotifications) {
            self.notificationsHandler = notificationsHandler
        }
    }
}

struct NotificationAlertSender: ViewModifier {
    @EnvironmentObject var notificationObserver: NotificationObservable
    
    let notificationsHandler: CommonScopeWithNotifications
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                self.notificationObserver.data = .init(notificationsHandler: notificationsHandler)
            }
            .onDisappear {
                self.notificationObserver.data = nil
            }
    }
}

struct NotificationAlert: View {
    let notificationsHandler: CommonScopeWithNotifications
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    
    init(notificationsHandler: CommonScopeWithNotifications) {
        self.notificationsHandler = notificationsHandler
        
        self.notification = SwiftDataSource(dataSource: notificationsHandler.notifications)
    }
    
    var body: some View {
        ZStack {
            if let notification = self.notification.value {
                let dismissAction: (() -> ())? = notification.isDismissible ?
                    { _ = notificationsHandler.dismissNotification() } : nil
                
                if notification.isSimple {
                    SimpleNotificationAlert(notification: notification,
                                            dismissAction: dismissAction)
                }
                else {
                    ComplexNotificationAlert(notification: notification,
                                             dismissAction: dismissAction)
                }
            }
        }
        .animation(.default)
    }
}

struct SimpleNotificationAlert: View {
    let notification: ScopeNotification
    let dismissAction: (() -> ())?
    
    var body: some View {
        CustomAlert<AnyView>(titleKey: notification.title,
                             descriptionKey: notification.body,
                             button: .standard(action: dismissAction),
                             dismissAction: dismissAction)
    }
}

// MARK: Custom Alert
struct CustomAlert<Content: View>: View {
    let titleKey: String
    let descriptionKey: String?
    
    let primaryButton: AlertButton
    let dismissAction: (() -> ())?
    
    let customBody: Content?
    
    var body: some View {
        ZStack {
            BlurEffectView()
                .edgesIgnoringSafeArea(.all)
                .onTapGesture {
                    dismissAction?()
                }
            
            VStack(spacing: 0) {
                VStack(spacing: 6) {
                    LocalizedText(localizationKey: titleKey,
                                  textWeight: .semiBold,
                                  fontSize: 17,
                                  color: .black)
                    
                    if let descriptionKey = self.descriptionKey,
                       !descriptionKey.isEmpty {
                        LocalizedText(localizationKey: descriptionKey,
                                      color: .black)
                    }
                }
                .padding(20)
                
                self.customBody

                VStack(spacing: 0) {
                    Separator()
                    
                    Button(action: { primaryButton.action?() }) {
                        ZStack {
                            Color.clear
                            
                            LocalizedText(localizationKey: primaryButton.text,
                                          textWeight: .medium,
                                          fontSize: 17,
                                          color: .lightBlue)
                        }
                    }
                    .frame(height: 44)
                }
            }
            .frame(width: 270)
            .background(AppColor.white.color.opacity(0.82).cornerRadius(13))
        }
    }
    
    init(titleKey: String,
         descriptionKey: String? = nil,
         button: AlertButton,
         dismissAction: (() -> ())? = nil) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.primaryButton = button
        self.dismissAction = dismissAction
        
        self.customBody = nil
    }
    
    init(titleKey: String,
         descriptionKey: String? = nil,
         button: AlertButton,
         dismissAction: (() -> ())? = nil,
         @ViewBuilder content: () -> Content) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.customBody = content()
        
        self.primaryButton = button
        self.dismissAction = dismissAction
    }
    
    struct Separator: View {
        var body: some View {
            AppColor.grey1.color
                .opacity(0.8)
                .frame(height: 0.5)
        }
    }
    
    struct AlertButton {
        let text: String
        let action: (() -> ())?
        
        static func standard(action: (() -> ())?) -> AlertButton {
            AlertButton(text: "okay", action: action)
        }
    }
}