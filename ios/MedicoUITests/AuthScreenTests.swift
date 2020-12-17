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
    
    private let phoneNumberOrEmail = "+1234567890"
    private let password = ""
    
    override func launchApp(with environment: [String: String]? = nil) {
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: phoneNumberOrEmail,
                                                    type: .phone,
                                                    password: password)
        
        super.launchApp(with: logInScope.getLaunchEnvironment())
    }
    
    func testAuthScreenInitialState() {
        self.testLocalizedText(with: "log_in")
        
        self.testFloatingTextField(with: "phone_number_or_email", equals: phoneNumberOrEmail)
        
        self.testFloatingTextField(with: "password", equals: password)
        
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
}
