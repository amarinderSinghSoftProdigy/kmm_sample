//
//  SignUpAadhaarCardDetailsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 29.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpAadhaarCardDetailsScreen: View {
    let scope: SignUpScope.Details.DetailsAadhaar
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        VStack {
            AadhaardCardDataFields(aadhaarData: scope.aadhaarData,
                                   changeCard: scope.changeCard,
                                   changeShareCode: scope.changeShareCode)
            Spacer()
        }
        .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                               action: { scope.addAadhaar() }))
        .keyboardResponder()
    }
    
    init(scope: SignUpScope.Details.DetailsAadhaar) {
        self.scope = scope
        
        self.canGoNext = SwiftDataSource(dataSource: scope.isVerified)
    }
}

struct AadhaardCardDataFields: View  {
    let changeCard: (String) -> ()
    let changeShareCode: (String) -> ()
    
    @ObservedObject var aadhaarData: SwiftDataSource<DataAadhaarData>
    
    var body: some View {
        VStack(spacing: 12) {
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "aadhaar_card",
                                         text: aadhaarData.value?.cardNumber,
                                         onTextChange: { newValue in self.changeCard(newValue) },
                                         keyboardType: .numberPad)
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "share_code",
                                         text: aadhaarData.value?.shareCode,
                                         onTextChange: { newValue in self.changeShareCode(newValue)},
                                         keyboardType: .numberPad)
        }
    }
    
    init(aadhaarData: DataSource<DataAadhaarData>,
         changeCard: @escaping (String) -> (),
         changeShareCode: @escaping (String) -> ()) {
        self.aadhaarData = SwiftDataSource(dataSource: aadhaarData)
        
        self.changeCard = changeCard
        self.changeShareCode = changeShareCode
    }
}
