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
    let scope: LimitedAccessScope
    
    @ObservedObject var user: SwiftDataSource<DataUser>
    
    var body: some View {
        self.getView()
    }
    
    init(scope: LimitedAccessScope) {
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
            if let aadhaarDataHolder = self.scope as? AadhaarDataHolder {
                welcomeOption = WelcomeOption.Upload.AadhaarCard(aadhaarDataHolder: aadhaarDataHolder,
                                                                 onUploadClick: { scope.showBottomSheet() })
            }
            else {
                welcomeOption = WelcomeOption.Upload.DrugLicense(onUploadClick: { scope.showBottomSheet() })
            }
        }
        
        return AnyView(
            WelcomeScreen(welcomeOption: welcomeOption,
                          userName: user.fullName())
        )
    }
}
