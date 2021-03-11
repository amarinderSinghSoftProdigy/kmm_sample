//
//  NotificationsManager.swift
//  Medico
//
//  Created by Dasha Gurinovich on 1.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation
import core
import NotificationCenter

class NotificationsManager {
    private let notificationId = "id"
    private let cloudMessagingNotificationsService = CloudMessagingNotificationsService()
    
    let firebaseMessaging: FirebaseMessaging
    
    init(firebaseMessaging: FirebaseMessaging) {
        self.firebaseMessaging = firebaseMessaging
        
        self.cloudMessagingNotificationsService.delegate = self
        
        firebaseMessaging.notificationMessage.observeOnUi { newValue in
            if let notification = newValue { self.showNotification(notification) }
        }
    }
    
    func setDeviceToken(_ token: Data) {
        cloudMessagingNotificationsService.setDeviceToken(token)
    }
    
    func handleNotificationFetch(withUserInfo userInfo: [AnyHashable: Any]) {
        if let data = userInfo as? [String: Any] {
            firebaseMessaging.handleMessage(data: data)
        }
    }
    
    func handleNotificationReceive(withUserInfo userInfo: [AnyHashable: Any]) {
        cloudMessagingNotificationsService.handleRemoteNotificationReceive(withUserInfo: userInfo)
    }
    
    func handleNotificationTap(withUserInfo userInfo: [AnyHashable: Any]) {
        if let messageId = userInfo[notificationId] as? String {
            firebaseMessaging.dismissMessage(id: messageId)
        }
    }
    
    private func showNotification(_ notificationData: NotificationMessage) {
        let content = UNMutableNotificationContent()
        content.title = notificationData.title
        content.body = notificationData.body
        content.userInfo = [notificationId: notificationData.id]

        let trigger = UNTimeIntervalNotificationTrigger.init(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest.init(identifier: notificationData.id,
                                                 content: content,
                                                 trigger: trigger)

        let center = UNUserNotificationCenter.current()
        center.add(request)
    }
}

extension NotificationsManager: NotificationsServiceDelegate {
    func handleTokenReceive(_ token: String?) {
        guard let newToken = token else { return }
        
        firebaseMessaging.handleNewToken(token: newToken)
    }
}
