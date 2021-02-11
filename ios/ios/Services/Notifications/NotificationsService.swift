//
//  NotificationsService.swift
//  Medico
//
//  Created by Dasha Gurinovich on 1.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation
import core
import NotificationCenter

class NotificationsService {
    let firebaseMessaging: FirebaseMessaging
    
    init(firebaseMessaging: FirebaseMessaging) {
        self.firebaseMessaging = firebaseMessaging
        
        firebaseMessaging.notifications.observeOnUi { newValue in
            if let notification = newValue { self.showNotification(notification) }
        }
    }
    
    func setDeviceToken(_ token: Data) {
//        firebaseMessaging.handleNewToken(token: token.base64EncodedString())
    }
    
    func handleNotificationTap(withUserInfo userInfo: [AnyHashable: Any]) {
        print(userInfo)
    }
    
    private func showNotification(_ notificationData: NotificationMessage) {
        let content = UNMutableNotificationContent()
        content.title = notificationData.title
        content.body = notificationData.body

        let trigger = UNTimeIntervalNotificationTrigger.init(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest.init(identifier: notificationData.id,
                                                 content: content,
                                                 trigger: trigger)

        let center = UNUserNotificationCenter.current()
        center.add(request)
    }
}
