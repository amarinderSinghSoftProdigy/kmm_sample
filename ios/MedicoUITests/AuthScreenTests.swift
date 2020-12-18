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
    
    // MARK: Initial State
    func testAuthScreenInitialStateWithEmptyValues() {
        testAuthScreenInitialState(withPhoneNumberOrEmail: "",
                                   withType: .phone,
                                   withPassword: "")
    }
    
    func testAuthScreenInitialStateWithEmptyPassword() {
        testAuthScreenInitialState(withPhoneNumberOrEmail: phone,
                                   withType: .phone,
                                   withPassword: "")
    }
    
    func testAuthScreenInitialStateWithEmptyPhoneOrEmail() {
        testAuthScreenInitialState(withPhoneNumberOrEmail: "",
                                   withType: .phone,
                                   withPassword: password)
    }
    
    func testAuthScreenInitialStateWithPhoneAndPassword() {
        testAuthScreenInitialState(withPhoneNumberOrEmail: phone,
                                   withType: .phone,
                                   withPassword: password)
    }
    
    func testAuthScreenInitialStateWithEmailAndPassword() {
        testAuthScreenInitialState(withPhoneNumberOrEmail: email,
                                   withType: .email,
                                   withPassword: password)
    }
    
    private func testAuthScreenInitialState(withPhoneNumberOrEmail phoneNumberOrEmail: String,
                                            withType type: DataAuthCredentials.Type_,
                                            withPassword password: String) {
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: phoneNumberOrEmail,
                                                    type: type,
                                                    password: password)
        self.launchApp(with: logInScope.getLaunchEnvironment())
        
        self.testLocalizedText(with: "log_in")
        
        self.testFloatingTextField(with: "phone_number_or_email",
                                   equals: logInScope.phoneNumberOrEmail)
        
        self.testFloatingSecureField(with: "password",
                                     equals: logInScope.password)
        
        self.testLocalizedText(with: "forgot_password")
        
        self.testButton(with: "log_in",
                        isEnabled: !logInScope.phoneNumberOrEmail.isEmpty && !logInScope.password.isEmpty)
        
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
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: "",
                                                    type: .phone,
                                                    password: "")
        self.launchApp(with: logInScope.getLaunchEnvironment())
        
        let forgotPasswordLink = app.staticTexts["forgot_password"]
        forgotPasswordLink.tap()
        
        let activityView = app.otherElements["ActivityView"]
        let phoneNumberInput = app.textFields["phone_number_input"]
        
        XCTAssertFalse(forgotPasswordLink.exists)
        XCTAssertFalse(activityView.exists)
        XCTAssertTrue(phoneNumberInput.exists)
    }
    
    func testLogInNavigation() {
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: phone,
                                                    type: .phone,
                                                    password: password)
        self.launchApp(with: logInScope.getLaunchEnvironment())
        
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
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: "",
                                                    type: .phone,
                                                    password: "")
        self.launchApp(with: logInScope.getLaunchEnvironment())
        
        let signUpLink = app.staticTexts["sign_up_to_medico"]
        signUpLink.tap()
        
        let activityView = app.otherElements["ActivityView"]
        let whoAreYouText = app.staticTexts["who_are_you"]
        
        XCTAssertFalse(signUpLink.exists)
        XCTAssertFalse(activityView.exists)
        
        XCTAssertTrue(whoAreYouText.exists)
    }
}
