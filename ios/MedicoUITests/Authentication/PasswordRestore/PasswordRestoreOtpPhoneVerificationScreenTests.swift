//
//  OtpPhoneVerificationScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 22.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

class PasswordRestoreOtpPhoneVerificationScreenTests: OtpPhoneVerificationScreenTests {
    override func launchApp(with environment: [String : String]? = nil) {
        let phoneInputInfo = OtpScopePhoneNumberInputInfo(phone: phoneNumber)
        
        super.launchApp(with: phoneInputInfo.getLaunchEnvironment())
        
        let getCodeButton = app.buttons["get_code_button"]
        getCodeButton.tap()
        
        let activityView = app.otherElements["ActivityView"]
        waitForElementToDisappear(activityView, timeout: 5)
    }
    
    override func testBackButton() {
        super.testBackButton()
        
        let formattedNumber = PhoneNumberUtil.shared.getFormattedPhoneNumber(phoneNumber)
        self.testFloatingTextField(withLocalizationKey: "phone_number", equals: formattedNumber)
    }
}
