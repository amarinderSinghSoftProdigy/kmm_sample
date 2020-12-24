//
//  TestsHelper+LogInScope.swift
//  ios
//
//  Created by Dasha Gurinovich on 24.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

#if DEBUG
import Foundation
import core

extension EnvironmentProperty {
    static let phoneNumberOrEmail = EnvironmentProperty(rawValue: "phoneNumberOrEmail")
    static let logInUserNameType = EnvironmentProperty(rawValue: "logInUserNameType")
}

class LogInScopeInfo: BaseScopeInfo {
    static let testScope = LogInTestScope()
    
    let phoneNumberOrEmail: String
    let type: DataAuthCredentials.Type_
    let password: String
    
    init(phoneNumberOrEmail: String,
         type: DataAuthCredentials.Type_,
         password: String,
         errorCode: DataErrorCode? = nil) {
        self.phoneNumberOrEmail = phoneNumberOrEmail
        self.type = type
        self.password = password
        
       super.init(errorCode: errorCode)
    }
    
    override class func overrideScope(for environment: [String : String]) {
        guard let scopeInfo = getScopeInfo(from: environment) else { return }
        
        testScope.logIn(credentials: DataAuthCredentials(phoneNumberOrEmail: scopeInfo.phoneNumberOrEmail,
                                                         type: scopeInfo.type,
                                                         password: scopeInfo.password),
                        error: scopeInfo.errorCode)
    }
    
    override func getLaunchEnvironment() -> [String: String] {
        var environment = super.getLaunchEnvironment()
        
        environment[EnvironmentProperty.phoneNumberOrEmail.rawValue] = phoneNumberOrEmail
        environment[EnvironmentProperty.logInUserNameType.rawValue] = type.rawValue
        environment[EnvironmentProperty.password.rawValue] = password
        
        return environment
    }
    
    override class func getScopeInfo(from environment: [String: String]) -> LogInScopeInfo? {
        guard let phoneNumberOrEmail = environment[EnvironmentProperty.phoneNumberOrEmail.rawValue]
            else { return nil }
        
        guard let typeStringValue = environment[EnvironmentProperty.logInUserNameType.rawValue],
              let type = DataAuthCredentials.Type_.getValue(from: typeStringValue)
            else { return nil }
        
        guard let password = environment[EnvironmentProperty.password.rawValue]
            else { return nil }
        
        let baseScopeInfo = super.getScopeInfo(from: environment)
        
        return LogInScopeInfo(phoneNumberOrEmail: phoneNumberOrEmail,
                              type: type,
                              password: password,
                              errorCode: baseScopeInfo?.errorCode)
    }
}
#endif
