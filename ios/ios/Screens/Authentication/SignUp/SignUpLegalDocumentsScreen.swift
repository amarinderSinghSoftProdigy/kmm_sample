//
//  SeasonBoyAadharCardDataScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 7.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpLegalDocumentsScreen: View {
    let scope: SignUpScope.LegalDocuments
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        self.getView()
            .screenLogger(withScreenName: "SignUpLegalDocumentsScreen",
                          withScreenClass: SignUpLegalDocumentsScreen.self)
    }
    
    init(scope: SignUpScope.LegalDocuments) {
        self.scope = scope

        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
    }
    
    private func getView() -> some View {
        let documentRequestTextKey: String
        let buttonTextKey: String
        
        switch scope {

        case is SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            documentRequestTextKey = "aadhaar_card_request"
            buttonTextKey = "upload_aadhaar_card"

        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            documentRequestTextKey = "drug_license_request"
            buttonTextKey = "upload_new_document"
            
        default:
            documentRequestTextKey = ""
            buttonTextKey = ""
        }
        
        return AnyView(
            GeometryReader { geometry in
                HStack {
                    Spacer()
                    
                    VStack(spacing: 28) {
                        Spacer()
                        
                        Image("UploadDocuments")
                        
                        LocalizedText(localizationKey: documentRequestTextKey,
                                      fontSize: 16,
                                      color: .grey1)
                        
                        Spacer()
                    }
                    .padding(.horizontal, geometry.size.width * 0.17)
                    
                    Spacer()
                }
            }
            .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                                   buttonTextKey: buttonTextKey,
                                   skipButtonAction: { scope.skip() },
                                   action: { scope.showBottomSheet() }))
        )
    }
}
