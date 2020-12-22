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
        app.launchArguments = [TestsHelper.EnvironmentProperty.testingEnabled.rawValue]
        
        app.launch()
    }

    func testLocalizedText(with localizationKey: String,
                           isShown: Bool = true) {
        let text = app.staticTexts[localizationKey]
        
        XCTAssertTrue(text.isHittable == isShown,
                      "\(localizationKey) doesn't corespond to isShown = \(isShown)")
        
        if isShown {
            self.testLocalizedText(for: text, with: localizationKey)
        }
    }

    func testFloatingTextField(with localizationKey: String,
                               equals text: String) {
        testLocalizedText(with: localizationKey)
        
        let textField = app.textFields["\(localizationKey)_input"]
        
        XCTAssertTrue(textField.isHittable,
                      "'\(localizationKey)_input' is hidden")
        
        XCTAssertTrue(textField.value as! String == text,
                      "Text field value '\(textField.value as! String)' != '\(text)'")
    }
    
    func testFloatingSecureField(with localizationKey: String,
                                 equals text: String) {
        testLocalizedText(with: localizationKey,
                          isShown: text.isEmpty)
        
        let secureField = app.secureTextFields["\(localizationKey)_input"]
        
        XCTAssertTrue(secureField.isHittable,
                      "'\(localizationKey)_input' is hidden")
        
        let secureText = String(repeating: "•", count: text.count)
        XCTAssertTrue(secureField.value as! String == secureText,
                      "Secure field value '\(secureField.value as! String)' != '\(secureText)'")
    }
    
    func testButton(with localizationKey: String, isEnabled: Bool) {
        let button = app.buttons["\(localizationKey)_button"]
        
        XCTAssertTrue(button.isHittable,
                      "'\(localizationKey)_button' is hidden")
        XCTAssertTrue(button.isEnabled == isEnabled,
                      "'\(localizationKey)_button' doesn't corespond to isEnabled = \(isEnabled)")
        
        self.testLocalizedText(for: button, with: localizationKey)
    }
    
    func testAlert(withTitleKey titleKey: String,
                   withMessageKey messageKey: String,
                   withTimeout timeout: TimeInterval = 5) {
        let alert = app.alerts.firstMatch
        
        XCTAssertTrue(alert.waitForExistence(timeout: timeout),
                      "Alert wasn't shown in \(timeout) seconds")
        
        testLocalizedText(for: alert, with: titleKey)
        
        let localizedMessage = self.getLocalizedString(for: messageKey)
        XCTAssertTrue(alert.staticTexts[localizedMessage].exists,
                      "'\(messageKey)' doesn't have a localization for '\(self.currentLanguage?.languageCode ?? "unknown")' locale")
        XCTAssertFalse(alert.staticTexts[messageKey].exists,
                       "The alert message doesn't equal \(localizedMessage)")
    }
    
    func testNavigationBar(withTitleKey titleKey: String,
                           hasBackButton: Bool) {
        let backButton = app.buttons["back"]
        XCTAssertTrue(backButton.isHittable == hasBackButton)
        
        if hasBackButton {
            XCTAssertTrue(backButton.isEnabled)
        }
    
        let localizatedNavigationBarTitle = self.getLocalizedString(for: titleKey)
        let navigationBarTitle = app.staticTexts[localizatedNavigationBarTitle]
        
        XCTAssertTrue(navigationBarTitle.isHittable)
        XCTAssertTrue(navigationBarTitle.label == localizatedNavigationBarTitle)
        XCTAssertFalse(navigationBarTitle.label == titleKey)
    }
    
    func testActivityView(activityViewShown: Bool,
                          withHiddenElements hiddenElements: [XCUIElement],
                          withShownElements shownElements: [XCUIElement],
                          withTimeout timeout: TimeInterval = 5) {
        let activityView = app.otherElements["ActivityView"]
        
        XCTAssertTrue(activityView.exists == activityViewShown)
        
        if (!activityViewShown) {
            for element in hiddenElements {
                XCTAssertFalse(element.exists)
            }
            
            for element in shownElements {
                XCTAssertTrue(element.exists)
            }
            
            return
        }
        
        waitForElementToDisappear(activityView, timeout: timeout)
        
        for element in hiddenElements {
            waitForElementToDisappear(element, timeout: timeout)
        }
        
        for element in shownElements {
            XCTAssertTrue(element.waitForExistence(timeout: timeout))
        }
    }
    
    private func testLocalizedText(for element: XCUIElement,
                                   with localizationKey: String) {
        let localizedText = self.getLocalizedString(for: localizationKey)
        
        XCTAssertFalse(element.label == localizationKey,
                       "'\(localizationKey)' doesn't have a localization for '\(self.currentLanguage?.languageCode ?? "unknown")' locale")
        XCTAssertTrue(element.label == localizedText,
                      "The element text doesn't equal \(localizedText)")
    }
    
    // MARK: Actions
    func testBackButton(withShownElements shownElements: [XCUIElement]) {
        let backButton = app.buttons["back"]
        backButton.tap()
        
        let logInButton = app.buttons["log_in_button"]
        self.testActivityView(activityViewShown: false,
                              withHiddenElements: [backButton],
                              withShownElements: shownElements)
    }
}
