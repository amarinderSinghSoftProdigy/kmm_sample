//
//  TestsHelper+OtpScope.swift
//  ios
//
//  Created by Dasha Gurinovich on 24.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

#if DEBUG
import Foundation
import core

class OtpScopeInfo: BaseScopeInfo {
    let phone: String
    
    static let otpTestScope = OtpTestScope()
    
    init(phone: String,
         errorCode: DataErrorCode? = nil) {
        self.phone = phone
        
        super.init(errorCode: errorCode)
    }
    
    override func getLaunchEnvironment() -> [String: String] {
        var environment = super.getLaunchEnvironment()
        
        environment[EnvironmentProperty.phoneNumber.rawValue] = phone
        
        return environment
    }
    
    override class func getScopeInfo(from environment: [String: String]) -> OtpScopeInfo? {
        guard let phoneNumber = environment[EnvironmentProperty.phoneNumber.rawValue]
            else { return nil }
    
        let baseScopeInfo = super.getScopeInfo(from: environment)
        
        return OtpScopeInfo(phone: phoneNumber,
                            errorCode: baseScopeInfo?.errorCode)
    }
}

class OtpScopePhoneNumberInputInfo: OtpScopeInfo {
    override class func overrideScope(for environment: [String: String]) {
        guard let scopeInfo = OtpScopePhoneNumberInputInfo.getScopeInfo(from: environment)
            else { return }
        
        otpTestScope.phoneNumberInput(phoneNumber: scopeInfo.phone,
                                      isForRegisteredUsers: false,
                                      error: scopeInfo.errorCode)
    }
}

#endif
