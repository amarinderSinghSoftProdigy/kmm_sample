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
        case testingEnabled
        
        case scope
        
        case errorTitle
        case errorBody
        
        case phoneNumberOrEmail
        case logInUserNameType
        case password
        
        case firstName
        case lastName
        case email
        case phoneNumber
        case userType
        
        case resendTimer
    }
    
    let scopeCreator = ScopeCreator.Shortcuts()
    
    var testingEnabled: Bool {
        let testEnvironment = ProcessInfo.processInfo.arguments
        
        return testEnvironment.contains(EnvironmentProperty.testingEnabled.rawValue)
    }
    
    func overrideCurrentScope() {
        let testEnvironment = ProcessInfo.processInfo.environment
        
        guard let scope = testEnvironment[EnvironmentProperty.scope.rawValue] else { return }
        
        switch scope {
        case LogInScopeInfo.name:
            setLogInScope(for: testEnvironment)
            
        case OtpScopeNumberInputInfo.name:
            setOtpNumberInputScope(for: testEnvironment)
            
        case LimitedAppAccessScopeInfo.name:
            setLimitedAppAccessScope(for: testEnvironment)
            
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
                                         password: password,
                                         error: getErrorCode(for: testEnvironment))
    }
    
    private func setOtpNumberInputScope(for testEnvironment: [String: String]) {
        guard let phoneNumber = testEnvironment[EnvironmentProperty.phoneNumber.rawValue]
            else { return }
        
        scopeCreator.createOtpPhoneNumberInputShortcut(phoneNumber: phoneNumber,
                                                       error: getErrorCode(for: testEnvironment))
    }
    
    private func setLimitedAppAccessScope(for testEnvironment: [String: String]) {
        guard let firstName = testEnvironment[EnvironmentProperty.firstName.rawValue]
            else { return }
        
        guard let lastName = testEnvironment[EnvironmentProperty.lastName.rawValue]
            else { return }
    
        guard let typeString = testEnvironment[EnvironmentProperty.userType.rawValue],
              let type = DataUserType.getValue(from: typeString)
            else { return }
        
        scopeCreator.createLimitedAppAccessShortcut(firstName: firstName,
                                                    lastName: lastName,
                                                    type: type,
                                                    isDocumentUploaded: true)
    }
    
    private func getErrorCode(for testEnvironment: [String: String]) -> DataErrorCode? {
        guard let errorTitle = testEnvironment[EnvironmentProperty.errorTitle.rawValue],
              let errorBody = testEnvironment[EnvironmentProperty.errorBody.rawValue]
            else { return nil }
        
        return DataErrorCode(title: errorTitle, body: errorBody)
    }
}
#endif
