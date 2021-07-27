//
//  FirebaseAnalyticsService.swift
//  Medico
//
//  Created by Dasha Gurinovich on 13.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation
import FirebaseAnalytics

class FirebaseAnalyticsService: AnalyticsService {
    func logEvent(_ eventName: String, parameters: [String: Any]?) {
        Analytics.logEvent(eventName, parameters: parameters)
    }
    
    func logCurrentScreenEvent<T>(_ screenName: String, screenClass: T.Type) {
        self.logEvent(AnalyticsEventScreenView,
                      parameters: [AnalyticsParameterScreenName: screenName,
                                   AnalyticsParameterScreenClass: "\(screenClass)"])
    }
}
