//
//  TestsHelper+ScopeInfoClasses.swift
//  ios
//
//  Created by Dasha Gurinovich on 16.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

#if DEBUG
import Foundation
import core

extension TestsHelper {
    class BaseScopeInfo {
        class var name: String { return "BaseScope" }
        
        func getLaunchEnvironment() -> [String: String] {
            let environment = [EnvironmentProperty.scope.rawValue: type(of: self).name]
            
            return environment
        }
    }
    
    class LogInScopeInfo: BaseScopeInfo {
        override class var name: String { return "LogInScope" }
        
        let phoneNumberOrEmail: String
        let type: DataAuthCredentials.Type_
        let password: String
        
        init(phoneNumberOrEmail: String,
             type: DataAuthCredentials.Type_,
             password: String) {
            self.phoneNumberOrEmail = phoneNumberOrEmail
            self.type = type
            self.password = password
        }
        
        override func getLaunchEnvironment() -> [String: String] {
            var environment = super.getLaunchEnvironment()
            
            environment[EnvironmentProperty.phoneNumberOrEmail.rawValue] = phoneNumberOrEmail
            environment[EnvironmentProperty.logInUserNameType.rawValue] = type.rawValue
            environment[EnvironmentProperty.password.rawValue] = password
            
            return environment
        }
    }
}

extension DataAuthCredentials.Type_ {
    var rawValue: String {
        switch self {
        
        case .email:
            return "email"
            
        case .phone:
            return "phone"
            
        default:
            return ""
        }
    }
    
    static func getValue(from rawValue: String) -> DataAuthCredentials.Type_? {
        switch rawValue {
        
        case "email":
            return .email
            
        case "phone":
            return .phone
            
        default:
            return nil
        }
    }
}
#endif
