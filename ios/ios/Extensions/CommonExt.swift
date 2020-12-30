//
//  CommonExt.swift
//  ios
//
//  Created by Arnis on 20.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import core
import SwiftUI

extension UIApplication {
    var navigator: UiNavigator {
        (self.delegate as! AppDelegate).navigator
    }
}

extension String {
    func capitalizeFirstLetter() -> String {
        return prefix(1).capitalized + dropFirst()
    }
}

extension TimeInterval{
    init(milliseconds: Double) {
        self.init(milliseconds / 1000.0)
    }
    
    var timeString: String {
        let time = NSInteger(self)

        let seconds = time % 60
        let minutes = (time / 60)

        return String(format: "%1d:%0.2d", minutes, seconds)

    }
}

extension DataUserType {
    var imageName: String? {
        switch self {
        
        case DataUserType.stockist:
            return "Stockist"
            
        case DataUserType.retailer:
            return "Retailer"
            
        case DataUserType.seasonBoy:
            return "SeasonBoy"
            
        case DataUserType.hospital:
            return "Hospital"
            
        default:
            return nil
        }
    }
    
    var localizedName: String {
        return self.name.lowercased()
    }
}

extension UIImagePickerController.SourceType: Identifiable {
    public var id: UIImagePickerController.SourceType { self }
}

extension ScopeNotification {
    var title: String? {
        switch self {
        case is EnterNewPasswordScope.PasswordChangedSuccessfully:
            return "success"
        default:
            return nil
        }
    }
    
    var body: String? {
        switch self {
        case is EnterNewPasswordScope.PasswordChangedSuccessfully:
            return "password_change_success"
        default:
            return nil
        }
    }
}


extension BaseScope {
    func getAvailableDocumentTypes(from fileTypes: KotlinArray<DataFileType>) -> [String] {
        var documentTypes = [String]()
        
        let iterator = fileTypes.iterator()
        while iterator.hasNext() {
            guard let fileType = iterator.next() as? DataFileType,
                  fileType.isMandatory,
                  let uti = fileType.getUniformTypeIdentifier() else { continue }
            
            documentTypes.append(uti)
        }
        
        return documentTypes
    }
}

extension String {
    var localized: String {
        return NSLocalizedString(self, tableName: nil, bundle: Bundle.main, value: "", comment: "")
    }
}
