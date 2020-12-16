//
//  TestsHelper.swift
//  ios
//
//  Created by Dasha Gurinovich on 16.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

#if DEBUG
import Foundation
import core

class TestsHelper {
    
    enum EnvironmentProperty: String {
        case scope
        
        case phoneNumberOrEmail
        case logInUserNameType
        case password
    }
    
    func getCurrentScope() -> BaseScope? {
        let testEnvironment = ProcessInfo.processInfo.environment
        
        guard let scope = testEnvironment[EnvironmentProperty.scope.rawValue] else { return nil }
        
        switch scope {
        case LogInScopeInfo.name:
            return getLogInScope(for: testEnvironment)
            
        default:
            return nil
        }
    }
    
    private func getLogInScope(for testEnvironment: [String: String]) -> LogInScope? {
        guard let phoneNumberOrEmail = testEnvironment[EnvironmentProperty.phoneNumberOrEmail.rawValue]
            else { return nil }
        
        guard let typeStringValue = testEnvironment[EnvironmentProperty.logInUserNameType.rawValue],
              let type = DataAuthCredentials.Type_.getValue(from: typeStringValue)
            else { return nil }
        
        guard let password = testEnvironment[EnvironmentProperty.password.rawValue]
            else { return nil }
        
        let credentials = DataAuthCredentials(phoneNumberOrEmail: phoneNumberOrEmail,
                                              type: type,
                                              password: password)
        
        let logInScope = LogInScope(credentials: DataSource(initialValue: credentials),
                                    errors: DataSource(initialValue: nil))
        
        return logInScope
    }
}
#endif
