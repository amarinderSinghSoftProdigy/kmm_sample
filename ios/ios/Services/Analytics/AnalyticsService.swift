//
//  AnalyticsService.swift
//  Medico
//
//  Created by Dasha Gurinovich on 13.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation

protocol AnalyticsService: class {
    func logEvent(_ eventName: String, parameters: [String: Any]?)
    
    func logCurrentScreenEvent<T>(_ screenName: String, screenClass: T.Type)
}
