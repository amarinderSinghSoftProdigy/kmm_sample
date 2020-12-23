//
//  OtpPhoneVerificationScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 22.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

/// Parent class to test the OTP phone verification screen in different flows
///
/// - Warning: ALL tests should be executed from the CHILD classes
class OtpPhoneVerificationScreenTests: BaseTests {
    var phoneNumber: String = "+9102222870456"
    
    private let resendTimeout: TimeInterval = 70
    private let attemptsNumber = 3
    
    // MARK: Initial State
    func testInitialState() {
        self.testNavigationBar(withTitleKey: "phone_verification", hasBackButton: true)
        
        
        let timerText = app.staticTexts["timer"]
        XCTAssertTrue(timerText.isHittable)
        
        let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(phoneNumber)
        self.testLocalizedText(withLocalizationKey: "verification_code_sent_hint %@",
                               withParameter: formattedPhone,
                               withElementKey: "verification_code_sent_hint")
        
        self.testLocalizedText(withLocalizationKey: "attempts_left",
                               isShown: false)
        
        self.testFloatingTextField(withLocalizationKey: "verification_code", equals: "")
        
        self.testButton(withLocalizationKey: "submit", isEnabled: false)
        
        self.testLocalizedText(withLocalizationKey: "didnt_get_code")
        self.testLocalizedText(withLocalizationKey: "resend")
        
        let resendText = app.staticTexts["resend"]
        XCTAssertFalse(resendText.isEnabled)
    }
    
    // MARK: Resend
    func testTimerRunOut() {
        let timerText = app.staticTexts["timer"]
        let attemptsLeftText = app.staticTexts["attempts_left"]
        
        waitForElementToDisappear(timerText, timeout: resendTimeout)
        XCTAssertTrue(attemptsLeftText.waitForExistence(timeout: resendTimeout))
    }
    
    func testResendAttempsLeftText() {
        let attemptsLeftText = app.staticTexts["attempts_left"]
        
        XCTAssertTrue(attemptsLeftText.waitForExistence(timeout: resendTimeout))
        
        self.testLocalizedText(withLocalizationKey: "attempts_left %lld",
                               withParameter: String(attemptsNumber),
                               withParameterSymbol: "%lld",
                               withElementKey: "attempts_left")
    }
    
    func testResendTextEnable() {
        let resendText = app.staticTexts["resend"]
        
        waitForElementToChangeEnableState(resendText,
                                          isEnabled: true,
                                          timeout: resendTimeout)
    }
    
    func testResend() {
        let resendText = app.staticTexts["resend"]
        
        waitForElementToChangeEnableState(resendText,
                                          isEnabled: true,
                                          timeout: resendTimeout)
        
        resendText.tap()
        
        self.testInitialState()
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
        let submitButton = app.buttons["submit_button"]
        let getCodeButton = app.buttons["get_code_button"]
        
        self.testBackButton(withHiddenElements: [submitButton],
                            withShownElements: [getCodeButton])
    }

}
