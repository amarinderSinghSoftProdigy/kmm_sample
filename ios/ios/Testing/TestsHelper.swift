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
    
    let scopeCreator = ScopeCreator.Shortcuts()
    
    func overrideCurrentScope() {
        let testEnvironment = ProcessInfo.processInfo.environment
        
        guard let scope = testEnvironment[EnvironmentProperty.scope.rawValue] else { return }
        
        switch scope {
        case LogInScopeInfo.name:
            setLogInScope(for: testEnvironment)
            
        default:
            return
        }
    }
    
    private func setLogInScope(for testEnvironment: [String: String]) {
        guard let phoneNumberOrEmail = testEnvironment[EnvironmentProperty.phoneNumberOrEmail.rawValue]
            else { return }
        
        guard let typeStringValue = testEnvironment[EnvironmentProperty.logInUserNameType.rawValue],
              let type = DataAuthCredentials.Type_.getValue(from: typeStringValue)
            else { return }
        
        guard let password = testEnvironment[EnvironmentProperty.password.rawValue]
            else { return }
        
        scopeCreator.createLogInShortcut(phoneNumberOrEmail: phoneNumberOrEmail,
                                         type: type,
                                         password: password)
    }
}
#endif
