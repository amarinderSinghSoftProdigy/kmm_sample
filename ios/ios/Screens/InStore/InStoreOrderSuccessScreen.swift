//
//  InStoreOrderSuccessScreen.swift
//  Medico
//
//  Created by user on 08/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

//MARK: Main Screen
struct InStoreOrderSuccessScreen: View {
    
    let scope: InStoreOrderPlacedScope
    
    var body: some View {
        
        VStack(alignment: .center) {
            
            Spacer()
            
            VStack(alignment: .center, spacing: 40) {
                
                VStack(spacing: 5) {
                    
                    Image("OrderSuccess")
                        .resizable()
                        .frame(width: 80, height: 80)
                    LocalizedText(localizationKey: "instore_order_placed_success",
                                  textWeight: .semiBold,
                                  fontSize: 16,
                                  color: .darkBlue)
                    Text(scope.tradeName)
                        .medicoText(textWeight: .semiBold, fontSize: 16, color: .darkBlue)
                }
                
                MedicoButton(localizedStringKey: "instore_orders",
                             width: .infinity,
                             height: 45,
                             cornerRadius: 4,
                             fontSize: 15,
                             fontWeight: .bold,
                             fontColor: .white,
                             buttonColor: .blue,
                             buttonColorOpacity: 1,
                             action: { scope.goToOrders() })
                    .shadow(radius: 2)
               
            }
            .padding(30)
            .background(RoundedRectangle(cornerRadius: 6)
                            .fill(appColor: .lightGreen)
                            .opacity(0.2)
                            .overlay(
                                RoundedRectangle(cornerRadius: 6)
                                    .stroke(AppColor.green.color, lineWidth: 2)
                            ))
            
            Spacer()
        }
        .padding([.leading, .trailing], 30)
    }
    
    init(scope: InStoreOrderPlacedScope) {
        self.scope = scope
    }
}
