//
//  AuthNewPasswordScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 23.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

class AuthNewPasswordScreenTests: BaseTests {
    
    let phoneNumber = "+9102222870456"

    override func launchApp(with environment: [String : String]? = nil) {
        let phoneInputInfo = TestsHelper.OtpScopeNumberInputInfo(phone: phoneNumber)
        
        super.launchApp(with: phoneInputInfo.getLaunchEnvironment())
        
        let getCodeButton = app.buttons["get_code_button"]
        getCodeButton.tap()
        
        let activityView = app.otherElements["ActivityView"]
        waitForElementToDisappear(activityView, timeout: 5)
        
        let verificationCodeInput = app.textFields["verification_code_input"]
        
        verificationCodeInput.tap()
        verificationCodeInput.typeText("1")
        
        let submitButton = app.buttons["submit_button"]
        submitButton.tap()
        
        waitForElementToDisappear(activityView, timeout: 5)
    }

    // MARK: Initial State
    func testInitialState() {
        self.testNavigationBar(withTitleKey: "new_password", hasBackButton: true)
        
        self.testFloatingSecureField(withLocalizationKey: "new_password", equals: "")
        self.testFloatingSecureField(withLocalizationKey: "new_password_repeat", equals: "")
        
        self.testLocalizedText(withLocalizationKey: "password_doesnt_match",
                               isShown: false)
        
        self.testButton(withLocalizationKey: "confirm", isEnabled: false)
    }
    
    // MARK: Passwords input
    func testIdenticalPasswordInput() {
        let password = "qweASD123"
        
        enterPasswords(password: password, repeatPassword: password)
        
        self.testLocalizedText(withLocalizationKey: "password_doesnt_match",
                               isShown: false)
        
        let confirmButton = app.buttons["confirm_button"]
        XCTAssertTrue(confirmButton.isEnabled)
    }
    
    func testDifferentPasswordInput() {
        enterPasswords(password: "qweASD123", repeatPassword: "password")
        
        self.testLocalizedText(withLocalizationKey: "password_doesnt_match")
        
        let confirmButton = app.buttons["confirm_button"]
        XCTAssertFalse(confirmButton.isEnabled)
    }
    
    // MARK: Actions
    func testBackButton() {
        let confirmButton = app.buttons["confirm_button"]
        let getCodeButton = app.buttons["get_code_button"]
        
        self.testBackButton(withHiddenElements: [confirmButton],
                            withShownElements: [getCodeButton])
    }
    
    func testSubmitButton() {
        let password = "qweASD123"
        enterPasswords(password: password, repeatPassword: password)
        
        let confirmButton = app.buttons["confirm_button"]
        confirmButton.tap()
        
        self.testAlert(withTitleKey: "success",
                       withMessageKey: "password_change_success",
                       withClickButtonKey: "okay")
        
        let logInButton = app.buttons["log_in_button"]
        XCTAssertTrue(logInButton.isHittable)
    }
    
    private func enterPasswords(password: String, repeatPassword: String) {
        let newPasswordInput = app.secureTextFields["new_password_input"]
        newPasswordInput.tap()
        newPasswordInput.typeText(password)
        
        let newPasswordRepeatInput = app.secureTextFields["new_password_repeat_input"]
        newPasswordRepeatInput.tap()
        newPasswordRepeatInput.typeText(repeatPassword)
    }
}
