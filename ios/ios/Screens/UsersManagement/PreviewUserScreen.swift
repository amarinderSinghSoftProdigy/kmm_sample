//
//  PreviewUserScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 10.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct PreviewUserScreen: View {
    let scope: PreviewUserScope
    
    @ObservedObject var isConfirmed: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(scope.tradeName)
                .medicoText(textWeight: .semiBold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
            
            NonSeasonBoyImageAndAddressItem(previewItem: scope, onSubscribe: { })
            
            VStack(alignment: .leading, spacing: 5) {
                UserInfoItemDetailsPanel(titleKey: "gstin_number", valueKey: scope.gstin)
                UserInfoItemDetailsPanel(titleKey: "phone", valueKey: scope.phoneNumber)
            }
            
            HStack {
                let isConfirmed = Binding(get: { self.isConfirmed.value == true },
                                          set: { scope.changeConfirm(value: $0) })
                
                CheckBoxView(checked: isConfirmed)
                
                LocalizedText(localizationKey: "confirm_the_information",
                              color: .grey3,
                              multilineTextAlignment: .leading)
            }
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
        .padding(.top, 16)
        .modifier(SignUpButton(isEnabled: isConfirmed.value == true,
                               buttonTextKey: "add_retailer",
                               action: { scope.addRetailer() }))
    }
    
    init(scope: PreviewUserScope) {
        self.scope = scope
        
        self.isConfirmed = SwiftDataSource(dataSource: scope.isConfirmed)
    }
}
