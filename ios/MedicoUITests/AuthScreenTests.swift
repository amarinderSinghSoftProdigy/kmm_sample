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
    let phone = "1234567890"
    let email = "email@example.com"
    let password = "qweASD123"
    
    override func setUp() {
        continueAfterFailure = false
    }
    
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
}
