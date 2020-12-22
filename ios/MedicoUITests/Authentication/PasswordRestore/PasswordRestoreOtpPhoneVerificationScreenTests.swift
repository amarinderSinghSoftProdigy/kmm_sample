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
        let phoneInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: phoneNumber)
        
        super.launchApp(with: phoneInputInfo.getLaunchEnvironment())
        
        let getCodeButton = app.buttons["get_code_button"]
        getCodeButton.tap()
        
        let activityView = app.otherElements["ActivityView"]
        waitForElementToDisappear(activityView, timeout: 5)
    }
}
