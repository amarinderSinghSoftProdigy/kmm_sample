//
//  LimitedAppAccessScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 11.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct LimitedAppAccessScreen: View {
    let scope: MainScope.LimitedAccess
    
    @ObservedObject var user: SwiftDataSource<DataUser>
    
    var body: some View {
        self.getView()
    }
    
    init(scope: MainScope.LimitedAccess) {
        self.scope = scope
        
        self.user = SwiftDataSource(dataSource: scope.user)
    }
    
    private func getView() -> some View {
        guard let user = self.user.value else { return AnyView(EmptyView()) }
        
        let welcomeOption: WelcomeOption
        
        if user.isDocumentUploaded {
            welcomeOption = WelcomeOption.Thanks()
        }
        else {
            let documentTypes = scope.getAvailableDocumentTypes(from: scope.supportedFileTypes)
            
            if let scope = self.scope as? MainScope.LimitedAccess.LimitedAccessSeasonBoy {
                welcomeOption = WelcomeOption.Upload.AadhaarCard(documentTypes: documentTypes,
                                                                 isVerified: scope.isVerified,
                                                                 aadhaarData: scope.aadhaarData,
                                                                 changeCard: scope.changeCard,
                                                                 changeShareCode: scope.changeShareCode,
                                                                 uploadData: uploadData)
            }
            
            else {
                welcomeOption = WelcomeOption.Upload.DrugLicense(documentTypes: documentTypes,
                                                                 uploadData: uploadData)
            }
        }
        
        return AnyView(
            WelcomeScreen(welcomeOption: welcomeOption,
                          userName: user.fullName())
        )
    }
    
    private func uploadData(_ base64: String, withFileType fileType: DataFileType) {
        if let scope = self.scope as? MainScope.LimitedAccess.LimitedAccessSeasonBoy {
            scope.uploadAadhaar(base64: base64)
            
            return
        }
        
        if let scope = self.scope as? MainScope.LimitedAccess.LimitedAccessNonSeasonBoy {
            scope.uploadDrugLicense(base64: base64, fileType: fileType)
        }
    }
}
