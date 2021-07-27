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
                                     outsideTapAction: dismissAction)
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
                let dismissAction: (Bool) -> () = { outsideTap in
                    if notification.isDismissible || !outsideTap {
                        notificationsHandler.dismissNotification()
                    }
                }
                
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
    let dismissAction: (_ outsideTap: Bool) -> ()
    
    var body: some View {
        CustomAlert<AnyView>(titleKey: notification.title ?? "",
                             descriptionKey: notification.body,
                             button: .standard(action: { dismissAction(false) }),
                             outsideTapAction: { dismissAction(true) })
    }
}

// MARK: Custom Alert
struct CustomAlert<Content: View>: View {
    let localizedStringKey: LocalizedStringKey
    let descriptionKey: String?
    
    let primaryButton: AlertButton
    let cancelButton: AlertButton?
    let outsideTapAction: (() -> ())?
    
    let customBody: Content?
    
    var body: some View {
        ZStack {
            BlurEffectView()
                .edgesIgnoringSafeArea(.all)
                .onTapGesture {
                    outsideTapAction?()
                }
            
            VStack(spacing: 0) {
                VStack(spacing: 6) {
                    LocalizedText(localizedStringKey: localizedStringKey,
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
                .fixedSize(horizontal: false, vertical: true)
                
                self.customBody

                VStack(spacing: 0) {
                    Separator()
                    
                    HStack(spacing: 0) {
                        if let cancelButton = self.cancelButton {
                            AlertButtonView(alertButton: cancelButton)
                            
                            Divider()
                        }
                        
                        AlertButtonView(alertButton: primaryButton)
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
         cancelButton: AlertButton? = nil,
         outsideTapAction: (() -> ())? = nil,
         content: (() -> Content)? = nil) {
       self.init(localizedStringKey: LocalizedStringKey(titleKey),
                 descriptionKey: descriptionKey,
                 button: button,
                 cancelButton: cancelButton,
                 outsideTapAction: outsideTapAction,
                 content: content)
    }
    
    init(localizedStringKey: LocalizedStringKey,
         descriptionKey: String? = nil,
         button: AlertButton,
         cancelButton: AlertButton? = nil,
         outsideTapAction: (() -> ())? = nil,
         content: (() -> Content)? = nil) {
       self.localizedStringKey = localizedStringKey
       self.descriptionKey = descriptionKey
       
       self.primaryButton = button
       self.cancelButton = cancelButton
       self.outsideTapAction = outsideTapAction
       
       self.customBody = content?()
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
        let isEnabled: Bool
        let action: (() -> ())?
        
        static func standard(action: (() -> ())?) -> AlertButton {
            AlertButton(text: "okay",
                        isEnabled: true,
                        action: action)
        }
    }
    
    struct AlertButtonView: View {
        let alertButton: AlertButton
        
        var body: some View {
            Button(action: { alertButton.action?() }) {
                ZStack {
                    Color.clear
                    
                    LocalizedText(localizationKey: alertButton.text,
                                  textWeight: .medium,
                                  fontSize: 17,
                                  color: alertButton.isEnabled ? .lightBlue : .lightGrey)
                }
            }
            .disabled(!alertButton.isEnabled)
        }
    }
}
