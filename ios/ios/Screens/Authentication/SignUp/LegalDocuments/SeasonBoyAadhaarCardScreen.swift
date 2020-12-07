//
//  SeasonBoyAadharCardDataScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 7.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SeasonBoyAadhaarCardScreen: View {
    let scope: SignUpScope.LegalDocuments.LegalDocumentsAadhaar
    
    @ObservedObject var canGoNext: SwiftDatasource<KotlinBoolean>
    
    @ObservedObject var aadhaarData: SwiftDatasource<DataAadhaarData>
    
    var body: some View {
        VStack(alignment: .leading) {
            self.aadharCardDataFields
            
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               buttonTextKey: "upload_aadhaar_card",
                               skipButtonAction: skip,
                               action: uploadAadharCard))
        .keyboardResponder()
        .navigationBarTitle(LocalizedStringKey("personal_data"), displayMode: .inline)
    }
    
    var aadharCardDataFields: some View {
        VStack(spacing: 12) {
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "aadhaar_card",
                                         text: aadhaarData.value?.cardNumber,
                                         onTextChange: { newValue in scope.changeCard(card: newValue) },
                                         keyboardType: .numberPad)
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "share_code",
                                         text: aadhaarData.value?.shareCode,
                                         onTextChange: { newValue in scope.changeShareCode(shareCode: newValue)},
                                         keyboardType: .numberPad)
        }
    }
    
    init(scope: SignUpScope.LegalDocumentsAadhaar) {
        self.scope = scope
        
        self.canGoNext = SwiftDatasource(dataSource: scope.canGoNext)
        self.aadhaarData = SwiftDatasource(dataSource: scope.aadhaarData)
    }
    
    private func uploadAadharCard() {
        _ = scope.upload(base64: "")
    }
    
    private func skip() {
        scope.skip()
    }
}
