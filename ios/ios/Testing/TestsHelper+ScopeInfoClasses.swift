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
        
        let errorCode: DataErrorCode?
        
        init(errorCode: DataErrorCode? = nil) {
            self.errorCode = errorCode
        }
        
        func getLaunchEnvironment() -> [String: String] {
            var environment = [EnvironmentProperty.scope.rawValue: type(of: self).name]
            
            if let errorCode = self.errorCode {
                environment[EnvironmentProperty.errorTitle.rawValue] = errorCode.title
                environment[EnvironmentProperty.errorBody.rawValue] = errorCode.body
            }
            
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
             password: String,
             errorCode: DataErrorCode? = nil) {
            self.phoneNumberOrEmail = phoneNumberOrEmail
            self.type = type
            self.password = password
            
           super.init(errorCode: errorCode)
        }
        
        override func getLaunchEnvironment() -> [String: String] {
            var environment = super.getLaunchEnvironment()
            
            environment[EnvironmentProperty.phoneNumberOrEmail.rawValue] = phoneNumberOrEmail
            environment[EnvironmentProperty.logInUserNameType.rawValue] = type.rawValue
            environment[EnvironmentProperty.password.rawValue] = password
            
            return environment
        }
    }
    
    class OtpScopeNumberInputInfo: BaseScopeInfo {
        override class var name: String { return "OtpScopeNumberInput" }
        
        let phone: String
        
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
    }
    
    class LimitedAppAccessScopeInfo: BaseScopeInfo {
        override class var name: String { return "LimitedAppAccessScope" }
    
        let firstName: String
        let lastName: String
        let userType: DataUserType
        
        init(firstName: String,
             lastName: String,
             userType: DataUserType) {
            self.firstName = firstName
            self.lastName = lastName
            self.userType = userType
        }
        
        override func getLaunchEnvironment() -> [String: String] {
            var environment = super.getLaunchEnvironment()
            
            environment[EnvironmentProperty.firstName.rawValue] = firstName
            environment[EnvironmentProperty.lastName.rawValue] = lastName
            environment[EnvironmentProperty.userType.rawValue] = userType.name
            
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

extension DataUserType {
    static func getValue(from rawValue: String) -> DataUserType? {
        switch rawValue {
        
        case "STOCKIST":
            return DataUserType.stockist
            
        case "RETAILER":
            return DataUserType.retailer
            
        case "SEASONBOY":
            return DataUserType.seasonBoy
            
        case "HOSPITAL":
            return DataUserType.hospital
            
        default:
            return nil
        }
    }
}
#endif
