//
//  Extensions.swift
//  MedicoUITests
//
//  Created by Dasha Gurinovich on 17.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import XCTest

extension XCTestCase {
    var currentLanguage: (languageCode: String, localeCode: String)? {
        guard let preferredLanguage = Locale.preferredLanguages.first else { return nil }
        
        let currentLocale = Locale(identifier: preferredLanguage)
        
        guard let languageCode = currentLocale.languageCode else { return nil }
        
        var localeCode = languageCode
        if let scriptCode = currentLocale.scriptCode {
            localeCode = "\(languageCode)-\(scriptCode)"
        } else if let regionCode = currentLocale.regionCode {
            localeCode = "\(languageCode)-\(regionCode)"
        }
        
        return (languageCode, localeCode)
    }
    
    func getLocalizedString(for key: String) -> String {
        let testBundle = Bundle(for: type(of: self))
        
        guard let currentLanguage = currentLanguage,
              let bundlePath = testBundle.path(forResource: currentLanguage.localeCode, ofType: "lproj")
                ?? testBundle.path(forResource: currentLanguage.languageCode, ofType: "lproj"),
              let localizationBundle = Bundle(path: bundlePath)
        else { return key }
        
        let result = NSLocalizedString(key, bundle: localizationBundle, comment: "")
        
        return result
    }
    
    func waitForElementToDisappear(_ element: XCUIElement,
                                   timeout: TimeInterval) {
        let exists = NSPredicate(format: "exists == FALSE")

        expectation(for: exists, evaluatedWith: element, handler: nil)
        waitForExpectations(timeout: timeout, handler: nil)
    }
}
