//
//  NotificationsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 2.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct NotificationsScreen: View {
    @State private var notificationsSearch: NSString = ""
    
    var body: some View {
        VStack(spacing: 16) {
            SearchBar(placeholderLocalizationKey: "notifications",
                      searchText: notificationsSearch,
                      leadingButton: .init(emptyTextButton: .custom(AnyView(self.searchBarLeadingButton)),
                                           enteredTextButton: .smallMagnifyingGlass),
                      trailingButton: .init(emptyTextButton: .magnifyingGlass,
                                            enteredTextButton: .clear)) { newValue in
                notificationsSearch = newValue as NSString
            }
            
            VStack(alignment: .leading) {
                NotificationsSection(sectionLocalizationKey: "unread")
                
                AppColor.black.color.opacity(0.27)
                    .frame(height: 1)
                    .padding(.vertical, 3)
                
                NotificationsSection(sectionLocalizationKey: "read")
            }
            .scrollView()
        }
        .hideKeyboardOnTap()
        .padding(.horizontal, 16)
        .padding(.top, 32)
        .screenLogger(withScreenName: "NotificationsScreen",
                      withScreenClass: NotificationsScreen.self)
    }
    
    private var searchBarLeadingButton: some View {
        ZStack(alignment: .topTrailing) {
            Image("Bell")
                .resizable()
                .padding(2)
            
            Circle()
                .stroke(AppColor.white.color, lineWidth: 1.2)
                .background(Circle().fill(appColor: .red))
                .frame(width: 6, height: 6)
                .padding(.top, 5)
                .padding(.trailing, 3)
        }
    }
    
    private struct NotificationsSection: View {
        let sectionLocalizationKey: String
        
        var body: some View {
            VStack(alignment: .leading) {
                LocalizedText(localizationKey: sectionLocalizationKey,
                              textWeight: .medium,
                              fontSize: 16,
                              multilineTextAlignment: .leading)
                
                VStack(alignment: .leading, spacing: 13) {
                    VStack(alignment: .leading, spacing: 3) {
                        HStack {
                            Text("New request to subscribe from ABC Pharmacy")
                                .medicoText(textWeight: .semiBold,
                                            color: .lightBlue,
                                            multilineTextAlignment: .leading)
                            
                            Spacer()
                            
                            Text("1h")
                                .medicoText(color: .grey3,
                                            multilineTextAlignment: .leading)
                        }
                        
                        Text("Payment method: Invoice")
                            .medicoText(color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                    
                    HStack {
                        Spacer()
                        
                        MedicoButton(localizedStringKey: "Action Required",
                                     width: 150,
                                     height: 30,
                                     fontSize: 12,
                                     buttonColor: .navigationBar) {
                            print("clicked")
                        }
                    }
                }
                .padding(11)
                .background(AppColor.white.color.cornerRadius(5))
            }
        }
    }
}
