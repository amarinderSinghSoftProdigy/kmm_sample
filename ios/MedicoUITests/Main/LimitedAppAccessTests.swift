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
    
    let aadhaarNumber = "887489598799"
    
    override func launchApp(with environment: [String : String]? = nil) {
        self.launchApp(forUserType: userType)
    }
    
    private func launchApp(forUserType userType: DataUserType,
                           isDocumentUploaded: Bool = true) {
        let aadhaarCardNumber: String? = userType == .seasonBoy ? aadhaarNumber : nil
        
        let limitedAppAccessScopeInfo =
            LimitedAppAccessScopeInfo(firstName: firstName,
                                      lastName: lastName,
                                      userType: userType,
                                      aadhaarNumber: aadhaarCardNumber,
                                      isDocumentUploaded: isDocumentUploaded)
        
        super.launchApp(with: limitedAppAccessScopeInfo.getLaunchEnvironment())
    }

    func testInitialStateWithUploadedDocument() {
        launchApp(forUserType: userType)
        
        self.testNavigationBarInitialState(hasOnlyMenuButton: true)
        
        self.testLocalizedText(withLocalizationKey: "welcome %@",
                               withParameter: "\(firstName) \(lastName)",
                               withElementKey: "welcome")
        
        let image = app.images["Welcome"]
        XCTAssertTrue(image.isHittable)
        
        self.testLocalizedText(withLocalizationKey: "thank_you_for_registration")
        self.testLocalizedText(withLocalizationKey: "documents_under_review")
    }
    
    func testInitialStateWithNonUploadedDocumentForNonSeasonBoy() {
        launchApp(forUserType: userType, isDocumentUploaded: false)
        
        self.testNavigationBarInitialState(hasOnlyMenuButton: true)
        
        self.testLocalizedText(withLocalizationKey: "welcome %@",
                               withParameter: "\(firstName) \(lastName)",
                               withElementKey: "welcome")
        
        let image = app.images["UploadDocuments"]
        XCTAssertTrue(image.isHittable)
        
        self.testLocalizedText(withLocalizationKey: "drug_license_request")
        
        self.testButton(withLocalizationKey: "upload_new_document", isEnabled: true)
    }
    
    func testInitialStateWithNonUploadedDocumentForSeasonBoy() {
        launchApp(forUserType: .seasonBoy, isDocumentUploaded: false)
        
        self.testNavigationBarInitialState(hasOnlyMenuButton: true)
        
        self.testLocalizedText(withLocalizationKey: "welcome %@",
                               withParameter: "\(firstName) \(lastName)",
                               withElementKey: "welcome")
        
        let image = app.images["UploadDocuments"]
        XCTAssertTrue(image.isHittable)
        
        self.testLocalizedText(withLocalizationKey: "aadhaar_card_request")
        
        self.testFloatingTextField(withLocalizationKey: "aadhaar_card", equals: aadhaarNumber)
        self.testFloatingTextField(withLocalizationKey: "share_code", equals: "")
        
        self.testButton(withLocalizationKey: "upload_aadhaar_card", isEnabled: false)
    }
    
    func testNonUploadedSeasonBoyDocumentShareCodeEnter() {
        launchApp(forUserType: .seasonBoy, isDocumentUploaded: false)
        
        let shareCodeInput = app.textFields["share_code_input"]
        shareCodeInput.tap()
        shareCodeInput.typeText("1111")
        
        app.tap()
        
        self.testButton(withLocalizationKey: "upload_aadhaar_card", isEnabled: true)
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
