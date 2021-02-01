//
//  DIContainer.swift
//  Medico
//
//  Created by Dasha Gurinovich on 13.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import Foundation

protocol DIContainerProtocol {
  func register<Component>(type: Component.Type, component: Any)
  func resolve<Component>(type: Component.Type) -> Component?
}

final class DIContainer: DIContainerProtocol {
    static let shared = DIContainer()
  
    private init() {}
    
    var components: [String: Any] = [:]

    func initialize() {
        self.register(type: AnalyticsService.self, component: FirebaseAnalyticsService())
        self.register(type: NotificationsService.self, component: CloudMessagingNotificationsService())
    }
    
    func register<Component>(type: Component.Type, component: Any) {
        components["\(type)"] = component
    }

    func resolve<Component>(type: Component.Type) -> Component? {
        return components["\(type)"] as? Component
    }
}
