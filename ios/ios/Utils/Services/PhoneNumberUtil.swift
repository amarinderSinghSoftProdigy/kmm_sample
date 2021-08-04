//
//  PhoneNumberUtil.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import Foundation
import libPhoneNumber_iOS

class PhoneNumberUtil {
    private let phoneUtil = NBPhoneNumberUtil()
    
    private let region: String
    
    private let numberFormat: NBEPhoneNumberFormat = .INTERNATIONAL
    
    static let shared = PhoneNumberUtil()
    
    private init() {
        #if PROD
        region = "IN"
        #else
        region = Locale.current.regionCode ?? "IN"
        #endif
    }
    
    func isValidNumber(_ phoneNumberString: String) -> (isValid: Bool, formattedNumber: String) {
        guard let phoneNumber = try? phoneUtil.parse(phoneNumberString, defaultRegion: region),
              let formattedNumber = try? phoneUtil.format(phoneNumber, numberFormat: numberFormat) else {
            return (false, phoneNumberString)
        }
        
        let isValid = phoneUtil.isValidNumber(forRegion: phoneNumber, regionCode: region)
        
        return (isValid, isValid ? formattedNumber : phoneNumberString)
    }
    
    func getRawPhoneNumber(_ phoneNumberString: String) -> String {
        guard let phoneNumber = try? phoneUtil.parse(phoneNumberString, defaultRegion: region),
              let rawNumber = try? phoneUtil.format(phoneNumber, numberFormat: .E164) else { return phoneNumberString }
        
        return rawNumber.replacingOccurrences(of: "+", with: "")
    }
    
    func getFormattedPhoneNumber(_ phoneNumberString: String) -> String {
        return isValidNumber(phoneNumberString).formattedNumber
    }
}
