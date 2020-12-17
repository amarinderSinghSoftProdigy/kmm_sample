//
//  BaseTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 17.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

class BaseTests: XCTestCase {
    let app = XCUIApplication()

    override func setUp() {
        continueAfterFailure = false
        
        self.launchApp()
    }
    
    func launchApp(with environment: [String: String]? = nil) {
        var initialEnvironvent = [String: String]()
        
        if let additionalParameters = environment {
            initialEnvironvent.merge(additionalParameters, uniquingKeysWith: { _, last in last })
        }
        
        app.launchEnvironment = initialEnvironvent
        
        app.launch()
    }

    func testLocalizedText(with localizationKey: String) {
        let text = app.staticTexts[localizationKey]
        
        XCTAssertTrue(text.isHittable)
        
        self.testLocalizedText(for: text, with: localizationKey)
    }

    func testFloatingTextField(with localizationKey: String,
                               equals text: String) {
        testLocalizedText(with: localizationKey)
        
        let textField = app.textFields["\(localizationKey)_input"]
        
        XCTAssertTrue(textField.isHittable == !text.isEmpty)
        
        if textField.isHittable {
            XCTAssertTrue(textField.value as! String == text)
        }
    }
    
    func testButton(with localizationKey: String, isEnabled: Bool) {
        let button = app.buttons["\(localizationKey)_button"]
        
        XCTAssertTrue(button.isHittable)
        XCTAssertTrue(button.isEnabled == isEnabled)
        
        self.testLocalizedText(for: button, with: localizationKey)
    }
    
    private func testLocalizedText(for element: XCUIElement,
                                   with localizationKey: String) {
        let localizedText = self.getLocalizedString(for: localizationKey)
        
        XCTAssertTrue(element.label == localizedText)
        XCTAssertFalse(element.label == localizationKey)
    }
}
