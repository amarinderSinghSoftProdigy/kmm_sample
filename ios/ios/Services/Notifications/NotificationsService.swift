//
//  NotificationsService.swift
//  Medico
//
//  Created by Dasha Gurinovich on 1.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation

protocol NotificationsService {
    func setDeviceToken(_ deviceToken: Data)
    
    func handleRemoteNotificationReceive(withUserInfo userInfo: [AnyHashable: Any])
}
