//
//  MedicoUITests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 16.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
import core
//@testable import Medico

class AuthScreenTests: XCTestCase {
    
    private let app = XCUIApplication()

    override func setUp() {
        continueAfterFailure = false
    }
    
    func testAuthScreenInitialState() {
        let logInScope = TestsHelper.LogInScopeInfo(phoneNumberOrEmail: "+1234567890",
                                                    type: .phone,
                                                    password: "qweASD123")
        app.launchEnvironment = logInScope.getLaunchEnvironment()
        
        app.launch()
        
        
    }
}
