//
//  CloudMessagingNotificationsService.swift
//  Medico
//
//  Created by Dasha Gurinovich on 1.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation
import Firebase

class CloudMessagingNotificationsService: NSObject, NotificationsService, MessagingDelegate {
    private let gcmMessageIDKey = "gcm.message_id"
    
    private let messaging = Messaging.messaging()
    
    override init() {
        super.init()
        
        messaging.delegate = self
    }
    
    func setDeviceToken(_ deviceToken: Data) {
        messaging.apnsToken = deviceToken
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")
    }
    
    func handleRemoteNotificationReceive(withUserInfo userInfo: [AnyHashable: Any]) {
        messaging.appDidReceiveMessage(userInfo)

        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }

        // Print full message.
        print(userInfo)
    }
}
                         
