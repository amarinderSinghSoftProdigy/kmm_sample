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

struct EnvironmentProperty: RawRepresentable {
    typealias RawValue = String
    
    var rawValue: String
    
    static let testingEnabled = EnvironmentProperty(rawValue: "testingEnabled")
    
    static let scope = EnvironmentProperty(rawValue: "scope")
    
    static let errorTitle = EnvironmentProperty(rawValue: "errorTitle")
    static let errorBody = EnvironmentProperty(rawValue: "errorBody")
    
    static let userType = EnvironmentProperty(rawValue: "userType")
    static let firstName = EnvironmentProperty(rawValue: "firstName")
    static let lastName = EnvironmentProperty(rawValue: "lastName")
    static let email = EnvironmentProperty(rawValue: "email")
    static let phoneNumber = EnvironmentProperty(rawValue: "phoneNumber")
    static let password = EnvironmentProperty(rawValue: "password")
}

class TestsHelper {
    var testingEnabled: Bool {
        let testEnvironment = ProcessInfo.processInfo.arguments
        
        return testEnvironment.contains(EnvironmentProperty.testingEnabled.rawValue)
    }
    
    func overrideCurrentScope() {
        let testEnvironment = ProcessInfo.processInfo.environment
        
        guard let scope = testEnvironment[EnvironmentProperty.scope.rawValue],
              let infoDictionary = Bundle.main.infoDictionary,
              let namespace = infoDictionary["CFBundleExecutable"] as? String,
              let baseScopeInfoClass = NSClassFromString("\(namespace).\(scope)") as? BaseScopeInfo.Type else { return }
        
        baseScopeInfoClass.overrideScope(for: testEnvironment)
    }
}
    
class BaseScopeInfo {
    final class var name: String { String(describing: self) }
    
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
    
    class func overrideScope(for environment: [String: String]) {
        return
    }
    
    class func getScopeInfo(from environment: [String: String]) -> BaseScopeInfo? {
        return BaseScopeInfo(errorCode: getErrorCode(for: environment))
    }
    
    private class func getErrorCode(for testEnvironment: [String: String]) -> DataErrorCode? {
        guard let errorTitle = testEnvironment[EnvironmentProperty.errorTitle.rawValue],
              let errorBody = testEnvironment[EnvironmentProperty.errorBody.rawValue]
            else { return nil }
        
        return DataErrorCode(title: errorTitle, body: errorBody)
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
