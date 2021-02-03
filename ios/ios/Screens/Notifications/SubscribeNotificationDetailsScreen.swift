//
//  SubscribeNotificationDetailsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 3.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct SubscribeNotificationDetailsScreen: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading) {
                LocalizedText(localizationKey: "new_request_to_subscribe_from",
                              textWeight: .medium,
                              fontSize: 20,
                              multilineTextAlignment: .leading)
            
                Text("ABC Pharmacy")
                    .medicoText(textWeight: .semiBold,
                                fontSize: 20,
                                multilineTextAlignment: .leading)
            }
            
            VStack(spacing: 45) {
                VStack(alignment: .leading, spacing: 12) {
                    HStack(alignment: .top, spacing: 35) {
                        URLImage(withURL: "", withDefaultImageName: "DefaultProduct")
                            .frame(width: 125, height: 125)
                        
                        VStack(alignment: .leading,spacing: 5) {
                            SmallAddresView(location: "Vijayawada", pincode: "520001")
                            
                            Text("25 km from you")
                                .medicoText(color: .lightBlue,
                                            multilineTextAlignment: .leading)
                            
                            LocalizedText(localizationKey: "see_on_the_map",
                                          textWeight: .bold,
                                          color: .lightBlue,
                                          multilineTextAlignment: .leading)
                        }
                    }
                    
                    HStack(alignment: .bottom) {
                        VStack(alignment: .leading, spacing: 5) {
                            getDataPanel(withTitleKey: "phone", withValueKey: "+123456789")
                            getDataPanel(withTitleKey: "gstin_number", withValueKey: "656462926")
                            getDataPanel(withTitleKey: "payment_method", withValueKey: "credit")
                        }
                        
                        Spacer()
                        
                        MedicoButton(localizedStringKey: "edit_credit",
                                     width: 95,
                                     height: 30,
                                     fontSize: 12,
                                     buttonColor: .navigationBar) {
                            
                        }
                    }
                }
                
                HStack {
                    MedicoButton(localizedStringKey: "decline",
                                 height: 35,
                                 fontSize: 12,
                                 fontWeight: .bold,
                                 buttonColor: .navigationBar) {
                        
                    }
                    
                    MedicoButton(localizedStringKey: "accept",
                                 height: 35,
                                 fontSize: 12,
                                 fontWeight: .bold) {
                        
                    }
                }
            }
            .padding(12)
            .background(AppColor.white.color.cornerRadius(5))
        }
        .padding(.horizontal, 16)
        .padding(.top, 32)
    }
    
    private func getDataPanel(withTitleKey titleKey: String,
                              withValueKey valueKey: String) -> some View {
        HStack(spacing: 3) {
            LocalizedText(localizationKey: titleKey,
                          multilineTextAlignment: .leading)
            
            LocalizedText(localizationKey: valueKey,
                          textWeight: .semiBold,
                          multilineTextAlignment: .leading)
        }
    }
}
