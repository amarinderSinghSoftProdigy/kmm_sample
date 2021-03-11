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

extension EnvironmentProperty {
    static let isDocumentUploaded = EnvironmentProperty(rawValue: "isDocumentUploaded")
}

class MainScopeInfo: BaseScopeInfo {
    static let testScope = MainTestScope()
}

class LimitedAppAccessScopeInfo: MainScopeInfo {
    let firstName: String
    let lastName: String
    
    let userType: DataUserType
    
    let aadhaarNumber: String?
    
    let isDocumentUploaded: Bool
    
    init(firstName: String,
         lastName: String,
         userType: DataUserType,
         aadhaarNumber: String? = nil,
         isDocumentUploaded: Bool) {
        self.firstName = firstName
        self.lastName = lastName
        
        self.userType = userType
        
        self.aadhaarNumber = aadhaarNumber
        
        self.isDocumentUploaded = isDocumentUploaded
    }
    
    override func getLaunchEnvironment() -> [String: String] {
        var environment = super.getLaunchEnvironment()
        
        environment[EnvironmentProperty.firstName.rawValue] = firstName
        environment[EnvironmentProperty.lastName.rawValue] = lastName
        environment[EnvironmentProperty.userType.rawValue] = userType.name
        environment[EnvironmentProperty.isDocumentUploaded.rawValue] = String(isDocumentUploaded)
        
        if let aadhaarNumber = self.aadhaarNumber {
            environment[EnvironmentProperty.aadhaarNumber.rawValue] = aadhaarNumber
        }
        
        return environment
    }
    
    override class func overrideScope(for environment: [String: String]) {
        guard let scopeInfo = getScopeInfo(from: environment) else { return }
        
        let details: DataUser.Details
        if scopeInfo.userType == .seasonBoy {
            let cardNumber = environment[EnvironmentProperty.aadhaarNumber.rawValue] ?? ""
            
            details = DataUser.DetailsAadhaar(cardNumber: cardNumber, shareCode: "")
        }
        else {
            details = DataUser.DetailsDrugLicense(tradeName: "Trade",
                                                  gstin: "37AADCB2230M2ZR",
                                                  license1: "20B 398",
                                                  license2: "21B 398",
                                                  url: nil)
        }
        
        let user = DataUser(firstName: scopeInfo.firstName,
                            lastName: scopeInfo.lastName,
                            email: "email@example.com",
                            phoneNumber: "1234567890",
                            unitCode: "",
                            type: scopeInfo.userType,
                            details: details,
                            isActivated: false,
                            isDocumentUploaded: scopeInfo.isDocumentUploaded,
                            addressData: DataCustomerAddressData(address: "",
                                                                 city: "",
                                                                 district: "",
                                                                 latitude: 0,
                                                                 location: "",
                                                                 longitude: 0,
                                                                 pincode: 520001,
                                                                 placeId: "",
                                                                 state: ""))

        if scopeInfo.userType == .seasonBoy {
            testScope.limitedAccessSeasonBoy(user: user,
                                             error: scopeInfo.errorCode)
        }
        else {
            testScope.limitedAccessNonSeasonBoy(user: user,
                                                error: scopeInfo.errorCode)
        }
    }

    override class func getScopeInfo(from environment: [String: String]) -> LimitedAppAccessScopeInfo? {
        guard let firstName = environment[EnvironmentProperty.firstName.rawValue]
            else { return nil }
        
        guard let lastName = environment[EnvironmentProperty.lastName.rawValue]
            else { return nil }
    
        guard let typeString = environment[EnvironmentProperty.userType.rawValue],
              let type = DataUserType.getValue(from: typeString)
            else { return nil }
        
        guard let isDocumentUploadedString = environment[EnvironmentProperty.isDocumentUploaded.rawValue],
              let isDocumentUploaded = Bool(isDocumentUploadedString)
            else { return nil }
        
        return LimitedAppAccessScopeInfo(firstName: firstName,
                                         lastName: lastName,
                                         userType: type,
                                         isDocumentUploaded: isDocumentUploaded)
    }
}
#endif
