//
//  MedicoUITests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 16.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
import core

class AuthScreenTests: BaseTests {
    let phone = "+1234567890"
    let email = "email@example.com"
    let password = "qweASD123"
    
    override func setUp() {
        continueAfterFailure = false
    }

    private func launchApp(withPhoneOrEmail phoneOrEmail: String,
                           withType type: DataAuthCredentials.Type_,
                           withPassword password: String,
                           withErrorCode errorCode: DataErrorCode? = nil) {
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: phoneOrEmail,
                                                    type: type,
                                                    password: password,
                                                    errorCode: errorCode)
        
        super.launchApp(with: logInScope.getLaunchEnvironment())
    }
    
    // MARK: Initial State
    func testInitialStateWithEmptyValues() {
        testInitialState(withPhoneNumberOrEmail: "",
                         withType: .phone,
                         withPassword: "")
    }
    
    func testInitialStateWithEmptyPassword() {
        testInitialState(withPhoneNumberOrEmail: phone,
                         withType: .phone,
                         withPassword: "")
    }
    
    func testInitialStateWithEmptyPhoneOrEmail() {
        testInitialState(withPhoneNumberOrEmail: "",
                         withType: .phone,
                         withPassword: password)
    }
    
    func testInitialStateWithPhoneAndPassword() {
        testInitialState(withPhoneNumberOrEmail: phone,
                         withType: .phone,
                         withPassword: password)
    }
    
    func testInitialStateWithEmailAndPassword() {
        testInitialState(withPhoneNumberOrEmail: email,
                         withType: .email,
                         withPassword: password)
    }
    
    private func testInitialState(withPhoneNumberOrEmail phoneNumberOrEmail: String,
                                  withType type: DataAuthCredentials.Type_,
                                  withPassword password: String) {
        launchApp(withPhoneOrEmail: phoneNumberOrEmail,
                  withType: type,
                  withPassword: password)
        
        self.testLocalizedText(with: "log_in")
        
        self.testFloatingTextField(with: "phone_number_or_email",
                                   equals: phoneNumberOrEmail)
        
        self.testFloatingSecureField(with: "password",
                                     equals: password)
        
        self.testLocalizedText(with: "forgot_password")
        
        self.testButton(with: "log_in",
                        isEnabled: !phoneNumberOrEmail.isEmpty && !password.isEmpty)
        
        let signUpText = app.staticTexts["sign_up_to_medico"]
        
        let signUpKey = "sign_up"
        let toMedicoKey = "to_medico"
        
        let signUpString = self.getLocalizedString(for: signUpKey)
        let toMedicoString = self.getLocalizedString(for: toMedicoKey)
        
        XCTAssertTrue(signUpText.isHittable)
        XCTAssertTrue(signUpText.label == signUpString + toMedicoString)
        XCTAssertFalse(signUpText.label == signUpKey + toMedicoKey)
        
        self.testLocalizedText(with: "copyright")
    }
    
    // MARK: Actions
    func testForgetPasswordNavigation() {
        launchApp(withPhoneOrEmail: "",
                  withType: .phone,
                  withPassword: "")
        
        let forgotPasswordLink = app.staticTexts["forgot_password"]
        forgotPasswordLink.tap()
        
        let activityView = app.otherElements["ActivityView"]
        let phoneNumberInput = app.textFields["phone_number_input"]
        
        XCTAssertFalse(forgotPasswordLink.exists)
        XCTAssertFalse(activityView.exists)
        XCTAssertTrue(phoneNumberInput.exists)
    }
    
    func testLogInNavigation() {
        launchApp(withPhoneOrEmail: phone,
                  withType: .phone,
                  withPassword: password)
        
        let logInButton = app.buttons["log_in_button"]
        logInButton.tap()
        
        let activityView = app.otherElements["ActivityView"]
        let menuNavigationBarButton = app.buttons["Menu"]
        
        XCTAssertTrue(activityView.exists)
        
        let timeout: TimeInterval = 5
        
        waitForElementToDisappear(activityView, timeout: timeout)
        waitForElementToDisappear(logInButton, timeout: timeout)
        XCTAssertTrue(menuNavigationBarButton.waitForExistence(timeout: timeout))
    }
    
    func testSignUpNavigation() {
        launchApp(withPhoneOrEmail: "",
                  withType: .phone,
                  withPassword: "")
        
        let signUpLink = app.staticTexts["sign_up_to_medico"]
        signUpLink.tap()
        
        let activityView = app.otherElements["ActivityView"]
        let whoAreYouText = app.staticTexts["who_are_you"]
        
        XCTAssertFalse(signUpLink.exists)
        XCTAssertFalse(activityView.exists)
        
        XCTAssertTrue(whoAreYouText.exists)
    }
    
    // MARK: Error
    func testErrorAlert() {
        let errorCode = DataErrorCode(title: "error", body: "something_went_wrong")
        
        launchApp(withPhoneOrEmail: "",
                  withType: .phone,
                  withPassword: "",
                  withErrorCode: errorCode)
        
        self.testAlert(withTitleKey: errorCode.title,
                       withMessageKey: errorCode.body)
    }
}
