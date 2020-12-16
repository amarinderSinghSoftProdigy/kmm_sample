//
//  MedicoUITests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 16.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
@testable import Medico
import core

class MedicoUITests: XCTestCase {
    
    private let app = XCUIApplication()

    override func setUp() {
        continueAfterFailure = false
        
        app.launch()
        
//        let credentials = DataAuthCredentials(phoneNumberOrEmail: "",
//                                              type: .none,
//                                              password: "")
//
//        let errors: DataSource<DataErrorCode> = DataSource(initialValue: nil)
//
//        UiLink().overrideCurrentScope(
//            uiNavigator: navigator,
//            scope: LogInScope(credentials: DataSource(initialValue: credentials),
//                              errors: errors)
//        )
    }
    
    func testAuthScreenInitialState() {
        
    }
}
