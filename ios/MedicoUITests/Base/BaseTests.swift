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

    func testLocalizedText(withLocalizationKey localizationKey: String,
                           withParameter parameter: String? = nil,
                           withParameterSymbol parameterSymbol: String? = nil,
                           isShown: Bool = true,
                           withElementKey elementKey: String? = nil) {
        let text = app.staticTexts[elementKey ?? localizationKey]
        
        XCTAssertTrue(text.isHittable == isShown,
                      "\(localizationKey) doesn't corespond to isShown = \(isShown)")
        
        if isShown {
            self.testLocalizedText(for: text,
                                   withLocalizationKey: localizationKey,
                                   withParameter: parameter,
                                   withParameterSymbol: parameterSymbol)
        }
    }

    func testFloatingTextField(withLocalizationKey localizationKey: String,
                               equals text: String) {
        testLocalizedText(withLocalizationKey: localizationKey)
        
        let textField = app.textFields["\(localizationKey)_input"]
        
        XCTAssertTrue(textField.isHittable,
                      "'\(localizationKey)_input' is hidden")
        
        XCTAssertTrue(textField.value as! String == text,
                      "Text field value '\(textField.value as! String)' != '\(text)'")
    }
    
    func testFloatingSecureField(withLocalizationKey localizationKey: String,
                                 equals text: String) {
        testLocalizedText(withLocalizationKey: localizationKey,
                          isShown: text.isEmpty)
        
        let secureField = app.secureTextFields["\(localizationKey)_input"]
        
        XCTAssertTrue(secureField.isHittable,
                      "'\(localizationKey)_input' is hidden")
        
        let secureText = String(repeating: "•", count: text.count)
        XCTAssertTrue(secureField.value as! String == secureText,
                      "Secure field value '\(secureField.value as! String)' != '\(secureText)'")
    }
    
    func testButton(withLocalizationKey localizationKey: String,
                    isEnabled: Bool) {
        let button = app.buttons["\(localizationKey)_button"]
        
        XCTAssertTrue(button.isHittable,
                      "'\(localizationKey)_button' is hidden")
        XCTAssertTrue(button.isEnabled == isEnabled,
                      "'\(localizationKey)_button' doesn't corespond to isEnabled = \(isEnabled)")
        
        self.testLocalizedText(for: button, withLocalizationKey: localizationKey)
    }
    
    func testAlert(withTitleKey titleKey: String,
                   withMessageKey messageKey: String,
                   withTimeout timeout: TimeInterval = 5,
                   withClickButtonKey clickButtonKey: String? = nil) {
        let alert = app.alerts.firstMatch
        
        XCTAssertTrue(alert.waitForExistence(timeout: timeout),
                      "Alert wasn't shown in \(timeout) seconds")
        
        testLocalizedText(for: alert, withLocalizationKey: titleKey)
        
        let localizedMessage = self.getLocalizedString(for: messageKey)
        XCTAssertTrue(alert.staticTexts[localizedMessage].exists,
                      "'\(messageKey)' doesn't have a localization for '\(self.currentLanguage?.languageCode ?? "unknown")' locale")
        XCTAssertFalse(alert.staticTexts[messageKey].exists,
                       "The alert message doesn't equal \(localizedMessage)")
        
        guard let clickButtonKey = clickButtonKey else { return }
        
        let clickButton = alert.buttons[self.getLocalizedString(for: clickButtonKey)]
        clickButton.tap()
    }
    
    func testNavigationBar(withTitleKey titleKey: String,
                           hasBackButton: Bool) {
        let backButton = app.buttons["back"]
        XCTAssertTrue(backButton.isHittable == hasBackButton)
        
        if hasBackButton {
            XCTAssertTrue(backButton.isEnabled)
        }
    
        let localizatedNavigationBarTitle = self.getLocalizedString(for: titleKey)
        let navigationBarTitle = app.navigationBars[localizatedNavigationBarTitle]
            .staticTexts[localizatedNavigationBarTitle]
        
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
        
        if !activityViewShown {
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
                                   withLocalizationKey localizationKey: String,
                                   withParameter parameter: String? = nil,
                                   withParameterSymbol parameterSymbol: String? = nil) {
        var localizedText = self.getLocalizedString(for: localizationKey)
        
        if let parameter = parameter {
            localizedText = localizedText.replacingOccurrences(of: parameterSymbol ?? "%@",
                                                               with: parameter)
        }
        
        XCTAssertFalse(element.label == localizationKey,
                       "'\(localizationKey)' doesn't have a localization for '\(self.currentLanguage?.languageCode ?? "unknown")' locale")
        XCTAssertTrue(element.label == localizedText,
                      "The element text doesn't equal '\(localizedText)'")
    }
    
    // MARK: Actions
    func testBackButton(withHiddenElements hiddenElements: [XCUIElement],
                        withShownElements shownElements: [XCUIElement]) {
        let backButton = app.buttons["back"]
        backButton.tap()
        
        self.testActivityView(activityViewShown: false,
                              withHiddenElements: hiddenElements,
                              withShownElements: shownElements)
    }
}
