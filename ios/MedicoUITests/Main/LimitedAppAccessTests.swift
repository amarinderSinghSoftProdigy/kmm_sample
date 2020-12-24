//
//  LimitedAppAccessTests.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 18.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest
import core

class LimitedAppAccessTests: UserInfoNavigationBarScreenTests {
    
    let firstName = "Name"
    let lastName = "Surname"
    let userType = DataUserType.stockist
    
    override func launchApp(with environment: [String : String]? = nil) {
        self.launchApp(forUserType: userType)
    }
    
    private func launchApp(forUserType userType: DataUserType,
                           isDocumentUploaded: Bool = true) {
        let limitedAppAccessScopeInfo =
            LimitedAppAccessScopeInfo(firstName: firstName,
                                      lastName: lastName,
                                      userType: userType,
                                      isDocumentUploaded: isDocumentUploaded)
        
        super.launchApp(with: limitedAppAccessScopeInfo.getLaunchEnvironment())
    }

    func testInitialStateWithUploadedDocument() {
        launchApp(forUserType: userType)
        
        self.testNavigationBarInitialState(hasOnlyMenuButton: true)
        
        let welcomeMessageText = app.staticTexts["welcome"]
        
        let localizedWelcomeMessage = self.getLocalizedString(for: "welcome %@")
        let userWelcomeMessage = localizedWelcomeMessage
            .replacingOccurrences(of: "%@", with: "\(firstName) \(lastName)")
        
        XCTAssertTrue(welcomeMessageText.isHittable)
        XCTAssertTrue(welcomeMessageText.label == userWelcomeMessage)
        XCTAssertFalse(welcomeMessageText.label == localizedWelcomeMessage)
        
        let image = app.images["Welcome"]
        XCTAssertTrue(image.isHittable)
        
        self.testLocalizedText(withLocalizationKey: "thank_you_for_registration")
        self.testLocalizedText(withLocalizationKey: "documents_under_review")
    }
    
    func testSlidingPanelUserInterfaceForStockist() {
        self.testSlidingPanelUserInterface(forUserType: .stockist)
    }
    
    func testSlidingPanelUserInterfaceForRetailer() {
        self.testSlidingPanelUserInterface(forUserType: .retailer)
    }
    
    func testSlidingPanelUserInterfaceForSeasonBoy() {
        self.testSlidingPanelUserInterface(forUserType: .seasonBoy)
    }
    
    func testSlidingPanelUserInterfaceForHospital() {
        self.testSlidingPanelUserInterface(forUserType: .hospital)
    }
    
    override func testSlidingPanelUserInterface(forUserType userType: DataUserType) {
        self.launchApp(forUserType: userType)
        
        super.testSlidingPanelUserInterface(forUserType: userType)
    }
}
