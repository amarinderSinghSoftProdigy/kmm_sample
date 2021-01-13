//
//  ScreenLogger.swift
//  Medico
//
//  Created by Dasha Gurinovich on 13.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation

class ScreenLogger: ObservableObject {
    private let analyticsService = DIContainer.shared.resolve(type: AnalyticsService.self)
    
    private var previousScreenName: String?
    private var previousScreenClass: String?
    
    func logCurrentScreen<T>(_ screenName: String, screenClass: T.Type) {
        let screenClassString = "\(screenClass)"
        guard previousScreenName != screenName || previousScreenClass != screenClassString else { return }
        
        analyticsService?.logCurrentScreenEvent(screenName, screenClass: screenClass)
        
        self.previousScreenName = screenName
        self.previousScreenClass = screenClassString
    }
}
