//
//  OtpPhoneRequestScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 21.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
import core

class OtpPhoneRequestScreenTests: BaseTests {

    let phoneNumber = "+9102222870456"
    
    override func setUp() {
        self.continueAfterFailure = false
    }
    
    // MARK: Initial State
    func testInitialStateWithInvalidPhoneNumber() {
        let otpNumberInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: "")
        self.launchApp(with: otpNumberInputInfo.getLaunchEnvironment())
        
        self.testNavigationBar(withTitleKey: "password_reset", hasBackButton: true)
        
        self.testLocalizedText(with: "reset_password_hint")
        
        self.testFloatingTextField(with: "phone_number", equals: otpNumberInputInfo.phone)
        
        self.testButton(with: "get_code", isEnabled: false)
    }
    
    func testInitialStateWithValidPhoneNumber() {
        let otpNumberInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: phoneNumber)
        self.launchApp(with: otpNumberInputInfo.getLaunchEnvironment())
        
        self.testNavigationBar(withTitleKey: "password_reset", hasBackButton: true)
        
        self.testLocalizedText(with: "reset_password_hint")
        
        let formattedNumber = PhoneNumberUtil.shared.getFormattedPhoneNumber(otpNumberInputInfo.phone)
        self.testFloatingTextField(with: "phone_number", equals: formattedNumber)
        
        self.testButton(with: "get_code", isEnabled: true)
    }
    
    // MARK: Button Actions
    func testBackButton() {
        let otpNumberInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: "")
        self.launchApp(with: otpNumberInputInfo.getLaunchEnvironment())
        
        let getCodeButton = app.buttons["get_code_button"]
        let logInButton = app.buttons["log_in_button"]
        self.testBackButton(withHiddenElements: [getCodeButton],
                            withShownElements: [logInButton])
    }
    
    func testSendOtpButton() {
        let otpNumberInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: phoneNumber)
        self.launchApp(with: otpNumberInputInfo.getLaunchEnvironment())
        
        let getCodeButton = app.buttons["get_code_button"]
        getCodeButton.tap()
        
        let verificationCodeHint = app.staticTexts["verification_code_sent_hint"]
        
        self.testActivityView(activityViewShown: true,
                              withHiddenElements: [getCodeButton],
                              withShownElements: [verificationCodeHint])
    }
    
    // MARK: Number Enter
    func testNonIndianNumberEnter() {
        self.testNumberEnter("+375441234567",
                             isValid: false)
    }
    
    func testIndianNumberEnterWithoutCountryCode() {
        self.testNumberEnter("02223829419",
                             isValid: true)
    }
    
    func testFullIndianNumberEnter() {
        self.testNumberEnter(phoneNumber,
                             isValid: true)
    }
    
    private func testNumberEnter(_ number: String,
                                 isValid: Bool) {
        let otpNumberInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: "")
        self.launchApp(with: otpNumberInputInfo.getLaunchEnvironment())
        
        let input = app.textFields["phone_number_input"]
        input.tap()
        
        input.typeText(number)
        
        let inputValue = input.value as! String
        let formattedNumber = PhoneNumberUtil.shared.getFormattedPhoneNumber(inputValue)
        XCTAssertTrue(inputValue == formattedNumber)
        
        let getCodeButton = app.buttons["get_code_button"]
        XCTAssertTrue(getCodeButton.isEnabled == isValid)
    }
}
