//
//  AlertViewModifiers.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ErrorAlert: ViewModifier {
    @EnvironmentObject var alertData: AlertData
    
    let errorsHandler: Scope.Host
    
    @ObservedObject var error: SwiftDataSource<DataErrorCode>
    
    init(errorsHandler: Scope.Host) {
        self.errorsHandler = errorsHandler
        
        self.error = SwiftDataSource(dataSource: errorsHandler.alertError)
    }
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                error.onValueDidSet = { newValue in
                    var alertData: AlertData.Data? = nil
                    
                    if let value = newValue {
                        alertData = AlertData.Data(titleKey: value.title,
                                                   messageKey: value.body,
                                                   buttonAction: { errorsHandler.dismissAlertError() })
                    }
                    
                    if self.alertData.data != alertData {
                        self.alertData.data = alertData
                    }
                }
            }
    }
}

struct NotificationAlert: ViewModifier {
    @EnvironmentObject var alertData: AlertData
    
    let notificationsHandler: CommonScopeWithNotifications
    let onDismiss: (() -> ())?
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    
    init(notificationsHandler: CommonScopeWithNotifications,
         onDismiss: (() -> ())? = nil) {
        self.notificationsHandler = notificationsHandler
        self.onDismiss = onDismiss
        
        self.notification = SwiftDataSource(dataSource: notificationsHandler.notifications)
    }
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                notification.onValueDidSet = { newValue in
                    var alertData: AlertData.Data? = nil
                    
                    if let value = newValue {
                        alertData = AlertData.Data(titleKey: value.title,
                                                   messageKey: value.body,
                                                   buttonAction: {
                                                    notificationsHandler.dismissNotification()
                                                    onDismiss?()
                                                   })
                    }
                    
                    if self.alertData.data != alertData {
                        self.alertData.data = alertData
                    }
                }
            }
    }
}

class AlertData: ObservableObject {
    @Published var data: Data?
    
    class Data: Identifiable, Equatable {
        let titleKey: String
        let messageKey: String
        let buttonTextKey: String
        let buttonAction: (() -> ())?
        
        init(titleKey: String,
             messageKey: String,
             buttonTextKey: String = "okay",
             buttonAction: (() -> ())? = nil) {
            self.titleKey = titleKey
            self.messageKey = messageKey
            
            self.buttonTextKey = buttonTextKey
            self.buttonAction = buttonAction
        }
        
        static func == (lhs: Data, rhs: Data) -> Bool {
            lhs.titleKey == rhs.titleKey &&
                lhs.messageKey == rhs.messageKey &&
                lhs.buttonTextKey == rhs.buttonTextKey
        }
    }
}
