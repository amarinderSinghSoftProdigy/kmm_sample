//
//  OtpPhoneVerificationScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 22.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

class OtpPhoneVerificationScreenTests: BaseTests {

    let phoneNumber = "+9102222870456"
    
    override func launchApp(with environment: [String : String]? = nil) {
        let phoneVerificationInfo = TestsHelper.OtpAwaitVerificationInputInfo(phone: phoneNumber)
        
        super.launchApp(with: phoneVerificationInfo.getLaunchEnvironment())
    }

    // MARK: Initial State
    func testInitialState() {
        self.testNavigationBar(withTitleKey: "phone_verification", hasBackButton: true)
        
        let localizedKey = "verification_code_sent_hint %@"
        let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(phoneNumber)
        let localizedHint = self.getLocalizedString(for: localizedKey)
            .replacingOccurrences(of: "%@", with: formattedPhone)
        
        let timerText = app.staticTexts["timer"]
        XCTAssertTrue(timerText.isHittable)
        
        let attemptsLeftText = app.staticTexts["attempts_left"]
        XCTAssertFalse(attemptsLeftText.isHittable)
        
        let hintText = app.staticTexts["verification_code_sent_hint"]
        XCTAssertTrue(hintText.isHittable)
        XCTAssertTrue(hintText.label == localizedHint)
        XCTAssertFalse(hintText.label == localizedKey)
        
        self.testFloatingTextField(with: "verification_code", equals: "")
        
        self.testButton(with: "submit", isEnabled: false)
        
        self.testLocalizedText(with: "didnt_get_code")
        self.testLocalizedText(with: "resend")
    }

    // MARK: Actions
    func testSubmit() {
        let verificationCodeInput = app.textFields["verification_code_input"]
        
        verificationCodeInput.tap()
        verificationCodeInput.typeText("1234")
        
        let submitButton = app.buttons["submit_button"]
        submitButton.tap()
        
        self.testActivityView(activityViewShown: true,
                              withHiddenElements: [submitButton],
                              withShownElements: [XCUIElement]())
    }
    
    func testBackButton() {
        let getCodeButton = app.buttons["get_code_button"]
        self.testBackButton(withShownElements: [getCodeButton])
    }
}
