//
//  NavigationBarScreenTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 18.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
import core

/// Base class for the tests for the screens that have `UserInfoNavigationBar`
///
/// - Warning: ALL tests should be executed from the CHILD classes
class UserInfoNavigationBarScreenTests: BaseTests {
    private var userFirstName: String!
    private var userLastName: String!
    private var userType: DataUserType!
    
    let errorMessage = "Please, check that the test was called from the child class"
    
    override func launchApp(with environment: [String : String]? = nil) {
        if let properties = environment {
            userFirstName = properties[TestsHelper.EnvironmentProperty.firstName.rawValue] ?? ""
            userLastName = properties[TestsHelper.EnvironmentProperty.lastName.rawValue] ?? ""
            userType = DataUserType.getValue(from:  properties[TestsHelper.EnvironmentProperty.userType.rawValue] ?? "")
        }
        
        super.launchApp(with: environment)
    }
    
    func testNavigationBarInitialState(hasOnlyMenuButton: Bool) {
        let menuButton = app.buttons["Menu"]
        
        XCTAssertTrue(menuButton.isHittable,
                      getErrorMessage("Menu button is hidden"))
        XCTAssertTrue(menuButton.isEnabled,
                      getErrorMessage("Menu button is disabled"))
        
        if hasOnlyMenuButton { return }
    }
    
    // MARK: Open/Close
    func testSlidingPanelOpeningWithMenuButtonClick() {
        let menuButton = app.buttons["Menu"]
        menuButton.tap()
        
        let slidingPanel = app.otherElements["sliding_panel"]
        
        XCTAssertTrue(slidingPanel.waitForExistence(timeout: 2),
                      getErrorMessage("Sliding panel wasn't opened"))
    }
    
    func testSlidingPanelOpeningWithRightSwipe() {
        _ = self.openSlidingPanelWithRightSwipe()
    }
    
    func testSlidingPanelClosingWithLeftSwipe() {
        let timeout: TimeInterval = 2
        
        let slidingPanel = self.openSlidingPanelWithRightSwipe(waitForTimeout: timeout)
        
        app.swipeLeft()
        
        waitForElementToDisappear(slidingPanel, timeout: timeout)
    }
    
    func testSlidingPanelClosingWithBlurViewTap() {
        let timeout: TimeInterval = 2
        
        let slidingPanel = self.openSlidingPanelWithRightSwipe(waitForTimeout: timeout)
        
        let blurView = app.otherElements["blur_view"]
        blurView.tap()
        
        waitForElementToDisappear(slidingPanel, timeout: timeout)
    }
    
    // MARK: Sliding Panel UI
    func testSlidingPanelUserInterface(forUserType userType: DataUserType) {
        _ = self.openSlidingPanelWithRightSwipe()
        
        let userPhoto = app.images["user_photo"]
        XCTAssertTrue(userPhoto.isHittable,
                      getErrorMessage("The 'user_photo' image isn't presented"))
        
        let userNameText = app.staticTexts["user_name"]
        let userName = "\(userFirstName!) \(userLastName!)"
        XCTAssertTrue(userNameText.isHittable,
                      getErrorMessage("The 'user_name' text isn't presented"))
        XCTAssertTrue(userNameText.label == userName,
                      getErrorMessage("The 'user_name' text value doesn't match '\(userName)'"))
        
        let userTypeLocalizationKey = userType.name.lowercased()
        self.testLocalizedText(with: userTypeLocalizationKey)
        
        self.testButton(with: "log_out", isEnabled: true)
    }
    
    // MARK: Actions
    func testSlidingPanelLogOut() {
        let timeout: TimeInterval = 2
        
        let slidingPanel = self.openSlidingPanelWithRightSwipe(waitForTimeout: timeout)
        
        let logOutButton = app.buttons["log_out_button"]
        logOutButton.tap()
        
        XCTAssertFalse(slidingPanel.exists,
                       getErrorMessage("Sliding panel wasn't closed"))
        
        let logInButton = app.buttons["log_in_button"]
        XCTAssertTrue(logInButton.isHittable,
                      getErrorMessage("The app hasn't returned to the log in screen"))
    }
    
    // MARK: Helping functions
    private func openSlidingPanelWithRightSwipe(waitForTimeout timeout: TimeInterval = 2) -> XCUIElement {
        app.swipeRight()
        
        let slidingPanel = app.otherElements["sliding_panel"]
        
        XCTAssertTrue(slidingPanel.waitForExistence(timeout: timeout),
                      getErrorMessage("Sliding panel wasn't opened"))
        
        return slidingPanel
    }
    
    private func getErrorMessage(_ message: String) -> String {
        "\(message)\n\(errorMessage)"
    }
}
