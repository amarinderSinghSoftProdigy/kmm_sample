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

extension UITableView {
    func hasRowAtIndexPath(_ indexPath: IndexPath) -> Bool {
        return indexPath.section < self.numberOfSections &&
            indexPath.row < self.numberOfRows(inSection: indexPath.section)
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
        case is PasswordScope.EnterNewPasswordChangedSuccessfully:
            return "success"
        default:
            return nil
        }
    }
    
    var body: String? {
        switch self {
        case is PasswordScope.EnterNewPasswordChangedSuccessfully:
            return "password_change_success"
        default:
            return nil
        }
    }
}

extension String {
    func capitalizeFirstLetter() -> String {
        return prefix(1).capitalized + dropFirst()
    }
    
    var localized: String {
        return NSLocalizedString(self, tableName: nil, bundle: Bundle.main, value: "", comment: "")
    }
}

extension UIImage {
    func colorized(color : UIColor?) -> UIImage {
        guard let color = color else { return self }
        
        let rect = CGRect(x: 0, y: 0, width: self.size.width, height: self.size.height)

        UIGraphicsBeginImageContextWithOptions(rect.size, false, 0.0)
        
        if let context = UIGraphicsGetCurrentContext(),
           let cgImage = self.cgImage {
            context.setBlendMode(.multiply)
            context.translateBy(x: 0, y: self.size.height)
            context.scaleBy(x: 1.0, y: -1.0)
            context.draw(cgImage, in: rect)
            context.clip(to: rect, mask: cgImage)
            context.setFillColor(color.cgColor)
            context.fill(rect)
        }

        let colorizedImage = UIGraphicsGetImageFromCurrentImageContext()

        UIGraphicsEndImageContext()
        return colorizedImage ?? self
      }
}

extension StringProtocol {
    subscript(offset: Int) -> Character { self[index(startIndex, offsetBy: offset)] }
}

extension DataStockInfo {
    var statusColor: AppColor {
        switch self.status {
        case .inStock:
            return .green
        case .lowStock:
            return .orange
        case .outOfStock:
            return .red
        default:
            return .darkBlue
        }
    }
}
